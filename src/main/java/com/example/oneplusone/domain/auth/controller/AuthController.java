package com.example.oneplusone.domain.auth.controller;

import com.example.oneplusone.domain.auth.controller.dto.LoginRequest;
import com.example.oneplusone.domain.auth.controller.dto.LoginResponse;
import com.example.oneplusone.domain.auth.controller.dto.SignUpRequest;
import com.example.oneplusone.domain.auth.controller.dto.SignUpResponse;
import com.example.oneplusone.domain.auth.service.AuthService;
import com.example.oneplusone.domain.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(@Valid @RequestBody SignUpRequest request) {

        SignUpResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입 되었습니다.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("로그인 되었습니다.", response));
    }
}
