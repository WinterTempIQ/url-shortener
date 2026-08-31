package com.github.wintertempiq.urlshortener.refreshtoken.cleanup;

import com.github.wintertempiq.urlshortener.refreshtoken.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredOrRevokedTokenCleanup {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        long deleted = refreshTokenRepository.deleteExpiredOrRevoked(now);
        if (deleted > 0) log.info("Deleted {} expired/revoked tokens", deleted);
    }
}
