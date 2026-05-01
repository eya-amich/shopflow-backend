package com.shopflow.shopflow.service;

import com.shopflow. shopflow.dto.request.LoginRequest;
import com.shopflow. shopflow.dto.request.RefreshTokenRequest;
import com.shopflow. shopflow. dto.request.RegisterRequest;
import com.shopflow. shopflow. dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String email);
}
