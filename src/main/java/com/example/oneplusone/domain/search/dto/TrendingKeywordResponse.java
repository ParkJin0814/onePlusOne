package com.example.oneplusone.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrendingKeywordResponse {
    private List<String> trendingKeywords;

}
