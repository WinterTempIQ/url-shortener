package com.github.wintertempiq.urlshortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "JWT pair returned after successful authentication")
public class JwtResponseDto {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "JWT access token")
    private String token;

    @Schema(example = "Bearer", description = "Token type (always Bearer)")
    private String type = "Bearer";

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "Refresh token used to obtain a new access token")
    private String refreshToken;

    @Schema(example = "user@example.com", description = "Email of the authenticated user")
    private String email;

    public JwtResponseDto(String token, String refreshToken, String email) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
