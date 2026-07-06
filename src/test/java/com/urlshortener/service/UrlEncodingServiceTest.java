package com.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UrlEncodingServiceTest {

    private final UrlEncodingService urlEncodingService = new UrlEncodingService();

    @Test
    void encodeZeroAndOneWorkCorrectly() {
        assertEquals("0", urlEncodingService.encode(0L));
        assertEquals("1", urlEncodingService.encode(1L));
    }

    @Test
    void encodeThenDecodeReturnsOriginalValue() {
        long originalId = 12345L;

        String shortCode = urlEncodingService.encode(originalId);

        assertEquals(originalId, urlEncodingService.decode(shortCode));
    }

    @Test
    void sequentialIdsProduceUniqueCodes() {
        Set<String> shortCodes = new HashSet<>();

        for (long id = 1; id <= 10_000; id++) {
            String shortCode = urlEncodingService.encode(id);
            assertTrue(shortCodes.add(shortCode), "Duplicate short code found for ID " + id);
        }

        assertEquals(10_000, shortCodes.size());
        assertNotEquals(urlEncodingService.encode(1L), urlEncodingService.encode(2L));
    }
}