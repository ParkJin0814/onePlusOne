package com.example.oneplusone.domain.auth.repository;

import com.example.oneplusone.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}