package com.urlshortener.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UrlEncodingService {

    private static final char[] BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int BASE = BASE62_ALPHABET.length;
    private static final Map<Character, Integer> REVERSE_LOOKUP = new HashMap<>();

    static {
        for (int index = 0; index < BASE62_ALPHABET.length; index++) {
            REVERSE_LOOKUP.put(BASE62_ALPHABET[index], index);
        }
    }

    public String encode(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("ID must not be negative");
        }

        if (id == 0) {
            return String.valueOf(BASE62_ALPHABET[0]);
        }

        StringBuilder encoded = new StringBuilder();
        long value = id;

        while (value > 0) {
            // Divide by 62 to move to the next digit in base62.
            // The remainder tells us which character belongs in the current position.
            int remainder = (int) (value % BASE);
            encoded.append(BASE62_ALPHABET[remainder]);
            value = value / BASE;
        }

        // Digits are collected from least significant to most significant,
        // so reverse the string before returning the final short code.
        return encoded.reverse().toString();
    }

    public Long decode(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new IllegalArgumentException("Short code must not be blank");
        }

        long decoded = 0L;

        for (char character : shortCode.toCharArray()) {
            Integer digit = REVERSE_LOOKUP.get(character);
            if (digit == null) {
                throw new IllegalArgumentException("Invalid Base62 character: " + character);
            }

            // Multiply by 62 to shift the previous digits left one base62 place,
            // then add the numeric value of the current character.
            decoded = (decoded * BASE) + digit;
        }

        return decoded;
    }
}