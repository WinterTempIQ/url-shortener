package com.github.wintertempiq.urlshortener.link;

import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import com.github.wintertempiq.urlshortener.link.entity.Link;
import com.github.wintertempiq.urlshortener.link.mapper.LinkMapper;
import com.github.wintertempiq.urlshortener.link.repository.LinkRepository;
import com.github.wintertempiq.urlshortener.link.service.LinkServiceImpl;
import com.github.wintertempiq.urlshortener.link.util.ShortCodeGenerator;
import com.github.wintertempiq.urlshortener.security.UserContext;
import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LinkServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private UserService userService;

    @Mock
    private ShortCodeGenerator generator;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkMapper linkMapper;

    @InjectMocks
    private LinkServiceImpl linkService;

    @Test
    void createLink_shouldSuccessful() {
        User user = new User();
        user.setEmail("bobo@bobo.bob");

        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://translate.yandex.ru/")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        String shortCode = "shortcode123";

        when(userContext.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userService.getUserEntityByEmail(user.getEmail())).thenReturn(user);
        when(generator.getShortCode()).thenReturn(shortCode);
        when(linkRepository.existsByShortCode(shortCode)).thenReturn(false);

        LinkShortDto expected = new LinkShortDto();
        expected.setOriginalUrl(request.getOriginalUrl());
        expected.setShortCode(shortCode);
        when(linkMapper.linkToLinkShortDto(any(Link.class))).thenReturn(expected);

        LinkShortDto dto = linkService.createLink(request);

        assertEquals("https://translate.yandex.ru/", dto.getOriginalUrl());
        assertEquals("shortcode123", dto.getShortCode());
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void createLink_shouldRetry_whenShortCodeCollision() {
        User user = new User();
        user.setEmail("bobo@bobo.bob");

        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://translate.yandex.ru/")
                .expiresAt(LocalDateTime.now())
                .build();

        String existingCode = "existing123";
        String newCode = "newcode456";

        when(userContext.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userService.getUserEntityByEmail(user.getEmail())).thenReturn(user);

        when(generator.getShortCode())
                .thenReturn(existingCode)   // попытка 1
                .thenReturn(existingCode)   // попытка 2
                .thenReturn(existingCode)   // попытка 3
                .thenReturn(newCode);       // попытка 4 - успех

        when(linkRepository.existsByShortCode(existingCode)).thenReturn(true);
        when(linkRepository.existsByShortCode(newCode)).thenReturn(false);

        LinkShortDto expected = new LinkShortDto();
        expected.setOriginalUrl(request.getOriginalUrl());
        expected.setShortCode(newCode);
        when(linkMapper.linkToLinkShortDto(any(Link.class))).thenReturn(expected);

        LinkShortDto result = linkService.createLink(request);

        assertEquals(newCode, result.getShortCode());
        assertEquals(request.getOriginalUrl(), result.getOriginalUrl());

        verify(linkRepository, times(4)).existsByShortCode(anyString());
        verify(linkRepository, times(1)).save(any(Link.class));
        verify(generator, times(4)).getShortCode(); // Без параметров
    }

    @Test
    void createLink_shouldThrow_whenAllRetriesExhausted() {
        User user = new User();
        user.setEmail("bobo@bobo.bob");

        CreateLinkRequest request = CreateLinkRequest.builder()
                .originalUrl("https://translate.yandex.ru/")
                .expiresAt(LocalDateTime.now())
                .build();

        String usedCode = "usedcode";

        when(userContext.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userService.getUserEntityByEmail(user.getEmail())).thenReturn(user);

        when(generator.getShortCode())
                .thenReturn(usedCode)
                .thenReturn(usedCode)
                .thenReturn(usedCode)
                .thenReturn(usedCode)
                .thenReturn(usedCode);

        when(linkRepository.existsByShortCode(usedCode)).thenReturn(true);

        String fallbackCode = "fallback";
        when(generator.getShortCode(8)).thenReturn(fallbackCode);
        when(linkRepository.existsByShortCode(fallbackCode)).thenReturn(true); // Даже fallback занят!

        assertThrows(IllegalStateException.class,
                () -> linkService.createLink(request));

        verify(linkRepository, times(6)).existsByShortCode(anyString());
        verify(linkRepository, never()).save(any(Link.class));
    }

    @Test
    void findLinksByUser_shouldSuccessful() {
        String email = "bobo@bob.bob";
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setEmail(email);

        Link link1 = new Link(user, "https://ya.ru/", "code1", null);
        Link link2 = new Link(user, "https://google.com/", "code2", null);

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(linkRepository.findAllByUser_Email(email, pageable))
                .thenReturn(new PageImpl<>(List.of(link1, link2), pageable, 2));

        LinkShortDto dto1 = new LinkShortDto();
        dto1.setOriginalUrl("https://ya.ru/");
        dto1.setShortCode("code1");

        LinkShortDto dto2 = new LinkShortDto();
        dto2.setOriginalUrl("https://google.com/");
        dto2.setShortCode("code2");

        when(linkMapper.linkToLinkShortDto(link1)).thenReturn(dto1);
        when(linkMapper.linkToLinkShortDto(link2)).thenReturn(dto2);

        Page<LinkShortDto> result = linkService.findLinksByUser(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("https://ya.ru/", result.getContent().get(0).getOriginalUrl());
        assertEquals("code1", result.getContent().get(0).getShortCode());
        assertEquals("https://google.com/", result.getContent().get(1).getOriginalUrl());
    }

    @Test
    void deleteLinkByShortCode_shouldThrow_whenLinkNotFound() {
        String email = "bob@bob.bo";
        String shortCode = "shortcode123";

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(linkRepository.deleteByShortCodeAndUser_Email(shortCode, email)).thenReturn(0L);

        assertThrows(NotFoundException.class,
                () -> linkService.deleteLinkByShortCode(shortCode));
    }

    @Test
    void deleteLinkByShortCode_shouldSuccessful_whenLinkExists() {
        String email = "bobo@bobo.bob";
        String shortCode = "shortcode123";

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(linkRepository.deleteByShortCodeAndUser_Email(shortCode, email))
                .thenReturn(1L); //

        linkService.deleteLinkByShortCode(shortCode);

        verify(linkRepository, times(1))
                .deleteByShortCodeAndUser_Email(shortCode, email);
        verify(linkRepository, never()).save(any(Link.class));
    }

    @Test
    void fullLinksInfoByShortCode_shouldThrow_whenLinkNotFound() {
        String email = "bob@bob.bo";
        String shortCode = "shortcode123";

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(linkRepository.findByShortCodeAndUser_Email(shortCode, email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> linkService.fullLinksInfoByShortCode(shortCode));
    }

    @Test
    void fullLinksInfoByShortCode_shouldReturn_LinkFullDto() {
        String email = "bob@bob.bo";
        String shortCode = "shortcode123";

        User user = new User();
        user.setEmail(email);

        Link link = new Link(user, "https://ya.ru/", shortCode, null);

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(linkRepository.findByShortCodeAndUser_Email(shortCode, email)).thenReturn(Optional.of(link));

        LinkFullDto dto = new LinkFullDto();
        dto.setShortCode(shortCode);
        dto.setOriginalUrl("https://ya.ru/");
        dto.setExpiresAt(null);
        dto.setClickCount(0L);
        dto.setCreatedAt(null);
        dto.setLastClickedAt(null);

        when(linkMapper.linkToLinkFullDto(link)).thenReturn(dto);

        LinkFullDto result = linkService.fullLinksInfoByShortCode(shortCode);

        assertEquals("https://ya.ru/", result.getOriginalUrl());
        assertEquals(shortCode, result.getShortCode());
        assertEquals(0L, result.getClickCount());
        verify(linkMapper).linkToLinkFullDto(link);
    }

}
