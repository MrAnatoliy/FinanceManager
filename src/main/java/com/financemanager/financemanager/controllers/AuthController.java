package com.financemanager.financemanager.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financemanager.financemanager.DTOs.LoginDto;
import com.financemanager.financemanager.DTOs.RegisterDto;
import com.financemanager.financemanager.services.UserService;
import com.financemanager.financemanager.utils.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService; // implements UserDetailsService
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto dto) {
        userService.create(dto.username(), dto.password(), dto.roles());
        return ResponseEntity.ok("User created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto dto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generate(user.getUsername(), user.getAuthorities());
        return ResponseEntity.ok(Map.of("token", token));
    }
}