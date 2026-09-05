package com.github.wintertempiq.urlshortener.link.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Full details about a shortened link")
public class LinkFullDto {

    @Schema(example = "aB3xK9z", description = "Short code of the link")
    private String shortCode;

    @Schema(example = "https://example.com/some/long/path", description = "Original URL")
    private String originalUrl;

    @Schema(example = "2026-01-01T10:00:00", description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(example = "42", description = "Number of times the link has been clicked")
    private Long clickCount;

    @Schema(example = "2026-02-01T15:30:00", description = "Timestamp of the last click")
    private LocalDateTime lastClickedAt;

    @Schema(example = "2026-12-31T23:59:59", description = "Expiration timestamp")
    private LocalDateTime expiresAt;
}
