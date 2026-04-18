package com.takehome.stayease.service.impl;

import com.takehome.stayease.dto.request.LoginRequest;
import com.takehome.stayease.dto.request.RegisterRequest;
import com.takehome.stayease.dto.response.AuthResponse;
import com.takehome.stayease.entity.User;
import com.takehome.stayease.exception.BadRequestException;
import com.takehome.stayease.exception.ResourceNotFoundException;
import com.takehome.stayease.repository.UserRepository;
import com.takehome.stayease.service.UserService;
import com.takehome.stayease.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  @Override
  public AuthResponse register(RegisterRequest request) {
    log.info("Registering new user with email: {}", request.getEmail());

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("User already exists with email: " + request.getEmail());
    }

    User.Role role = (request.getRole() != null) ? request.getRole() : User.Role.USER;

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(role)
        .build();

    userRepository.save(user);
    log.info("User registered successfully: {}", request.getEmail());

    UserDetails userDetails = buildUserDetails(user);
    String token = jwtUtil.generateToken(userDetails);
    return new AuthResponse(token);
  }

  @Override
  public AuthResponse login(LoginRequest request) {
    log.info("Login attempt for email: {}", request.getEmail());
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );
    } catch (BadCredentialsException e) {
      log.warn("Invalid credentials for email: {}", request.getEmail());
      throw new ResourceNotFoundException("Invalid credentials");
    }

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

    UserDetails userDetails = buildUserDetails(user);
    String token = jwtUtil.generateToken(userDetails);
    log.info("User logged in successfully: {}", request.getEmail());
    return new AuthResponse(token);
  }

  private UserDetails buildUserDetails(User user) {
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );
  }
}