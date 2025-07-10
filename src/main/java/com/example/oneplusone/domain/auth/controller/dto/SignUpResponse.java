package com.example.oneplusone.domain.auth.controller.dto;

import com.example.oneplusone.domain.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {

    private Long id;
    private String loginId;
    private String nickname;
    private UserRole userRole;
}
