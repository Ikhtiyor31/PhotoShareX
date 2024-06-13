package com.ikhtiyor.photosharex.config;

import com.ikhtiyor.photosharex.security.AccessTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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

    private final AccessTokenFilter accessTokenFilter;

    public SecurityConfig(AccessTokenFilter accessTokenFilter) {
        this.accessTokenFilter = accessTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/users/register",
                    "/api/v1/users/login",
                    "/api/v1/users/verify-email",
                    "/api/v1/users/refresh/**",
                    "/api/v1/users/reset-password",
                    "/api/v1/users/test").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/users/reset-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/photos").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
