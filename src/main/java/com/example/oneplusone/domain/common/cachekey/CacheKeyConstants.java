package com.example.oneplusone.domain.common.cachekey;

public final class CacheKeyConstants {
    private CacheKeyConstants() {}

    // 단일 상품 조회 캐시
    public static final String PRODUCT = "cached_product";
    // 상품 검색(페이지) 캐시
    public static final String SEARCH_PRODUCT = "cached_search_product";
    // 주문리스트 캐시
    public static final String ORDERS = "cached_orders";
}
