package com.takehome.stayease.service;

import com.takehome.stayease.dto.request.LoginRequest;
import com.takehome.stayease.dto.request.RegisterRequest;
import com.takehome.stayease.dto.response.AuthResponse;

public interface UserService {

  AuthResponse register(RegisterRequest request);

  AuthResponse login(LoginRequest request);
}