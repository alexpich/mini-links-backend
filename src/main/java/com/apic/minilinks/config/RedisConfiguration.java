/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apic.minilinks.config;

import com.apic.minilinks.model.UrlDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *
 * @author alexpich
 */
@Configuration
public class RedisConfiguration {
        @Autowired
    private ObjectMapper objectMapper;
 
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
 
    // Setting up the Redis template object.
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public RedisTemplate<String, UrlDto> redisTemplate() {
        final Jackson2JsonRedisSerializer jackson2JsonRedisSerializer 
                = new Jackson2JsonRedisSerializer(UrlDto.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
 
        final RedisTemplate<String, UrlDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }
}
