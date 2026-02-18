package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class UsuarioDAO {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscarPorNfc(String token) {
        return usuarioRepository.findByNfcToken(token);
    }

    public Usuario buscarPorNfcToken(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        return usuarioRepository.findByNfcToken(token).orElse(null);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void borrarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByGmail(email).orElse(null);
    }
    
    public Usuario buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario buscarPorUsername(String username) {
        if (username == null) return null;
        String u = username.trim();
        if (u.isEmpty()) return null;
        // En nuestra app, el "username" realmente es el DNI.
        // Si llega un correo (contiene '@'), buscar por gmail.
        if (u.contains("@")) {
            return usuarioRepository.findByGmail(u).orElse(null);
        }
        return usuarioRepository.findByDni(u.toUpperCase()).orElse(null);
    }

    public Usuario buscarPorDni(String dni) {
        if (dni == null) return null;
        String d = dni.trim();
        if (d.isEmpty()) return null;
        return usuarioRepository.findByDni(d.toUpperCase()).orElse(null);
    }
}
