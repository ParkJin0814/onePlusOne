package com.example.oneplusone.global.config;

import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.example.oneplusone.domain.product.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // <String, String> 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, ProductResponse> productRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ProductResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // <String, ProductResponse> 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProductResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, Page<Product>> productPageRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Page<Product>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Page 직렬화 사전작업
        SimpleModule module = new SimpleModule().addDeserializer(PageImpl.class, new PageImplDeserializer());
        ObjectMapper objectMapper = new ObjectMapper().registerModule(module).registerModule(new JavaTimeModule());

        // <String, Page> 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, PageImpl.class));
        return template;
    }

}