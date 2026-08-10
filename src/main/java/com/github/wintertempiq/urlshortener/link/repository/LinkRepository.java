package com.github.wintertempiq.urlshortener.link.repository;

import com.github.wintertempiq.urlshortener.link.entity.Link;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Page<Link> findAllByUser_Email(String userEmail, Pageable pageable);

    @Modifying
    @Transactional
    long deleteByShortCodeAndUser_Email(String shortCode, String email);
}
