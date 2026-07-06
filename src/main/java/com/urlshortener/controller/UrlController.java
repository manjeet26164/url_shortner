package com.urlshortener.controller;

import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.entity.UrlMapping;
import com.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request,
                                                      HttpServletRequest httpServletRequest) {
        UrlMapping mapping = urlService.createShortUrl(request.longUrl(), request.customAlias(), request.expiresAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(mapping, httpServletRequest));
    }

    @GetMapping
    public ResponseEntity<List<UrlResponse>> getUserUrls(HttpServletRequest request) {
        List<UrlResponse> response = urlService.getUserUrls().stream()
                .map(mapping -> toResponse(mapping, request))
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        urlService.deleteShortUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    private UrlResponse toResponse(UrlMapping mapping, HttpServletRequest request) {
        String scheme = request.getScheme();
        String baseUrl = scheme + "://" + request.getServerName();
        int serverPort = request.getServerPort();
        boolean standardPort = ("http".equalsIgnoreCase(scheme) && serverPort == 80)
                || ("https".equalsIgnoreCase(scheme) && serverPort == 443);
        if (!standardPort) {
            baseUrl = baseUrl + ":" + serverPort;
        }

        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank()) {
            baseUrl = baseUrl + contextPath;
        }

        String fullShortUrl = baseUrl + "/" + mapping.getShortCode();

        return new UrlResponse(
                mapping.getId(),
                mapping.getShortCode(),
                mapping.getLongUrl(),
                fullShortUrl,
                mapping.getCreatedAt(),
                mapping.getExpiresAt(),
                mapping.getClickCount());
    }

}