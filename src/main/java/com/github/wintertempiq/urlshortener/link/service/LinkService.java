package com.github.wintertempiq.urlshortener.link.service;

import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkService {

    LinkShortDto createLink(CreateLinkRequest request);

    Page<LinkShortDto> findLinksByUser(Pageable pageable);

    void deleteLinkByShortCode(String shortCode);

    LinkFullDto fullLinksInfoByShortCode(String shortCode);
}
