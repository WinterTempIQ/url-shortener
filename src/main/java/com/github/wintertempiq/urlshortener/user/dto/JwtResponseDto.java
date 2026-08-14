package com.github.wintertempiq.urlshortener.user.dto;

import lombok.Getter;

@Getter
public class JwtResponseDto {

    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private String email;


    public JwtResponseDto(String token, String refreshToken, String email) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
