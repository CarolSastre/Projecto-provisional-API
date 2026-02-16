package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.UsuarioDAO;
import com.intermodular.jcc.dto.LoginResponse;
import com.intermodular.jcc.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth") // Coincide con la llamada de Android @POST("auth/login")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String dni = credenciales.get("dni");
        String password = credenciales.get("password");

        // 1. Buscar usuario
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorDni(dni);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Verificar contraseña
        if (!encoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        // 3. Generar Token (Simplificado con UUID para desarrollo)
        String token = "Bearer " + UUID.randomUUID().toString();

        // 4. Devolver respuesta
        return ResponseEntity.ok(new LoginResponse(token, usuario));
    }
}