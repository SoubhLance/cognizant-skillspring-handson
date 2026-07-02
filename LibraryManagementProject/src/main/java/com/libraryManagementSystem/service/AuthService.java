package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.*;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    UserDto registerUser(RegisterRequest registerRequest);
    JwtResponse refreshAccessToken(TokenRefreshRequest refreshRequest);
    void logoutUser();
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void verifyEmail(String token);
}
