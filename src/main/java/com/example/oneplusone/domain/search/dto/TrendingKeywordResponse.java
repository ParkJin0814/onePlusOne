package com.example.oneplusone.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrendingKeywordResponse {
    private final List<String> trendingKeywords;

}
