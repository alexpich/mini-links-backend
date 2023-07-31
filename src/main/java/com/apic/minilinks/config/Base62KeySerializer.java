package com.apic.minilinks.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author alex
 */
public class Base62KeySerializer implements RedisSerializer<String> {

    @Override
    public byte[] serialize(String key) throws SerializationException {
        // Convert the base62 encoded key to bytes using UTF-8 encoding
        return key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        // Convert the bytes to a base62 encoded string using UTF-8 encoding
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
