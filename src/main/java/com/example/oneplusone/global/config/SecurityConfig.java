package com.example.oneplusone.global.config;

import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.common.filter.JwtFilter;
import com.example.oneplusone.domain.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JwtFilter 등록 (전역 필터로 동작)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()    // 로그인, 회원가입 등은 모두 허용
                        .requestMatchers("/search").permitAll()    // 로그인, 회원가입 등은 모두 허용
                        .requestMatchers("/search/no-cache").permitAll()    // 로그인, 회원가입 등은 모두 허용
                        .requestMatchers("/seller/**").hasRole("SELLER") // SELLER 권한만 접근 가능
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class); // 필터 삽입

        return http.build();
    }
}
