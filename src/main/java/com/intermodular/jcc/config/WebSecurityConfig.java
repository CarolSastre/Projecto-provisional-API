package com.intermodular.jcc.config;

import com.intermodular.jcc.service.UserDetailsServiceImpl; // Tu servicio nuevo
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas PÚBLICAS (NFC no hace login, y crear usuario si quieres que sea libre)
                .requestMatchers("/api/acceso/validar").permitAll() 
                
                // 2. Rutas PROTEGIDAS (Solo ADMIN o PROFESOR pueden ver/crear usuarios)
                // Ajusta esto según tus roles. Si Rol.PROFESOR, aquí pones "PROFESOR"
                .requestMatchers("/api/usuarios/**").hasAnyRole("PROFESOR", "ADMIN")
                
                // 3. El resto requiere login
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {}); // Habilita autenticación básica (Usuario/Pass en Postman)
            
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsServiceImpl userDetailsService, // Inyectamos TU servicio
            PasswordEncoder passwordEncoder) {
        
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
}
