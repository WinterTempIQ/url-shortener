package com.github.wintertempiq.urlshortener.link.service;

import com.github.wintertempiq.urlshortener.exceptions.LinkExpiredException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.link.entity.Link;
import com.github.wintertempiq.urlshortener.link.repository.LinkRepository;
import com.github.wintertempiq.urlshortener.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedirectServiceImplTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private RedirectServiceImpl redirectService;

    @Test
    void resolveUrl_shouldThrow_whenLinkNotFound() {
        String shortCode = "123312dsf";

        when(linkRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> redirectService.resolveUrl(shortCode));

        verify(linkRepository, times(1)).findByShortCode(shortCode);
        verify(linkRepository, never()).incrementClickCount(anyString());
    }

    @Test
    void resolveUrl_shouldThrow_whenLinkExpired() {
        String shortCode = "123312dsf";
        User user = new User();
        Link link = new Link(user, "https://ya.ru/", shortCode, LocalDateTime.now().minusDays(2));

        when(linkRepository.findByShortCode(shortCode)).thenReturn(Optional.of(link));

        assertThrows(LinkExpiredException.class, () -> redirectService.resolveUrl(shortCode));

        verify(linkRepository, times(1)).findByShortCode(shortCode);
        verify(linkRepository, never()).incrementClickCount(anyString());
    }

    @Test
    void resolveUrl_shouldReturnOriginalUrl_whenNoExpiration() {
        String shortCode = "123312dsf";
        User user = new User();
        Link link = new Link(user, "https://ya.ru/", shortCode, null);

        when(linkRepository.findByShortCode(shortCode)).thenReturn(Optional.of(link));
        when(linkRepository.incrementClickCount(shortCode)).thenReturn(1);

        String result = redirectService.resolveUrl(shortCode);

        assertEquals("https://ya.ru/", result);
        verify(linkRepository, times(1)).findByShortCode(shortCode);
        verify(linkRepository, times(1)).incrementClickCount(shortCode);
    }

    @Test
    void resolveUrl_shouldReturnOriginalUrl_whenNotYetExpired() {
        String shortCode = "123312dsf";
        User user = new User();
        Link link = new Link(user, "https://ya.ru/", shortCode, LocalDateTime.now().plusDays(1));

        when(linkRepository.findByShortCode(shortCode)).thenReturn(Optional.of(link));
        when(linkRepository.incrementClickCount(shortCode)).thenReturn(1);

        String result = redirectService.resolveUrl(shortCode);

        assertEquals("https://ya.ru/", result);
        verify(linkRepository, times(1)).findByShortCode(shortCode);
        verify(linkRepository, times(1)).incrementClickCount(shortCode);
    }
}
