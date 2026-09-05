package com.github.wintertempiq.urlshortener.link.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Brief information about a shortened link")
public class LinkShortDto {

    @Schema(example = "aB3xK9z", description = "Short code of the link")
    private String shortCode;

    @Schema(example = "https://example.com/some/long/path", description = "Original URL")
    private String originalUrl;

    @Schema(example = "2026-01-01T10:00:00", description = "Creation timestamp")
    private LocalDateTime createdAt;
}
