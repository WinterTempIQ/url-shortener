package com.github.wintertempiq.urlshortener.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wintertempiq.urlshortener.auth.controller.AuthController;
import com.github.wintertempiq.urlshortener.auth.dto.LoginRequestDto;
import com.github.wintertempiq.urlshortener.auth.service.AuthenticationService;
import com.github.wintertempiq.urlshortener.exceptions.AuthenticationFailedException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.ratelimit.KeyExtractor;
import com.github.wintertempiq.urlshortener.ratelimit.RateLimiter;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.LogoutRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.RefreshTokenRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.TokenResponseDto;
import com.github.wintertempiq.urlshortener.refreshtoken.service.RefreshTokenService;
import com.github.wintertempiq.urlshortener.security.JwtTokenProvider;
import com.github.wintertempiq.urlshortener.user.dto.JwtResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private RateLimiter rateLimiter;

    @MockitoBean
    private KeyExtractor keyExtractor;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void login_shouldReturn200_whenValid() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("email@email.mail")
                .password("password")
                .build();

        JwtResponseDto responseDto = new JwtResponseDto(
                "token228",
                "refreshtoken144",
                requestDto.getEmail());

        when(authenticationService.authenticate(any(LoginRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token228"))
                .andExpect(jsonPath("$.refreshToken").value("refreshtoken144"))
                .andExpect(jsonPath("$.email").value("email@email.mail"));

        verify(authenticationService, times(1)).authenticate(any(LoginRequestDto.class));
    }

    @Test
    void login_shouldReturn401_whenInvalidCredentials() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("email@email.mail")
                .password("password")
                .build();

        when(authenticationService.authenticate(any(LoginRequestDto.class)))
                .thenThrow(new AuthenticationFailedException("Invalid email or password"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        verify(authenticationService, times(1)).authenticate(any(LoginRequestDto.class));
    }

    @Test
    void login_shouldReturn400_whenRequestInvalid() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email("")
                .password("password")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_shouldReturnTokenResponseDto() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("123refresh123")
                .build();

        TokenResponseDto response = new TokenResponseDto(
                "123accessToken123",
                "123refresh123"
        );

        when(refreshTokenService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("123accessToken123"))
                .andExpect(jsonPath("$.refreshToken").value("123refresh123"));

        verify(refreshTokenService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void refreshToken_shouldReturn404_whenRefreshTokenNotFound() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("123refresh123")
                .build();

        when(refreshTokenService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new NotFoundException("Token not found."));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Token not found."));

        verify(refreshTokenService, times(1))
                .refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void refreshToken_shouldReturn400_whenRefreshTokenIsEmpty() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("")
                .build();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameters"));
    }

    @Test
    void logout_shouldReturn204() throws Exception {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("123trewfds123")
                .build();

        doNothing().when(refreshTokenService).revokeToken(request.getRefreshToken());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(refreshTokenService, times(1)).revokeToken(request.getRefreshToken());
    }

    @Test
    void logout_shouldReturn400_whenRefreshTokenIsEmpty() throws Exception {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("")
                .build();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameters"));

    }
}
