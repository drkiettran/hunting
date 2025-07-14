package com.ops.hunting.common.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Different rate limits for different endpoints
        if (path.startsWith("/api/auth/login")) {
            // Strict rate limiting for login attempts
            if (!rateLimitService.tryConsumeFromRequest(request, 1, 5, Duration.ofMinutes(15))) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many login attempts. Please try again later.");
                return;
            }
        } else if (path.startsWith("/api/")) {
            // General API rate limiting
            if (!rateLimitService.tryConsumeFromRequest(request, 1, 100, Duration.ofMinutes(1))) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Rate limit exceeded. Please slow down.");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
