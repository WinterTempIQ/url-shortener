package com.github.wintertempiq.urlshortener.auth.controller;

import com.github.wintertempiq.urlshortener.refreshtoken.dto.LogoutRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.RefreshTokenRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.TokenResponseDto;
import com.github.wintertempiq.urlshortener.refreshtoken.service.RefreshTokenService;
import com.github.wintertempiq.urlshortener.auth.service.AuthenticationService;
import com.github.wintertempiq.urlshortener.user.dto.JwtResponseDto;
import com.github.wintertempiq.urlshortener.auth.dto.LoginRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponseDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/refresh")
    public TokenResponseDto refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.refreshToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
    }
}
