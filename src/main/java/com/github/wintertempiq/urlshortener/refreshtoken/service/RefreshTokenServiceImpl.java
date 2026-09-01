package com.github.wintertempiq.urlshortener.refreshtoken.service;

import com.github.wintertempiq.urlshortener.exceptions.AuthenticationFailedException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.RefreshTokenRequest;
import com.github.wintertempiq.urlshortener.refreshtoken.dto.TokenResponseDto;
import com.github.wintertempiq.urlshortener.refreshtoken.entity.RefreshToken;
import com.github.wintertempiq.urlshortener.refreshtoken.repository.RefreshTokenRepository;
import com.github.wintertempiq.urlshortener.security.JwtTokenProvider;
import com.github.wintertempiq.urlshortener.security.TokenGenerator;
import com.github.wintertempiq.urlshortener.security.TokenHasher;
import com.github.wintertempiq.urlshortener.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;

    @Override
    public String createToken(User user) {
        log.info("Creating refresh token for user: {}", user.getId());
        return createAndSaveToken(user);
    }

    @Override
    @Transactional
    public TokenResponseDto refreshToken(RefreshTokenRequest request) {
        log.info("Attempting to refresh token");

        String hashToken = tokenHasher.hash(request.getRefreshToken());

        RefreshToken token = refreshTokenRepository.findByToken(hashToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found");
                    return new NotFoundException("Token not find.");
                });

        if (token.isRevoked()) {
            log.warn("Attempt to use revoked refresh token for user: {}", token.getUser().getId());
            throw new AuthenticationFailedException("Refresh token revoked.");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Expired refresh token for user: {}", token.getUser().getId());
            throw new AuthenticationFailedException("Refresh token expired.");
        }

        User user = token.getUser();
        log.info("Valid refresh token for user: {}", user.getId());

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newRefreshToken = createAndSaveToken(user);

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());

        log.info("Tokens refreshed successfully for user: {}", user.getId());
        return new TokenResponseDto(accessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void revokeToken(String refreshToken) {
        log.info("Attempting to revoke refresh token");
        String tokenHash = tokenHasher.hash(refreshToken);

        RefreshToken token = refreshTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found during revocation");
                    return new NotFoundException("Refresh token not found.");
                });

        token.setRevoked(true);

        refreshTokenRepository.save(token);
        log.info("Refresh token revoked successfully for user: {}", token.getUser().getId());
    }

    private String createAndSaveToken(User user) {
        String token = tokenGenerator.generate();
        String hash = tokenHasher.hash(token);

        RefreshToken entity = new RefreshToken();
        entity.setToken(hash);
        entity.setUser(user);
        entity.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        entity.setRevoked(false);

        refreshTokenRepository.save(entity);

        log.debug("Refresh token saved for user: {}", user.getId());
        return token;
    }
}
