package com.github.wintertempiq.urlshortener.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDto {

    private String token;
    private String type = "Bearer";
    private String email;

    public JwtResponseDto(String token, String email) {
        this.token = token;
        this.email = email;
    }
}
