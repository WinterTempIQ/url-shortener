package com.github.wintertempiq.urlshortener.link.repository;

import com.github.wintertempiq.urlshortener.link.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<Link> findAllByUser_Id(Long userId);
}
