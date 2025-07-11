package com.example.oneplusone.global.aop;

import com.example.oneplusone.domain.common.redis.RedisLockRepository;
import com.example.oneplusone.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SearchKeywordAspect {
    private final SearchService searchService;
    private final RedisLockRepository redisLockRepository;

    // 정상종료되었을때만 실행
    @AfterReturning(
            pointcut = "execution(* com.example.oneplusone.domain.product.service.ProductService.productsPage(..))"
    )
    public void logSearchKeyword(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 1 && args[0] instanceof String searchKeyword) {
            if (!searchKeyword.isBlank()) {
                searchService.saveKeyword(searchKeyword);
                log.info("검색 키워드 저장: {}", searchKeyword);
            }
        }
    }
}

