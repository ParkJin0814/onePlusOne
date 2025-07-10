package com.example.oneplusone.global.config;

import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisManagerConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // <String, String>
        RedisCacheConfiguration cacheStringConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(getKeySerializer())  // 키 직렬화 설정
                .serializeValuesWith(getValueStringSerializer())  // 값 직렬화 설정
                .entryTtl(Duration.ofMinutes(10)); // 캐시 시간 설정

        // Page<Product>
        RedisCacheConfiguration cachePageProductConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(getKeySerializer())  // 키 직렬화 설정
                .serializeValuesWith(getValuePageSerializer())  // 값 직렬화 설정
                .entryTtl(Duration.ofMinutes(10)); // 캐시 시간 설정

        // ProductResponse
        RedisCacheConfiguration cacheProductConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(getKeySerializer())  // 키 직렬화 설정
                .serializeValuesWith(getValueProductResponseSerializer())  // 값 직렬화 설정
                .entryTtl(Duration.ofMinutes(10)); // 캐시 시간 설정

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("products", cachePageProductConfiguration);
        cacheConfigs.put("product", cacheProductConfiguration);
        cacheConfigs.put("keyword", cacheStringConfiguration);

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    private static RedisSerializationContext.SerializationPair<String> getKeySerializer() {
        // 키 직렬화 설정: String 직렬화 사용
        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new StringRedisSerializer());
    }

    private static RedisSerializationContext.SerializationPair<String> getValueStringSerializer() {
        // 값 직렬화 설정: String 직렬화 사용
        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    }

    private static RedisSerializationContext.SerializationPair<ProductResponse> getValueProductResponseSerializer() {
        // 값 직렬화 설정: JSON 직렬화 사용
        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new Jackson2JsonRedisSerializer<>(ProductResponse.class));
    }

    private static RedisSerializationContext.SerializationPair<PageImpl> getValuePageSerializer() {
        // Page 직렬화 사전작업
        SimpleModule module = new SimpleModule().addDeserializer(PageImpl.class, new PageImplDeserializer());
        ObjectMapper objectMapper = new ObjectMapper().registerModule(module).registerModule(new JavaTimeModule());

        // 값 직렬화 설정: PageImpl 직렬화 사용
        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, PageImpl.class));
    }
}