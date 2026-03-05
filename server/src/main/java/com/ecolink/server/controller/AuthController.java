package com.ecolink.server.controller;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.dto.auth.AuthResponse;
import com.ecolink.server.dto.auth.LoginRequest;
import com.ecolink.server.dto.auth.RegisterRequest;
import com.ecolink.server.dto.user.UserMeResponse;
import com.ecolink.server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/auth/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/users/me")
    public ApiResponse<UserMeResponse> me() {
        return ApiResponse.ok(authService.me());
    }
}
