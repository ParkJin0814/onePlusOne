package com.example.oneplusone.domain.search.controller;

import com.example.oneplusone.domain.common.dto.ApiResponse;
import com.example.oneplusone.domain.search.dto.TrendingKeywordResponse;
import com.example.oneplusone.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<TrendingKeywordResponse>> productSearch(@RequestParam(defaultValue = "10") int limit) {
        TrendingKeywordResponse trendingKeywords = searchService.getTrendingKeywords(limit);
        return ResponseEntity.ok(ApiResponse.ok("인기 검색어 목록이 조회되었습니다.", trendingKeywords));
    }

    @GetMapping("/search/no-cache")
    public ResponseEntity<ApiResponse<TrendingKeywordResponse>> productSearchNoCache(
            @RequestParam(defaultValue = "10") int limit) {
        TrendingKeywordResponse trendingKeywords = searchService.getTrendingKeywordsNoCache(limit);
        return ResponseEntity.ok(
                ApiResponse.ok("캐시 없이 인기 검색어 목록이 조회되었습니다.", trendingKeywords)
        );
    }
}
