package com.github.wintertempiq.urlshortener.auth;

import com.github.wintertempiq.urlshortener.auth.dto.LoginRequestDto;
import com.github.wintertempiq.urlshortener.auth.service.AuthenticationService;
import com.github.wintertempiq.urlshortener.exceptions.AuthenticationFailedException;
import com.github.wintertempiq.urlshortener.refreshtoken.service.RefreshTokenService;
import com.github.wintertempiq.urlshortener.security.JwtTokenProvider;
import com.github.wintertempiq.urlshortener.user.dto.JwtResponseDto;
import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_shouldThrow_whenUserNotFound() {
        LoginRequestDto dto = LoginRequestDto.builder()
                .email("bobo@boba.com")
                .password("123321")
                .build();

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class,
                () -> authenticationService.authenticate(dto));

        verify(jwtTokenProvider, never()).generateToken(any());
        verify(refreshTokenService, never()).createToken(any());
    }

    @Test
    void authenticate_shouldThrow_whenPasswordNotMatch() {
        LoginRequestDto dto = LoginRequestDto.builder()
                .email("bobo@boba.com")
                .password("123321")
                .build();

        User user = new User();
        user.setEmail("bobo@boba.com");
        user.setPassword("correctStoredHash123");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(AuthenticationFailedException.class,
                () -> authenticationService.authenticate(dto));

        verify(passwordEncoder).matches(dto.getPassword(), user.getPassword());
        verify(jwtTokenProvider, never()).generateToken(any());
        verify(refreshTokenService, never()).createToken(any());
    }

    @Test
    void authenticate_shouldSuccessful() {
        LoginRequestDto dto = LoginRequestDto.builder()
                .email("bobo@boba.com")
                .password("123321")
                .build();

        User user = new User();
        user.setEmail("bobo@boba.com");
        user.setPassword("correctStoredHash123");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtTokenProvider.generateToken(user.getEmail()))
                .thenReturn("tokensupertoken123");
        when(refreshTokenService.createToken(user))
                .thenReturn("refreshtoken2244token");

        JwtResponseDto result = authenticationService.authenticate(dto);

        assertEquals("tokensupertoken123", result.getToken());
        assertEquals("refreshtoken2244token", result.getRefreshToken());
        assertEquals("bobo@boba.com", result.getEmail());
    }

}
