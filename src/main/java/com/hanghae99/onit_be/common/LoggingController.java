package com.hanghae99.onit_be.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController public class LoggingController {

    @GetMapping("/health")
    public String checkHealth() {
        return "healthy";
    }
}

