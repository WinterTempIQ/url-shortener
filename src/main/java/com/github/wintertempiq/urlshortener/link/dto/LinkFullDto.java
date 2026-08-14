package com.github.wintertempiq.urlshortener.link.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LinkFullDto {
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private Long clickCount;
    private LocalDateTime lastClickedAt;
    private LocalDateTime expiresAt;
}
