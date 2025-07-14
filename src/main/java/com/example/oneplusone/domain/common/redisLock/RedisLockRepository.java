package com.example.oneplusone.domain.common.redisLock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisLockRepository {
    private final RedisTemplate<String, String> redisLock;

    // 락 생성
    public Boolean redisLock(Object key) {
        return redisLock.opsForValue()
                .setIfAbsent(key.toString(), "lock", Duration.ofMillis(3000));
    }
    // 락 해제
    public void redisUnLock(Object key) {
        redisLock.delete(key.toString());
    }
}
