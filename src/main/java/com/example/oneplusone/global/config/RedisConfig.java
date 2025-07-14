package com.example.oneplusone.global.config;

import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);

        return Redisson.create(config);
    }


    @Bean
    public RedisTemplate<String, Object> popularSearch(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> popularSearch = new RedisTemplate<>();
        popularSearch.setConnectionFactory(connectionFactory);

        popularSearch.setKeySerializer(new StringRedisSerializer());
        popularSearch.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return popularSearch;
    }

    @Bean
    public RedisTemplate<String, List<ProductResponse>> searchProductsPage(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<ProductResponse>> searchProductsPage = new RedisTemplate<>();
        searchProductsPage.setConnectionFactory(connectionFactory);

        searchProductsPage.setKeySerializer(new StringRedisSerializer());
        searchProductsPage.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return searchProductsPage;
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
    public RedisTemplate<String, ProductResponse> searchProduct(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ProductResponse> searchProduct = new RedisTemplate<>();
        searchProduct.setConnectionFactory(connectionFactory);

        searchProduct.setKeySerializer(new StringRedisSerializer());
        searchProduct.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return searchProduct;
    }
}
