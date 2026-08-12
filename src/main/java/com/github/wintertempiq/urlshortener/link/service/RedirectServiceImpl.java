package com.github.wintertempiq.urlshortener.link.service;

import com.github.wintertempiq.urlshortener.exceptions.LinkExpiredException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.link.entity.Link;
import com.github.wintertempiq.urlshortener.link.repository.LinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectServiceImpl implements RedirectService {

    private final LinkRepository linkRepository;

    @Override
    @Transactional
    public String resolveUrl(String shortCode) {
        log.info("Redirect requested for shortCode: {}", shortCode);

        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Link not found for shortCode: {}", shortCode);
                    return new NotFoundException("Link not found");
                });

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Link has expired: {}", shortCode);
            throw new LinkExpiredException("Link has expired.");
        }

        int updated = linkRepository.incrementClickCount(shortCode);
        log.info("incrementClickCount returned: {}", updated);

        log.info("Redirecting to: {}", link.getOriginalUrl());
        return link.getOriginalUrl();
    }
}
