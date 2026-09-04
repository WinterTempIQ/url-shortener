package com.github.wintertempiq.urlshortener.link;

import com.github.wintertempiq.urlshortener.exceptions.LinkExpiredException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.link.controller.RedirectController;
import com.github.wintertempiq.urlshortener.link.service.RedirectService;
import com.github.wintertempiq.urlshortener.ratelimit.KeyExtractor;
import com.github.wintertempiq.urlshortener.ratelimit.RateLimiter;
import com.github.wintertempiq.urlshortener.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedirectService redirectService;

    @MockitoBean
    private RateLimiter rateLimiter;

    @MockitoBean
    private KeyExtractor keyExtractor;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void redirect_shouldReturn302AndLocationHeader() throws Exception {
        String shortCode = "123";
        String originalUrl = "https://ya.ru/";

        when(redirectService.resolveUrl(shortCode)).thenReturn(originalUrl);

        mockMvc.perform(get("/r/{shortCode}", shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl))
                .andExpect(redirectedUrl(originalUrl));

        verify(redirectService, times(1)).resolveUrl(shortCode);
    }

    @Test
    void redirect_shouldReturn404_whenServiceThrowsNotFound() throws Exception {
        String shortCode = "123";

        when(redirectService.resolveUrl(shortCode)).thenThrow(new NotFoundException("Link not found"));

        mockMvc.perform(get("/r/{shortCode}", shortCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_shouldReturn410_whenServiceThrowsLinkExpired() throws Exception {
        String shortCode = "123";

        when(redirectService.resolveUrl(shortCode)).thenThrow(new LinkExpiredException("Link has expired."));

        mockMvc.perform(get("/r/{shortCode}", shortCode))
                .andExpect(status().isGone());
    }
}
