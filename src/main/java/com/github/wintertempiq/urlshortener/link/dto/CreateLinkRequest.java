package com.github.wintertempiq.urlshortener.link.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateLinkRequest {
    @NotBlank(message = "URL cannot be empty")
    private String originalUrl;
}
