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
        String key = "trending_keywords:" + LocalDateTime.now().format(formatter); // trending_keywords: + 현재시간으로 키 설정
        redisTemplate.opsForZSet().incrementScore(key, keyword, 1); // 같은시간 같은 키워드가 들어오면 스코어 1씩 증가
        redisTemplate.expire(key, Duration.ofMinutes(30)); // 30분이후 자동삭제 설정
    }

    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        Map<String, Double> keywordScores = new HashMap<>();

        // 최근 30분 기준으로 인기검색어 조회
        for (int i = 0; i < 30; i++) {
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
