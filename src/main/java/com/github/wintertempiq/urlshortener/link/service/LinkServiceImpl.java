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

    private final UserContext userContext;
    private final UserService userService;
    private final ShortCodeGenerator generator;
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    @Override
    public LinkShortDto createLink(CreateLinkRequest request) {
        log.info("Creating link.");
        User user = userService.getUserEntityByEmail(userContext.getCurrentUserEmail());

        String shortCode = generator.getShortCode();

        while (linkRepository.existsByShortCode(shortCode)) {
            log.warn("Short code collision.");
            shortCode = generator.getShortCode();
        }

        Link link = new Link(user, request.getOriginalUrl(), shortCode);

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
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("Link not found"));

        LinkFullDto dto = linkMapper.linkToLinkFullDto(link);

        // Заглушки только если поля null (миграция еще не применена)
        if (dto.getClickCount() == null) {
            dto.setClickCount(0L);
        }
        // lastClickedAt и expiresAt оставляем как есть из БД (null — валидное значение)

        return dto;
    }

}
