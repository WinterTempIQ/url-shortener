package com.github.wintertempiq.urlshortener.link.dto;

import com.github.wintertempiq.urlshortener.validation.HttpUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Request body to create a shortened link")
public class CreateLinkRequest {

    @Schema(example = "https://example.com/some/long/path", description = "Original URL to shorten")
    @NotBlank(message = "URL cannot be empty")
    @Size(max = 2048)
    @HttpUrl
    private String originalUrl;

    @Schema(example = "2026-12-31T23:59:59", description = "Optional expiration date and time")
    @Future
    private LocalDateTime expiresAt;
}
