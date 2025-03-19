package com.ikhtiyor.photosharex.config;

import com.ikhtiyor.photosharex.security.AccessTokenAuthenticationProvider;
import com.ikhtiyor.photosharex.security.AccessTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccessTokenAuthenticationProvider provider;

    public SecurityConfig(AccessTokenAuthenticationProvider provider) {
        this.provider = provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
        AuthenticationManager authenticationManager) throws Exception {
        http
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/users/register",
                    "/api/v1/users/login",
                    "/api/v1/users/verify-email",
                    "/api/v1/users/refresh/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/users/reset-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/photos",
                    "/api/v1/comments/**",
                    "/api/v1/albums/**",
                    "/api/v1/likes/**",
                    "/api/v1/devices")
                .hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .authenticationProvider(provider)
            .addFilterBefore(accessTokenFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(provider)
            .build();
    }

    @Bean
    public AccessTokenFilter accessTokenFilter(AuthenticationManager authenticationManager) {
        return new AccessTokenFilter(authenticationManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
