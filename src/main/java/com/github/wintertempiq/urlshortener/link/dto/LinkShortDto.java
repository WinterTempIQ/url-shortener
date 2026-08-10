package com.github.wintertempiq.urlshortener.link.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LinkShortDto {
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
}
