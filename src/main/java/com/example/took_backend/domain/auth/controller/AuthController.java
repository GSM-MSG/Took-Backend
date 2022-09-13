package com.example.took_backend.domain.auth.controller;

import com.example.took_backend.domain.auth.dto.request.UserSignUpRequest;
import com.example.took_backend.domain.auth.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody UserSignUpRequest userReq) {
        signupService.execute(userReq);
        return ResponseEntity.ok().build();
    }
}