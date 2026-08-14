package com.github.wintertempiq.urlshortener.link.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateLinkRequest {
    @NotBlank(message = "URL cannot be empty")
    private String originalUrl;

    @Future
    private LocalDateTime expiresAt;
}
