package com.github.wintertempiq.urlshortener.link.dto;

import com.github.wintertempiq.urlshortener.validation.HttpUrl;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Getter
public class CreateLinkRequest {
    @NotBlank(message = "URL cannot be empty")
    @Size(max = 2048)
    @HttpUrl
    private String originalUrl;

    @Future
    private LocalDateTime expiresAt;
}
