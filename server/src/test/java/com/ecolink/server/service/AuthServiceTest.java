package com.ecolink.server.service;

import com.ecolink.server.domain.User;
import com.ecolink.server.domain.enums.UserStatus;
import com.ecolink.server.dto.auth.LoginRequest;
import com.ecolink.server.dto.auth.RegisterRequest;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.UserRepository;
import com.ecolink.server.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("demoUser", "123456", "演示用户", "13800000000");
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        var response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().username()).isEqualTo("demoUser");
        assertThat(response.user().nickname()).isEqualTo("演示用户");
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegisterRequest request = new RegisterRequest("demoUser", "123456", "演示用户", "13800000000");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        BizException exception = assertThrows(BizException.class, () -> authService.register(request));

        assertThat(exception.getCode()).isEqualTo(4002);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnTokenWhenPasswordMatches() {
        User user = new User();
        user.setId(2L);
        user.setUsername("demo");
        user.setNickname("普通用户");
        user.setPasswordHash("encoded-password");
        user.setRole("USER");
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(2L, "demo", "USER")).thenReturn("jwt-token");

        var response = authService.login(new LoginRequest("demo", "123456"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().username()).isEqualTo("demo");
    }

    @Test
    void loginShouldRejectWrongPassword() {
        User user = new User();
        user.setId(2L);
        user.setUsername("demo");
        user.setPasswordHash("encoded-password");
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad-password", "encoded-password")).thenReturn(false);

        BizException exception = assertThrows(BizException.class, () -> authService.login(new LoginRequest("demo", "bad-password")));

        assertThat(exception.getCode()).isEqualTo(4003);
    }
}
