package com.takehome.stayease.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takehome.stayease.config.JwtAuthFilter;
import com.takehome.stayease.dto.request.LoginRequest;
import com.takehome.stayease.dto.request.RegisterRequest;
import com.takehome.stayease.dto.response.AuthResponse;
import com.takehome.stayease.entity.User;
import com.takehome.stayease.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  UserService userService;
  @MockBean
  JwtAuthFilter jwtAuthFilter;
  @MockBean
  UserDetailsService userDetailsService;

  @Test
  void register_validRequest_returnsToken() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("test@example.com");
    request.setPassword("Test@123!");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setRole(User.Role.USER);

    when(userService.register(any(RegisterRequest.class)))
        .thenReturn(new AuthResponse("mock-jwt-token"));

    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mock-jwt-token"));
  }

  @Test
  void login_validRequest_returnsToken() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("Test@123!");

    when(userService.login(any(LoginRequest.class)))
        .thenReturn(new AuthResponse("mock-jwt-token"));

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mock-jwt-token"));
  }

  @Test
  void register_invalidPassword_returnsBadRequest() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("test@example.com");
    request.setPassword("weak");
    request.setFirstName("John");
    request.setLastName("Doe");

    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}