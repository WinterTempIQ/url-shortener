package com.github.wintertempiq.urlshortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Public user profile information")
public class UserDto {

    @Schema(example = "1", description = "Unique user identifier")
    private Long id;

    @Schema(example = "user@example.com", description = "User email address")
    private String email;
}
