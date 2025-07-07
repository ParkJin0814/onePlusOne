package com.example.oneplusone.domain.auth.enums;

import com.example.oneplusone.domain.common.exception.BaseException;
import com.example.oneplusone.domain.common.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserRole {
    SELLER("ROLE_SELLER"),
    BUYER("ROLE_BUYER");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role) || r.roleName.equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode.USERROLE_NOT_FOUND));
    }
}
