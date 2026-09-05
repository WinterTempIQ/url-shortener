package com.github.wintertempiq.urlshortener.refreshtoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "New token pair returned on refresh")
public class TokenResponseDto {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "New JWT access token")
    private String accessToken;

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "New refresh token")
    private String refreshToken;
}
