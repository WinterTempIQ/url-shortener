package com.github.wintertempiq.urlshortener.link.entity;

import com.github.wintertempiq.urlshortener.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "links")
@NoArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Long clickCount;

    private LocalDateTime lastClickedAt;

    public Link(User user, String originalUrl, String shortCode) {
        this.user = user;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }
}
