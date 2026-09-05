package com.github.wintertempiq.urlshortener.refreshtoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Request body to obtain a new access token using a refresh token")
public class RefreshTokenRequest {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "Valid refresh token")
    @NotBlank
    private String refreshToken;
}
