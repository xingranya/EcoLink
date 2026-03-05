package com.ecolink.server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 4, max = 30, message = "用户名长度需在 4-30 之间")
        String username,
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 30, message = "密码长度需在 6-30 之间")
        String password,
        @NotBlank(message = "昵称不能为空")
        @Size(max = 30, message = "昵称长度不能超过 30")
        String nickname,
        String phone
) {}
