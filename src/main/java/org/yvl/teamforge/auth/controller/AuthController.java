package org.yvl.teamforge.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yvl.teamforge.auth.dto.request.LoginRequest;
import org.yvl.teamforge.auth.dto.request.RefreshTokenRequest;
import org.yvl.teamforge.auth.dto.request.RegisterRequest;
import org.yvl.teamforge.auth.dto.response.AuthResponse;
import org.yvl.teamforge.auth.dto.response.RefreshTokenResponse;
import org.yvl.teamforge.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return service.refreshToken(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        service.logout(request);
    }
}
