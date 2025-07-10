package com.example.oneplusone.domain.auth.service;

import com.example.oneplusone.domain.auth.controller.dto.LoginRequest;
import com.example.oneplusone.domain.auth.controller.dto.LoginResponse;
import com.example.oneplusone.domain.auth.controller.dto.SignUpRequest;
import com.example.oneplusone.domain.auth.controller.dto.SignUpResponse;
import com.example.oneplusone.domain.auth.entity.User;
import com.example.oneplusone.domain.auth.enums.UserRole;
import com.example.oneplusone.domain.auth.repository.UserRepository;
import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import com.example.oneplusone.domain.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignUpResponse signup(SignUpRequest request) {
        // 아이디 중복 검사
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATED_LOGIN_ID);
        }

        UserRole role = UserRole.of(request.getUserRole());

        User user = new User(
                request.getNickname(),
                request.getLoginId(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        User saved = userRepository.save(user);

        return new SignUpResponse(saved.getId(), saved.getLoginId(), saved.getNickname(), saved.getUserRole());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.createAccessToken(user.getId(), user.getUserRole());

        return new LoginResponse(token);
    }

}
