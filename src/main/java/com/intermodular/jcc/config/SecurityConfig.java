package com.intermodular.jcc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita protección CSRF para poder hacer POST desde Postman
                .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // ¡PERMITE TODO! (Solo para desarrollo)
                );
        return http.build();
    }
}
