package com.github.wintertempiq.urlshortener.link.dto;

import com.github.wintertempiq.urlshortener.validation.HttpUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@Builder
@Jacksonized
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

    @Schema(
            example = "my-custom-link",
            description = "Optional custom short code/alias. 3-30 chars: latin letters, "
                    + "digits, '-' or '_'. Leave empty to auto-generate."
    )
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,30}$", message = "Alias must be 3-30 chars: letters, digits, - or _")
    private String alias;
}
