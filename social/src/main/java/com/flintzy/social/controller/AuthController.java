package com.flintzy.social.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @GetMapping("/login/google")
    public ResponseEntity<Map<String ,String >>googlelogin(){
        return ResponseEntity.ok(Map.of("loginUrl", "/oauth2/authorization/google"));
    }
}
