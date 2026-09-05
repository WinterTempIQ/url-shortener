package com.github.wintertempiq.urlshortener.auth.controller;

import com.github.wintertempiq.urlshortener.auth.dto.LoginRequestDto;
import com.github.wintertempiq.urlshortener.auth.service.AuthenticationService;
import com.github.wintertempiq.urlshortener.exceptions.ApiError;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.LogoutRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.RefreshTokenRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.TokenResponseDto;
import com.github.wintertempiq.urlshortener.refreshtoken.service.RefreshTokenService;
import com.github.wintertempiq.urlshortener.user.dto.JwtResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, token refresh and logout")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Authenticate a user",
            description = "Validates credentials and returns an access token pair",
            security = {})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, tokens issued",
                    content = @Content(schema = @Schema(implementation = JwtResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public JwtResponseDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
            description = "Exchanges a valid refresh token for a new token pair")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public TokenResponseDto refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.refreshToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Log out a user",
            description = "Revokes the provided refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void logout(@Valid @RequestBody LogoutRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
    }
}