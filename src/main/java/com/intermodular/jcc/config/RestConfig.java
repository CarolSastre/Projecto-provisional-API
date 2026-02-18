
package com.intermodular.jcc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// Usamos 'record' que es nativo en Java 17 y ahorra getters/setters
@ConfigurationProperties(prefix = "jwt")
public record RestConfig(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}