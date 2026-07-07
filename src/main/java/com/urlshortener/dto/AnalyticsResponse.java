package com.urlshortener.dto;

import java.util.List;

public record AnalyticsResponse(
        String shortCode,
        String longUrl,
        long totalClicks,
        List<DailyClickCount> clicksByDay) {
}