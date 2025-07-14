package com.example.oneplusone.domain.common.redisLock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class LockService {
    private final RedisLockRepository redisLockRepository;

    public <T> T execute(Long id, Supplier<T> task) {
        // 락을 얻지 못한 경우 대기
        while (!redisLockRepository.redisLock(id)) {
            try {
                Thread.sleep(3000); // 3초뒤 다시 락 얻기 시도
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try{
            return task.get();  // 락을 얻은 경우 작업 실행
        } finally { //작업이 실패해도 락을 해제해야 하기 때문에 try-finally 처리
            redisLockRepository.redisUnLock(id);    // 작업이 끝나고 락 해제
        }
    }

}
