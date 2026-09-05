package com.github.wintertempiq.urlshortener.refreshtoken.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Request body to log out and revoke a refresh token")
public class LogoutRequest {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "Refresh token to revoke")
    @NotBlank
    private String refreshToken;
}
