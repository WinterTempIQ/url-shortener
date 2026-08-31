package com.github.wintertempiq.urlshortener.link.cleanup;

import com.github.wintertempiq.urlshortener.link.repository.LinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredLinkCleanup {
    private final LinkRepository linkRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        long deleted = linkRepository.deleteExpired(now);
        if (deleted > 0) log.info("Deleted {} expired links", deleted);
    }
}
