package com.github.wintertempiq.urlshortener.link.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import com.github.wintertempiq.urlshortener.link.service.LinkService;
import com.github.wintertempiq.urlshortener.ratelimit.KeyExtractor;
import com.github.wintertempiq.urlshortener.ratelimit.RateLimiter;
import com.github.wintertempiq.urlshortener.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinkController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LinkService linkService;

    @MockitoBean
    private RateLimiter rateLimiter;

    @MockitoBean
    private KeyExtractor keyExtractor;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createLink_shouldReturn201_whenValid() throws Exception {
        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://www.google.com")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        LinkShortDto dto = new LinkShortDto();
        dto.setOriginalUrl(request.getOriginalUrl());
        dto.setShortCode("abc123");
        dto.setCreatedAt(request.getExpiresAt());

        when(linkService.createLink(any(CreateLinkRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"));

        verify(linkService, times(1)).createLink(any(CreateLinkRequest.class));
    }

    @Test
    void createLink_shouldReturn400_whenUrlInvalid() throws Exception {
        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("")
                .expiresAt(null)
                .build();

        mockMvc.perform(post("/api/v1/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameters"));
    }

    @Test
    void getLinksByUser_shouldReturn200() throws Exception {
        LinkShortDto dto1 = new LinkShortDto();
        dto1.setShortCode("abc123");
        dto1.setOriginalUrl("https://www.google.com");
        dto1.setCreatedAt(LocalDateTime.now().minusHours(1));

        LinkShortDto dto2 = new LinkShortDto();
        dto2.setShortCode("def456");
        dto2.setOriginalUrl("https://www.youtube.com");
        dto2.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<LinkShortDto> links = List.of(dto1, dto2);

        Page<LinkShortDto> page = new PageImpl<>(links, PageRequest.of(0, 10), links.size());

        when(linkService.findLinksByUser(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/links")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].shortCode").value("abc123"))
                .andExpect(jsonPath("$.content[0].originalUrl").value("https://www.google.com"))
                .andExpect(jsonPath("$.content[1].shortCode").value("def456"))
                .andExpect(jsonPath("$.content[1].originalUrl").value("https://www.youtube.com"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getLink_shouldReturn404_whenNotFound() throws Exception {
        String shortCode = "123gbn";

        when(linkService.fullLinksInfoByShortCode(shortCode)).thenThrow(new NotFoundException("Link not found."));

        mockMvc.perform(get("/api/v1/links/{shortCode}", shortCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLink_shouldReturn200() throws Exception {
        String shortCode = "123gbh";
        LinkFullDto dto = new LinkFullDto();
        dto.setShortCode(shortCode);
        dto.setOriginalUrl("https://www.google.com");
        dto.setCreatedAt(LocalDateTime.now().minusHours(1));
        dto.setExpiresAt(LocalDateTime.now().plusHours(1));
        dto.setClickCount(2L);
        dto.setLastClickedAt(LocalDateTime.now().minusMinutes(10));

        when(linkService.fullLinksInfoByShortCode(shortCode)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/links/{shortCode}", shortCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value(shortCode))
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"))
                .andExpect(jsonPath("$.clickCount").value(2))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.expiresAt").exists())
                .andExpect(jsonPath("$.lastClickedAt").exists());

        verify(linkService, times(1)).fullLinksInfoByShortCode(shortCode);
    }

    @Test
    void deleteLinkByShortCode_shouldReturn204() throws Exception {
        String shortCode = "abc123";

        doNothing().when(linkService).deleteLinkByShortCode(shortCode);

        mockMvc.perform(delete("/api/v1/links/{shortCode}", shortCode))
                .andExpect(status().isNoContent());

        verify(linkService, times(1)).deleteLinkByShortCode(shortCode);
    }
}