package com.example.oneplusone;

import com.example.oneplusone.domain.auth.controller.dto.SignUpRequest;
import com.example.oneplusone.domain.auth.service.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class InitData {

    private final PasswordEncoder passwordEncoder;
    private final AuthService userService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String KEY_PREFIX = "trending_keywords:";
    private static final int DUMMY_COUNT = 100_000;
    private static final int MINUTES_RANGE = 10; // 최근 10분

    @PostConstruct
    @Transactional
    public void init() {
        // 1) 인증용 유저 생성
        SignUpRequest user = new SignUpRequest("loadUser", "testPass", "test", "BUYER");
        userService.signup(user);

        // 2) Redis에 더미 트렌딩 키워드 데이터 생성
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < DUMMY_COUNT; i++) {
            // 0~9분 중 랜덤한 시각 선택
            int offset = random.nextInt(MINUTES_RANGE);
            LocalDateTime ts = now.minusMinutes(offset);
            String key = KEY_PREFIX + ts.format(FORMATTER);

            // 더미 키워드는 예시로 "keyword0"~"keyword999" 범위에서 랜덤 생성
            String keyword = "keyword" + random.nextInt(1_000);

            // 점수도 랜덤
            double score = random.nextDouble() * 100.0;

            // ZSet에 추가 (중복 키워드는 score 합산)
            redisTemplate.opsForZSet()
                    .add(key, keyword, score);
        }

        System.out.println(">> InitData: inserted " +
                DUMMY_COUNT + " dummy trending keywords into Redis");
    }
}
