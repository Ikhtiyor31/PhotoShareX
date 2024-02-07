package com.ikhtiyor.photosharex.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Service
public class AccessTokenFilter extends OncePerRequestFilter {

    private final RequestMatcher matcher =
        new AntPathRequestMatcher("/api/v1/**");

    private final AuthenticationManager manager;

    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, ex) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(ex.getMessage());
    };

    public AccessTokenFilter(AuthenticationManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        if (matcher.matches(request)) {
            try {
                String token = getTokenFromHeader(request);
                if (!token.isEmpty()) {
                    Authentication authentication = new AccessTokenAuthentication(token);
                    authentication = manager.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (AuthenticationException e) {
                authenticationEntryPoint.commence(request, response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static String getTokenFromHeader(HttpServletRequest request) {
        var token = request.getHeader("Access-Token");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length());
        }

        return "";
    }
}
