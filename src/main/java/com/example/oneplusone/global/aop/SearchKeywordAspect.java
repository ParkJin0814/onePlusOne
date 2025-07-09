package com.example.oneplusone.global.aop;

import com.example.oneplusone.domain.search.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SearchKeywordAspect {
    private final SearchService searchService;


    // 정상종료되었을때만 실행
    @AfterReturning(
            pointcut = "execution(* com.example.oneplusone.domain.product.service.ProductService.productsPage(..))"
    )
    public void logSearchKeyword(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 1 && args[0] instanceof String searchKeyword) {
            if (!searchKeyword.isBlank()) {
                String ipAddress = getClientIp();
                searchService.saveKeyword(searchKeyword, ipAddress);
                log.info("검색 키워드 저장: {}, IP: {}", searchKeyword, ipAddress);
            }
        }
    }

    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
