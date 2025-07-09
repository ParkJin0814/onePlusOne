package com.example.oneplusone.domain.common.filter;

import jakarta.servlet.*;
import com.example.oneplusone.domain.common.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        // Authorization 헤더에서 JWT 토큰 추출
        String authHeader = httpRequest.getHeader("Authorization");

        // 로드인 / 회원가입 요청은 jwt 토큰 없이도 접근 가능하게 허용
        if (requestURI.startsWith(""))
    }
}
