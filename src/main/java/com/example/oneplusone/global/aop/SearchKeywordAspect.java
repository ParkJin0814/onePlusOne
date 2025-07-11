package com.example.oneplusone.global.aop;

import com.example.oneplusone.domain.common.redis.RedisLockRepository;
import com.example.oneplusone.domain.search.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
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

    @Around("execution(* com.example.oneplusone.domain.orders.service.OrderService.orderProduct(..))")
    public Object lockProductOrder(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String requestURI = request.getRequestURI();
        String substring = requestURI.substring(17);
        Long productId = Long.valueOf(substring);   // order 상품 식별자

        while (!redisLockRepository.redisLock(productId)) {
            try {
                Thread.sleep(3000); // 3초뒤 다시 락 얻기 시도
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }  // 작업 하기 전 락 설정

        try{
            return joinPoint.proceed();
        }
        finally {   // 락은 작업이 실패해도 풀어야 하기 때문에 try-finally 처리
            redisLockRepository.redisUnLock(productId);
        }
    }
}

