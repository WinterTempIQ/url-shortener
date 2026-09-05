package com.github.wintertempiq.urlshortener.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "Login credentials")
public class LoginRequestDto {

    @Schema(example = "user@example.com", description = "User email")
    @Email
    @NotBlank
    private String email;

    @Schema(example = "Qwerty234!", description = "Password")
    @NotBlank
    private String password;
}
