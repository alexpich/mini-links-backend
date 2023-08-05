package com.apic.minilinks.controller;

import com.apic.minilinks.generator.UniqueIdGenerator;
import com.apic.minilinks.model.UrlDto;
import com.apic.minilinks.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author alexpich
 */
@Slf4j
@RestController
@RequestMapping(value = "/rest/url")
public class MiniLinksController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${redis.ttl}")
    private long ttl;

    @Autowired
    private RateLimiterService rateLimiterService;

    private final UniqueIdGenerator uniqueIdGenerator;

    @Autowired
    public MiniLinksController(UniqueIdGenerator uniqueIdGenerator) {
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody final String url,
            HttpServletRequest request) {

        // Using commons-validator library to validate the input URL.
        final UrlValidator urlValidator
                = new UrlValidator(new String[]{"http", "https"});
        if (!urlValidator.isValid(url)) {
            // Invalid url return HTTP 400 bad request.
            return ResponseEntity.badRequest().body("Invalid URL."
                    + " Please make sure the url has a proper prefix"
                    + " http:// or https://");
        }

        String ipAddress = getClientIpAddress(request);

        if (!rateLimiterService.allowRequest(ipAddress)) {
            System.out.println("\n****rate limit exceeded");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                    "Rate limit exceeded. Try again later."
            );
        }

        long uniqueId = uniqueIdGenerator.generateUniqueId();
        final String base62Key = UrlDto.encodeBase62(uniqueId);
        redisTemplate.opsForValue().set(base62Key, url);
        return ResponseEntity.ok(base62Key);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getUrl(@PathVariable final String id,
            HttpServletResponse response) throws IOException {
        // Get the URL string from the cache.
        final String url = redisTemplate.opsForValue().get(id);

        if (Objects.isNull(url)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Exception("No such key exists")
            );
        } else {
            log.info("URL retrieved = {}", url);
        }

        response.sendRedirect(url);
        return ResponseEntity.ok(url);
    }

    // Helper method to get the client's IP address from the request
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        System.out.println("\n"+ipAddress);
        return ipAddress;
    }

}
