package com.github.wintertempiq.urlshortener.refreshtoken.repository;

import com.github.wintertempiq.urlshortener.refreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying(clearAutomatically = true)
    @Query("""
                        DELETE FROM RefreshToken rt
                        WHERE rt.revoked = true OR rt.expiryDate < :now
            """)
    long deleteExpiredOrRevoked(@Param("now") LocalDateTime now);

}
