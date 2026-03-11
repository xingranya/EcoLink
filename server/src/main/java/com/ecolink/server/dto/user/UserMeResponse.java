package com.ecolink.server.dto.user;

public record UserMeResponse(
        Long id,
        String username,
        String nickname,
        String phone,
        String role
) {}
