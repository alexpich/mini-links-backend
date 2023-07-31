package com.apic.minilinks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author alexpich
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    private String id;
    private String url;
    private LocalDateTime created;

    public static UrlDto create(final String url, final long uniqueId) {
        final String id = encodeBase62(uniqueId);
        return new UrlDto(id, url, LocalDateTime.now());
    }

    // Helper method to convert a long value to base62 format
    public static String encodeBase62(long value) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int base = characters.length();
        StringBuilder sb = new StringBuilder();

        while (value > 0) {
            sb.append(characters.charAt((int) (value % base)));
            value /= base;
        }

        return sb.reverse().toString();
    }
}
