package com.github.wintertempiq.urlshortener.refreshtoken.service;

import com.github.wintertempiq.urlshortener.refreshtoken.dto.RefreshTokenRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.TokenResponseDto;
import com.github.wintertempiq.urlshortener.user.entity.User;

public interface RefreshTokenService {

    String createToken(User user);

    TokenResponseDto refreshToken(RefreshTokenRequest request);

    void revokeToken(String refreshToken);
}
