package com.example.oneplusone.domain.search.service;

import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private static final String CACHE_KEY = "cached_trending_keywords";

    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public void saveKeyword(String keyword, String ipAddress) {
        // 어뷰징패턴 체크를 위해서 ip와 키워드 저장
        String userKeyword = "userKeyword:" + ipAddress + ":" + keyword;
        // 검색어의 시간체크를 위한 키
        String trendingKey = "trending_keywords:" + LocalDateTime.now().format(formatter);

        // 어뷰방지코드 같은아이피에서 연속된 검색 처리
        if (redisTemplate.hasKey(userKeyword)) {
            return;
        }
        // 키워드 체크 있으면 수량증가 없으면 생성
        redisTemplate.opsForZSet().incrementScore(trendingKey, keyword, 1);
        // 10분뒤 자동삭제
        redisTemplate.expire(trendingKey, Duration.ofMinutes(10));

        // 어뷰징 방지를 위해 키워드설정  3분후 삭제
        redisTemplate.opsForValue().set(userKeyword, "1", Duration.ofMinutes(3));
    }

    @Cacheable(value = CACHE_KEY)
    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        Map<String, Double> keywordScores = new HashMap<>();

        // 최근 10분 기준으로 인기검색어 조회
        for (int i = 0; i < 10; i++) {
            LocalDateTime time = LocalDateTime.now().minusMinutes(i);
            String key = "trending_keywords:" + time.format(formatter);

            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate
                    .opsForZSet() // ZSet(Sorted Set) 관련 명령어를 사용할 수 있게 해주는 API
                    .rangeWithScores(key, 0, -1); // time 에 맞는 시간에 검색된 키워드 모든 요소를 가져오기

            if (entries != null) {
                for (ZSetOperations.TypedTuple<String> entry : entries) {
                    keywordScores.merge(entry.getValue(), entry.getScore(), Double::sum); //entry.getValue() 가 없으면 그대로 저장 있으면 점수합계
                }
            }
        }


        return new TrendingKeywordResponse(
                keywordScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // 많이 검색된 수로 정렬
                .limit(limit) // 상위부터 limit 만큼 갯수만
                .map(Map.Entry::getKey) // 키워드만 추출
                .toList());
    }
}
