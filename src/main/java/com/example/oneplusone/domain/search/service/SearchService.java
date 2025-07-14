package com.example.oneplusone.domain.search.service;

import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
    private final RedisTemplate<String, Object> popularSearch;
    private final RedisTemplate<String, Long> block;

    /**
     * 인기 검색어 리스트를 반환해주는 메소드
     *
     * @param limit 인기 검색어 개수 지정
     * @return 인기 검색어 리스트
     */
    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        String popular_search_time = "popular_search:" + LocalDateTime.now().format(formatter);
        Set<ZSetOperations.TypedTuple<Object>> keywords = popularSearch.opsForZSet().reverseRangeWithScores(popular_search_time, 0, limit - 1);
        List<String> topKeywords = Objects.requireNonNull(keywords).stream()
                .map(keyword -> String.valueOf(keyword.getValue()))
                .toList();
        return new TrendingKeywordResponse(topKeywords);
    }

    /**
     * 검색어 카운팅
     * 현재 시간에 해당 키값의
     *
     * @param search // 검색어
     * @param userId // 검색한 유저의 아이디
     */
    public void popularSearch(String search, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        String popular_search_time = "popular_search:" + now.format(formatter);
        String popular_search_time_plusHour = "popular_search:" + now.plusHours(1).format(formatter);

        String blockKey = "block:" + userId + ":search:" + search;
        // block 캐시가 없는 경우 검색어 카운트를 증가
        if (block.opsForValue().get(blockKey) == null) {
            popularSearch.opsForZSet().incrementScore(popular_search_time, search, 1);
            popularSearch.opsForZSet().incrementScore(popular_search_time_plusHour, search, 1);
            // 검색한 유저는 인기 검색어 카운팅의 어뷰징을 10분간 제한
            block.opsForValue().set(blockKey, 1L, 10, TimeUnit.MINUTES);

            // 캐시에 TTL이 설정 안된 경우 TTL 설정
            if (popularSearch.getExpire(popular_search_time_plusHour) == -1) {
                popularSearch.expire(popular_search_time, Duration.ofHours(1));
                popularSearch.expire(popular_search_time_plusHour, Duration.ofHours(2));
            }
        }
    }
}
