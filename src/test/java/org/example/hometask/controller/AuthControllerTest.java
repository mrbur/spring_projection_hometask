package org.example.hometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hometask.dto.AuthRequest;
import org.example.hometask.dto.AuthResponse;
import org.example.hometask.security.CustomUserDetailsService;
import org.example.hometask.security.JWTUtils;
import org.example.hometask.security.JwtAuthenticationFilter;
import org.example.hometask.security.LoggingFilter;
import org.example.hometask.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvcTester mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoSpyBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JWTUtils jwtUtils;

    @MockitoBean
    private LoggingFilter loggingFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void mockFilters() throws Exception {
        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(loggingFilter).doFilter(any(), any(), any());
    }
    @Test
    @WithMockUser
    void login_ShouldReturnAccessAndRefreshTokens_WhenCredentialsAreValid() throws Exception {
        AuthRequest request = new AuthRequest("user", "password123");
        AuthResponse response = new AuthResponse("mocked-access-token", "mocked-refresh-token");

        Mockito.when(authenticationService.login(any(AuthRequest.class))).thenReturn(response);
        mvc.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        assertThat(mvc.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.accessToken", token -> assertThat(token).isEqualTo("mocked-access-token"))
                .hasPathSatisfying("$.refreshToken", token -> assertThat(token).isEqualTo("mocked-refresh-token"));
    }


    @Test
    @WithMockUser
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        AuthRequest request = new AuthRequest("wrong_user", "wrong_password");

        Mockito.when(authenticationService.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThat(mvc.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }
}