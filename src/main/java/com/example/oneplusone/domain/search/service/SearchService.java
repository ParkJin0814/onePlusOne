package com.example.oneplusone.domain.search.service;

import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RedisTemplate<String, String> redisTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public void saveKeyword(String keyword) {
        String key = "trending_keywords:" + LocalDateTime.now().format(formatter);
        redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
        redisTemplate.expire(key, Duration.ofMinutes(30));
    }

    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        Map<String, Double> keywordScores = new HashMap<>();

        for (int i = 0; i < 30; i++) {
            LocalDateTime time = LocalDateTime.now().minusMinutes(i);
            String key = "trending_keywords:" + time.format(formatter);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

            if (entries != null) {
                for (ZSetOperations.TypedTuple<String> entry : entries) {
                    keywordScores.merge(entry.getValue(), entry.getScore(), Double::sum);
                }
            }
        }


        return new TrendingKeywordResponse(
                keywordScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList());
    }
}
