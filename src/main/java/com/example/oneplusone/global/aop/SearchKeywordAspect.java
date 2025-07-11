package com.example.oneplusone.global.aop;

import com.example.oneplusone.domain.search.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class SearchKeywordAspect {
    private final SearchService searchService;


    // 정상종료되었을때만 실행
    @AfterReturning(
            pointcut = "execution(* com.example.oneplusone.domain.product.service.ProductService.productsPage(..))"
                    + " || execution(* com.example.oneplusone.domain.product.controller.ProductController.productsPageV2(..))"
    )
    public void logSearchKeyword(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 1 && args[0] instanceof String searchKeyword) {
            if (!searchKeyword.isBlank()) {
                searchService.saveKeyword(searchKeyword, getClientIp());
            }
        }
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "unknown";
        HttpServletRequest request = attributes.getRequest();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}