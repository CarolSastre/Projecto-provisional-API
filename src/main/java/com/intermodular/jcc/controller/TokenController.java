package com.intermodular.jcc.controller;

import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private JwtEncoder encoder;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        Instant now = Instant.now();
        
        // Recopilar los roles del usuario (ej: "ROLE_PROFESOR")
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Crear el contenido del token (Claims)
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600L)) // Expira en 1 hora
                .subject(authentication.getName())
                .claim("scope", scope) // Guardamos los roles en el token
                .build();

        // Firmar el token con nuestra clave privada RSA
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}