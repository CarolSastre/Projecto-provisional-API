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

    // --- NUEVO MÉTODO PARA LOGIN ---
    public Optional<Usuario> buscarPorDni(String dni) {
        // Opción A: Si tu repositorio ya tiene findByDni
        // return usuarioRepository.findByDni(dni);

        // Opción B (Más segura si no quieres tocar el repositorio ahora):
        // Trae todos y filtra (menos eficiente pero funciona seguro con lo que tienes)
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getDni() != null && u.getDni().equalsIgnoreCase(dni))
                .findFirst();
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
}
