package com.example.oneplusone.domain.search.service;

import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_KEY = "keyword:";

    public void saveKeyword(String keyword, String ip) {
        String keyAndIp = "search:" + keyword + "_" + ip;
        Long count = redisTemplate.opsForValue().increment(keyAndIp, 1); // 요청별 카운트
        if (count != null && count == 1) {
            // 요청이 들어온 기준으로 1분 제한
            redisTemplate.expire(keyAndIp, Duration.ofMinutes(1));
        }
        if (count != null && count <= 2) {
            // 같은 ip의 요청이 1분동안 2번을 넘어가면 인기검색어 카운트 안함
            redisTemplate.opsForZSet().incrementScore(CACHE_KEY, keyword, 1); // 인기 검색어 카운트
            redisTemplate.expire(CACHE_KEY, Duration.ofMinutes(10));
            log.info("검색 키워드 저장: {}", keyword);
        }
    }

    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(CACHE_KEY, 0, limit - 1);

        return new TrendingKeywordResponse(Objects.requireNonNull(typedTuples)
                .stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList());
    }
}