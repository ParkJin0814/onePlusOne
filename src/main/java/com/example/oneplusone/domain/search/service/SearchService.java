package com.example.oneplusone.domain.search.service;

import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_KEY = "keyword";

    public void saveKeyword(String keyword) {
        redisTemplate.opsForZSet().incrementScore(CACHE_KEY, keyword, 1);
        redisTemplate.expire(CACHE_KEY, Duration.ofMinutes(10));
    }

    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(CACHE_KEY, 0, limit - 1);

        return new TrendingKeywordResponse(Objects.requireNonNull(typedTuples)
                .stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList());
    }
}