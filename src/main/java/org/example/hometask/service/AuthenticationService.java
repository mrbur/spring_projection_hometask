package org.example.hometask.service;

import org.example.hometask.dto.AuthRequest;
import org.example.hometask.dto.AuthResponse;
import org.example.hometask.security.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;

    public AuthenticationService(AuthenticationManager authenticationManager, 
                                 UserDetailsService userDetailsService, 
                                 JWTUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        String accessToken = jwtUtils.generateToken(userDetails);

        String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }
}