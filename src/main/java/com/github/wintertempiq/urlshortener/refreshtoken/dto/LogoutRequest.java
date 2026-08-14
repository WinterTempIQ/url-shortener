package com.github.wintertempiq.urlshortener.refreshtoken.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    @NotBlank
    private String refreshToken;
}
