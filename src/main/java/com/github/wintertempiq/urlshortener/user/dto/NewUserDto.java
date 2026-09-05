package com.github.wintertempiq.urlshortener.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Data required to register a new user")
public class NewUserDto {

    @Schema(example = "user@example.com", description = "User email address")
    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    @Schema(example = "Qwerty234!", description = "Password, at least 8 characters long")
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
}
