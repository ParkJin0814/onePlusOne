package com.example.oneplusone.domain.common.security;

import com.example.oneplusone.domain.auth.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    private final long TOKEN_TIME = 60 * 60 * 1000L; //60분
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access 토큰 생성
    public String createAccessToken(Long userId, UserRole userRole) {
        Date now = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId)) // 토큰의 주인 (식별자)
                .claim("auth", userRole.name()) // 권한 저장 ("SELLER" 또는 "BUYER")
                .setIssuedAt(now) // 발급시간
                .setExpiration(new Date(now.getTime() + TOKEN_TIME)) // 만료시간 (60분)
                .signWith(key, signatureAlgorithm) // 비밀 키 + 서명 알고리즘
                .compact(); // 토큰 생성
    }

    // Refresh 토큰 생성
    public String createRefreshToken() {
        Date now = new Date();

        return Jwts.builder()
                .setId(java.util.UUID.randomUUID().toString()) // 고유 ID
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 헤더에서 Bearer을 제외한 Token 부분만 추출
    public String resolveToken(String bearerToken) {

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // jwt 토큰에서 userID(식별자) 정보 추출
    public String extractUserId(String token) {

        return extractAllClaims(token).getSubject();  // JWT의 subject는 생성 시 setSubject(UserId)로 설정한 값
    }

    // JWT 토큰에서 권한 정보를 추출하는 메서드
    public String extractUserRole(String token) {

        return extractAllClaims(token).get("auth", String.class);
    }

    public boolean hasRole(String token, String role) {
        String tokenRole = extractUserRole(token); // 토큰에서 역할 추출

        return tokenRole.equalsIgnoreCase(role); // 대소문자 무시
    }

    // jwt 토큰의 유효성 검증
    public boolean vaildateToken(String token) {
        try {
            // 1. JWT 파서 객체를 생성하고
            // 2. 서명 검증에 사용할 키를 지정
            // 3. 해당 키로 JWT 서명 및 포맷 검사를 수행
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 해당 부분 예외처리 시 토큰이 유효하지 않다는 것

            return true; // 예외가 없으면 유효한 토큰
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            // 서명이 유효하지 않거나, 토큰이 잘못된 형식일 경우
            log.error("유효하지 않은 JWT 서명", e);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.error("만료된 JWT 토큰", e);
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 JWT 형식일 때 (ex: 압축된 JWT 등)
            log.error("지원되지 않는 JWT 토큰", e);
        } catch (IllegalArgumentException e) {
            // 토큰 내용이 비어있거나 형식이 이상할 때
            log.error("JWT claims가 비어있습니다", e);
        }
        return false; // 예외가 발생하면 유효하지 않은 토큰
    }

    // JWT 토큰을 파싱해서 모든 Claims 정보를 꺼내오는 내부 메서드
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 검증을 위한 키
                .build()
                .parseClaimsJws(token) // JWT 문자열을 파싱하고 검증
                .getBody(); // 토큰 본문 (payLoad) 반환
    }

}
