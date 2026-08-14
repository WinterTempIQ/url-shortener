package com.github.wintertempiq.urlshortener.link.controller;

import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import com.github.wintertempiq.urlshortener.link.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LinkShortDto createLink(@Valid @RequestBody CreateLinkRequest request) {
        return linkService.createLink(request);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<LinkShortDto> getLinksByUser(Pageable pageable) {
        return linkService.findLinksByUser(pageable);
    }

    @GetMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.OK)
    public LinkFullDto getLink(@PathVariable String shortCode) {
        return linkService.fullLinksInfoByShortCode(shortCode);
    }

    @DeleteMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLinkByShortCode(@PathVariable String shortCode) {
        linkService.deleteLinkByShortCode(shortCode);
    }


}
