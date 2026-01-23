package com.intermodular.jcc.service;

import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos el usuario en MongoDB (tienes que añadir este método al repo si no existe)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. Convertimos tu Usuario (Mongo) al UserDetails (Spring Security)
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword()) // Spring espera que esto esté cifrado con BCrypt
                .roles(usuario.getRol().name())  // Convierte tu Enum Rol a String (ej: "PROFESOR")
                .build();
    }
}