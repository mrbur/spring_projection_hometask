package org.example.hometask.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {  
  
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);  
  
    @Override  
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            var roles = authentication.getAuthorities();

            logger.info("Фильтр зафиксировал действие от пользователя: " + username + " с ролями: " + roles);
        } else {
            logger.info("Фильтр зафиксировал анонимный запрос");
        }
        filterChain.doFilter(request, response);
    }  
}