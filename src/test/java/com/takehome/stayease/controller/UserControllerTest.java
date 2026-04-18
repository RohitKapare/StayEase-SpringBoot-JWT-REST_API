package com.takehome.stayease.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takehome.stayease.dto.request.LoginRequest;
import com.takehome.stayease.dto.request.RegisterRequest;
import com.takehome.stayease.dto.response.AuthResponse;
import com.takehome.stayease.entity.User;
import com.takehome.stayease.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  @DisplayName("POST /api/users/register - Valid request returns JWT token")
  void registerValidRequestReturnsToken() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("test@example.com");
    request.setPassword("Test@123!");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setRole(User.Role.USER);

    when(userService.register(any(RegisterRequest.class)))
        .thenReturn(new AuthResponse("mock-jwt-token"));

    mockMvc.perform(post("/api/users/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mock-jwt-token"));
  }

  @Test
  @DisplayName("POST /api/users/register - Weak password returns 400 Bad Request")
  void register_InvalidPassword_ReturnsBadRequest() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("test@example.com");
    request.setPassword("weak");
    request.setFirstName("John");
    request.setLastName("Doe");

    mockMvc.perform(post("/api/users/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/users/login - Valid credentials return JWT token")
  void login_ValidCredentials_ReturnsToken() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");
    request.setPassword("Test@123!");

    when(userService.login(any(LoginRequest.class)))
        .thenReturn(new AuthResponse("mock-jwt-token"));

    mockMvc.perform(post("/api/users/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mock-jwt-token"));
  }
}