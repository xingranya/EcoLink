package com.ecolink.server.dto.auth;

import com.ecolink.server.dto.user.UserMeResponse;

public record AuthResponse(
        String token,
        UserMeResponse user
) {}
