package com.urlshortener.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoLocationService {

    private static final Logger log = LoggerFactory.getLogger(GeoLocationService.class);

    private static final String API_URL = "http://ip-api.com/json/{ip}?fields=status,country,city";

    private final RestTemplate restTemplate = new RestTemplate();

    public GeoLocation resolve(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank() || isPrivateOrLoopback(ipAddress)) {
            return new GeoLocation("Local", "Local");
        }

        try {
            Map<String, Object> body = restTemplate.getForObject(API_URL, Map.class, ipAddress);
            if (body == null || !"success".equals(body.get("status"))) {
                return new GeoLocation("Unknown", "Unknown");
            }

            String country = (String) body.getOrDefault("country", "Unknown");
            String city = (String) body.getOrDefault("city", "Unknown");
            return new GeoLocation(country, city);
        } catch (Exception exception) {
            log.warn("Geo lookup failed for {}: {}", ipAddress, exception.getMessage());
            return new GeoLocation("Unknown", "Unknown");
        }
    }

    private boolean isPrivateOrLoopback(String ipAddress) {
        return ipAddress.equals("127.0.0.1")
                || ipAddress.equals("0:0:0:0:0:0:0:1")
                || ipAddress.equals("::1")
                || ipAddress.startsWith("192.168.")
                || ipAddress.startsWith("10.")
                || ipAddress.startsWith("172.16.");
    }

    public record GeoLocation(String country, String city) {
    }
}