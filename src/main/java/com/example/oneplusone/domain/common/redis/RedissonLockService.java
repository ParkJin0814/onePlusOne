package com.example.oneplusone.domain.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockService {
    private final RedissonClient redissonClient;

    public <T> T execute(Long productId, Supplier<T> task) {
        RLock lock = redissonClient.getLock(productId.toString());

        try {
            boolean acquireLock = lock.tryLock(30, 10, TimeUnit.SECONDS);
            if (!acquireLock) {
                return null;
            }
            log.info("락 획득 성공 - productId: {}", productId);
            return task.get();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }
}
