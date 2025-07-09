package com.example.oneplusone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OnePlusOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnePlusOneApplication.class, args);
    }

}