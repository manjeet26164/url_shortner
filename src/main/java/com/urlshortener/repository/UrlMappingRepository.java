package com.urlshortener.repository;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Optional<UrlMapping> findByCustomAlias(String alias);

    boolean existsByCustomAlias(String alias);

    List<UrlMapping> findByUserOrderByCreatedAtDesc(User user);
}