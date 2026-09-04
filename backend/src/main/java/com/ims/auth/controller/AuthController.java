package com.ims.auth.controller;

import com.ims.auth.dto.AuthResponse;
import com.ims.auth.dto.LoginRequest;
import com.ims.auth.dto.RegisterRequest;
import com.ims.auth.service.AuthService;

import com.ims.security.CustomUserDetails;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

        private final AuthService authService;

        /*
         * Register User
         * 
         * POST /api/auth/register
         */

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(
                        @Valid @RequestBody RegisterRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                authService.register(request));

        }

        /*
         * Login User
         * 
         * POST /api/auth/login
         */

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                return ResponseEntity
                                .ok(
                                                authService.login(request));

        }

        /*
         * Current Logged User
         * 
         * GET /api/auth/me
         * 
         * Requires JWT
         */

        @GetMapping("/me")
        public ResponseEntity<AuthResponse> currentUser(Authentication authentication) {

                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                String role = userDetails.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                                .orElse("USER");

                AuthResponse response = AuthResponse.builder()
                                .id(userDetails.getId())
                                .firstName(userDetails.getFirstName())
                                .lastName(userDetails.getLastName())
                                .email(userDetails.getUsername())
                                .role(role)
                                .build();

                return ResponseEntity.ok(response);
        }

}