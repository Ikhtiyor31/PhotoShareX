package com.ikhtiyor.photosharex.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class AccessTokenFilter extends OncePerRequestFilter {

    private final AuthenticationManager manager;

    private static final List<AntPathRequestMatcher> PERMITTED_ENDPOINTS = List.of(
        new AntPathRequestMatcher("/api/v1/users/register", "POST"),
        new AntPathRequestMatcher("/api/v1/users/login", "POST"),
        new AntPathRequestMatcher("/api/v1/users/verify-email", "POST"),
        new AntPathRequestMatcher("/api/v1/users/refresh/**", "POST"),
        new AntPathRequestMatcher("/api/v1/users/reset-password", "POST"),
        new AntPathRequestMatcher("/api/v1/users/reset-password", "PATCH")
    );

    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, ex) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(ex.getMessage());
    };

    public AccessTokenFilter(AuthenticationManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain) throws ServletException, IOException {

        if (isPermittedPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromHeader(request);
            Authentication authentication = new AccessTokenAuthentication(token);
            authentication = manager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        }

    }

    private boolean isPermittedPath(HttpServletRequest request) {
        return PERMITTED_ENDPOINTS.stream().anyMatch(matcher -> matcher.matches(request));
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BadCredentialsException("Access token is required");
        }
        return header.substring(7);
    }
}
