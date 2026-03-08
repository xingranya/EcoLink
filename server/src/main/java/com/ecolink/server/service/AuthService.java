package com.ecolink.server.service;

import com.ecolink.server.domain.User;
import com.ecolink.server.domain.enums.UserStatus;
import com.ecolink.server.dto.auth.AuthResponse;
import com.ecolink.server.dto.auth.LoginRequest;
import com.ecolink.server.dto.auth.RegisterRequest;
import com.ecolink.server.dto.user.UserMeResponse;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.UserRepository;
import com.ecolink.server.security.JwtTokenProvider;
import com.ecolink.server.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BizException(4002, "用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setPhone(request.phone());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, toUserMe(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new BizException(4003, "账号或密码错误"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BizException(4004, "账号已禁用");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BizException(4003, "账号或密码错误");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, toUserMe(user));
    }

    public UserMeResponse me() {
        long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new BizException(4040, "用户不存在"));
        return toUserMe(user);
    }

    public User getCurrentUserEntity() {
        long userId = SecurityUtils.currentUserId();
        return userRepository.findById(userId).orElseThrow(() -> new BizException(4040, "用户不存在"));
    }

    private UserMeResponse toUserMe(User user) {
        return new UserMeResponse(user.getId(), user.getUsername(), user.getNickname(), user.getPhone());
    }
}
