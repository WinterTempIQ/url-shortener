package com.github.wintertempiq.urlshortener.link.service;

import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import com.github.wintertempiq.urlshortener.link.entity.Link;
import com.github.wintertempiq.urlshortener.link.mapper.LinkMapper;
import com.github.wintertempiq.urlshortener.link.repository.LinkRepository;
import com.github.wintertempiq.urlshortener.link.util.ShortCodeGenerator;
import com.github.wintertempiq.urlshortener.security.UserContext;
import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {
    private static final int MAX_SHORT_CODE_ATTEMPTS = 5;
    private static final int FALLBACK_CODE_LENGTH = 8;

    private final UserContext userContext;
    private final UserService userService;
    private final ShortCodeGenerator generator;
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    @Override
    public LinkShortDto createLink(CreateLinkRequest request) {
        log.info("Creating link.");
        User user = userService.getUserEntityByEmail(userContext.getCurrentUserEmail());

        String shortCode = generateUniqueShortCode();

        Link link = new Link(user, request.getOriginalUrl(), shortCode, request.getExpiresAt());

        linkRepository.save(link);

        log.info("Successful link creation.");
        return linkMapper.linkToLinkShortDto(link);
    }

    @Override
    public Page<LinkShortDto> findLinksByUser(Pageable pageable) {
        log.info("Getting all the user's links.");
        Page<Link> links = linkRepository.findAllByUser_Email(userContext.getCurrentUserEmail(), pageable);

        return links.map(linkMapper::linkToLinkShortDto);
    }

    @Override
    @Transactional
    public void deleteLinkByShortCode(String shortCode) {
        log.info("An attempt to delete a link.");

        String email = userContext.getCurrentUserEmail();

        long deleted = linkRepository.deleteByShortCodeAndUser_Email(
                shortCode,
                email
        );

        if (deleted == 0) {
            log.warn("Link not found or does not belong to current user.");
            throw new NotFoundException("Link not found.");
        }

        log.info("Successful deletion of link.");
    }

    @Override
    public LinkFullDto fullLinksInfoByShortCode(String shortCode) {
        log.info("An attempt to get full information about the link.");
        String email = userContext.getCurrentUserEmail();

        Link link = linkRepository.findByShortCodeAndUser_Email(shortCode, email)
                .orElseThrow(() -> {
                    log.warn("Link not found or access denied");
                    return new NotFoundException("Link not found.");
                });

        return linkMapper.linkToLinkFullDto(link);
    }

    private String generateUniqueShortCode() {
        for (int attempt = 1; attempt <= MAX_SHORT_CODE_ATTEMPTS; attempt++) {
            String candidate = generator.getShortCode();
            if (!linkRepository.existsByShortCode(candidate)) {
                return candidate;
            }
            log.warn("Short code collision on attempt {}/{}", attempt, MAX_SHORT_CODE_ATTEMPTS);
        }
        String fallback = generator.getShortCode(FALLBACK_CODE_LENGTH);

        if (linkRepository.existsByShortCode(fallback)) {
            log.error("Short code generation failed after retries");
            throw new IllegalStateException("Short code generation failed after retries");
        }
        return fallback;
    }

}
