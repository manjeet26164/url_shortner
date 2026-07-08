package com.urlshortener.service;

import com.urlshortener.dto.AnalyticsResponse;
import com.urlshortener.dto.DailyClickCount;
import com.urlshortener.entity.UrlMapping;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.repository.ClickEventRepository;
import com.urlshortener.repository.UrlMappingRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final ClickEventRepository clickEventRepository;
    private final UrlMappingRepository urlMappingRepository;
    private final UrlService urlService;

    public AnalyticsService(ClickEventRepository clickEventRepository,
                             UrlMappingRepository urlMappingRepository,
                             UrlService urlService) {
        this.clickEventRepository = clickEventRepository;
        this.urlMappingRepository = urlMappingRepository;
        this.urlService = urlService;
    }

    public AnalyticsResponse getAnalytics(String shortCode) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
        urlService.assertOwnership(mapping);

        List<Object[]> rawResults = clickEventRepository.countClicksGroupedByDay(mapping.getId());

        List<DailyClickCount> clicksByDay = rawResults.stream()
                .map(row -> {
                    LocalDate date = ((Date) row[0]).toLocalDate();
                    long count = ((Number) row[1]).longValue();
                    return new DailyClickCount(date, count);
                })
                .collect(Collectors.toList());

        long totalClicks = mapping.getClickCount() == null ? 0L : mapping.getClickCount();

        return new AnalyticsResponse(mapping.getShortCode(), mapping.getLongUrl(), totalClicks, clicksByDay);
    }
}