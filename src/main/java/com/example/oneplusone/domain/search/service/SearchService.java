package com.example.oneplusone.domain.search.service;

import com.example.oneplusone.domain.product.dto.response.ProductResponse;
import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
    private final RedisTemplate<String, Object> popularSearch;
    private final RedisTemplate<String, ProductResponse> searchProductCache;
    private final RedisTemplate<String, List<ProductResponse>> searchPageCache;
    private final RedisTemplate<String, Long> searchTotal;
    private final RedisTemplate<String, Long> block;

    private final String popular_search_time = "popular_search:" + LocalDateTime.now().format(formatter);
    private String searchCacheKey;
    private String totalCountKey;
    private String blockKey;
    private String searchInfo;
    private String popular_search_time_plusHour;

    /**
     * 인기 검색어 리스트를 반환해주는 메소드
     * @param limit 인기 검색어 개수 지정
     * @return  인기 검색어 리스트
     */
    public TrendingKeywordResponse getTrendingKeywords(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> keywords = popularSearch.opsForZSet().reverseRangeWithScores(popular_search_time, 0, limit - 1);
        List<String> topKeywords = keywords.stream()
                .map(keyword -> String.valueOf(keyword.getValue()))
                .toList();
        return new TrendingKeywordResponse(topKeywords);

    }

    /**
     * 상품 단건 조회 cache 생성
     * @param id    상품 식별자
     * @param product   상품 객체
     */
    public void createProductCache(Long id, ProductResponse product) {
        String key = "search:" + id;
        searchProductCache.opsForValue().set(key, product, 10, TimeUnit.MINUTES);
    }

    /**
     * 상품 단건 조회시 cache 유무를 확인
     * @param id    상품 식별자
     * @return      상품 객체
     */
    public ProductResponse cacheProductSearch(Long id) {
        String key = "search:" + id;
        return  searchProductCache.opsForValue().get(key);
    }

    /**
     * 캐시 삭제
     * @param id    상품 식별자
     */
    public void cacheEviction(Long id) {
        String key = "search:" + id;
        searchProductCache.delete(key);
    }

    /**
     * 상품 목록 조회시 캐시의 유무를 확인
     * @param search    상품 검색어
     * @param pageable  상품 페이지 정보
     * @return  검색한 상품 페이지 목록
     */
    public Page<ProductResponse> cachePageSearch( String search, Pageable pageable) {
        setKey(search, pageable);

        List<ProductResponse> cacheProducts = searchPageCache.opsForValue().get(searchCacheKey);
        //캐시가 존재하는 경우
        if (cacheProducts != null) {
            // 검색어의 총 상품 개수
            long totalCount = searchTotal.opsForValue().get(totalCountKey);

            // 인기 검색어 추가
            popularSearch();



            log.info("redis 조회");
            return new PageImpl<>(
                    cacheProducts, pageable, totalCount
            );
        }
        return null;
    }

    /**
     * 상품 목록 조회 cache 생성
     * @param productResponses  상품 목록 페이지
     * @param search    상품 검색어
     * @param pageable  상품 페이지 정보
     */
    public void createPageCache(Page<ProductResponse> productResponses, String search, Pageable pageable) {
        setKey(search, pageable);

        popularSearch();

        searchPageCache.opsForValue().set(searchCacheKey, productResponses.toList(), 10, TimeUnit.MINUTES);
        searchTotal.opsForValue().set(totalCountKey, productResponses.getTotalElements(), 10, TimeUnit.MINUTES);

    }

    /**
     * 검색어 카운팅
     */
    public void popularSearch() {

        // block 캐시가 없는 경우 검색어 카운트를 증가
        if (block.opsForValue().get(blockKey) == null) {
            popularSearch.opsForZSet().incrementScore(popular_search_time, searchInfo, 1);
            popularSearch.opsForZSet().incrementScore(popular_search_time_plusHour, searchInfo, 1);
            // 검색한 유저는 인기 검색어 카운팅의 어뷰징을 10분간 제한
            block.opsForValue().set(blockKey, 1L, 10, TimeUnit.MINUTES);

            // 캐시에 TTL이 설정 안된 경우 TTL 설정
            if (popularSearch.getExpire(popular_search_time_plusHour) == -1) {
                popularSearch.expire(popular_search_time, Duration.ofHours(1));
                popularSearch.expire(popular_search_time_plusHour, Duration.ofHours(2));
            }
        }
    }

    // 캐시 데이터 키 등록
    private void setKey(String search, Pageable pageable) {
        searchCacheKey = "search:" + search + ":page:" + pageable.getPageNumber() + ":size:" + pageable.getPageSize();
        totalCountKey = "search:" + search + ":size";
        blockKey = "block:" + "id" + ":search:" + search;
        searchInfo = "search:" + search;
        popular_search_time_plusHour = "popular_search:" + LocalDateTime.now().plusHours(1).format(formatter);
    }
}
