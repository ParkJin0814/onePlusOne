package com.example.oneplusone.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignUpRequest {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    @NotBlank
    private String userRole;
}
