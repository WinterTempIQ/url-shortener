package com.github.wintertempiq.urlshortener.refreshtoken.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequest {
    @NotBlank
    private String refreshToken;
}
