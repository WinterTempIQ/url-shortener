package com.github.wintertempiq.urlshortener.refreshtoken.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
