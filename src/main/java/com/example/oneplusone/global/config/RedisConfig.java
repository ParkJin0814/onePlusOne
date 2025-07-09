package com.example.oneplusone.global.config;

import com.example.oneplusone.domain.product.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public RedisTemplate<String, Page<Product>> productPageRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Page<Product>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Page 직렬화 사전작업
        SimpleModule module = new SimpleModule().addDeserializer(PageImpl.class, new PageImplDeserializer());
        ObjectMapper objectMapper = new ObjectMapper().registerModule(module).registerModule(new JavaTimeModule());

        // Key는 String, Value는 Page 형식으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, PageImpl.class));
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Page 직렬화 사전작업
        SimpleModule module = new SimpleModule().addDeserializer(PageImpl.class, new PageImplDeserializer());
        ObjectMapper objectMapper = new ObjectMapper().registerModule(module).registerModule(new JavaTimeModule());

        // 키 직렬화 설정: String 직렬화 사용
        RedisSerializationContext.SerializationPair<String> keySerializer = RedisSerializationContext
                .SerializationPair
                .fromSerializer(new StringRedisSerializer());

        // 값 직렬화 설정: PageImpl 직렬화 사용
        RedisSerializationContext.SerializationPair<PageImpl> valueSerializer = RedisSerializationContext
                .SerializationPair
                .fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, PageImpl.class));

        // RedisCacheConfiguration에 직렬화 방식 설정
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keySerializer)  // 키 직렬화 설정
                .serializeValuesWith(valueSerializer)  // 값 직렬화 설정
                .entryTtl(Duration.ofMinutes(30)); // 캐시 시간 설정

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}