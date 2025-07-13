package com.example.oneplusone.global.config;

import com.example.oneplusone.domain.common.cachekey.CacheKeyConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> popularSearch(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> popularSearch = new RedisTemplate<>();
        popularSearch.setConnectionFactory(connectionFactory);

        popularSearch.setKeySerializer(new StringRedisSerializer());
        popularSearch.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return popularSearch;
    }

    @Bean
    public RedisTemplate<String, Long> pageTotalElements(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> pageTotalElements = new RedisTemplate<>();
        pageTotalElements.setConnectionFactory(connectionFactory);

        pageTotalElements.setKeySerializer(new StringRedisSerializer());
        pageTotalElements.setValueSerializer(new GenericToStringSerializer<>(Long.class));

        return pageTotalElements;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 TTL 없이 생성
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration(
                        // 인기검색어의 경우 TTL 5분 설정
                        CacheKeyConstants.POPULAR_SEARCH,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                .build();
    }
}
