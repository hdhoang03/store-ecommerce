package com.example.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisService {
    RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, Object value, long timeOutInMinutes){
        redisTemplate.opsForValue().set(key, value, timeOutInMinutes, TimeUnit.MINUTES);
    }

    public <T> T getValue(String key, Class<T> clazz){
        Object raw = redisTemplate.opsForValue().get(key);
        return raw == null ? null : new ObjectMapper().convertValue(raw, clazz);
//        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key){
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
