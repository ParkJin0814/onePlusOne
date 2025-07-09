package com.example.oneplusone.domain.common.filter;

import jakarta.servlet.*;
import com.example.oneplusone.domain.common.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // HttpServletRequest/Response로 캐스팅 (서블릿 기준)
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // 현재 요청 URI 확인
        String requestURI = httpRequest.getRequestURI();
        log.info("요청 URI: {}", requestURI);

        // Authorization 헤더에서 JWT 토큰 추출
        String authHeader = httpRequest.getHeader("Authorization");

        // 로그인 / 회원가입 요청은 jwt 토큰 없이도 접근 가능하게 허용
        if (requestURI.startsWith("/auth")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Authorization 헤더가 없거나 Bearer 형식이 아닌 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JWT 토큰 없음 또는 형식 오류");
            sendError(httpResponse, HttpServletResponse .SC_UNAUTHORIZED, "JWT 토큰이 필요합니다. ");
            return;
        }

        // Bearer 접두사를 제거하고 실제 토큰만 추출
        String token = jwtUtil.resolveToken(authHeader);

        // 토큰 유효성 검증
        if (!jwtUtil.vaildateToken(token)) {
            sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다. ");
            return;
        }

        // 토큰에서 사용자ID(식별자) 추출
        String userId = jwtUtil.extractUserId(token);

        // 특정한 권한이 필요한 URI에 접근하는 경우 권한 확인 (판매자)
        if (requestURI.startsWith("/seller")) {
            // 판매자 권한인지 확인
            if (!jwtUtil.hasRole(token, "SELLER")) {
                sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "판매자 권한이 필요합니다.");
                return;
            }
        }

        // 전용 API가 아닌 일반 API의 경우
        filterChain.doFilter(servletRequest, servletResponse);


    }

    /**
     * 에러 응답을 공통 형식으로 처리하는 유틸 메서드
     * @param response HttpServletResponse
     * @param status HTTP 상태 코드
     * @param message 사용자에게 전달할 메시지
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
