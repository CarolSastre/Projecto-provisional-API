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

    // Buscar un usuario concreto
    public Optional<Usuario> buscarPorNfc(String token) {
        return usuarioRepository.findByNfcToken(token);
    }

    // Obtener la lista de todos los alumnos y profesores
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Guardar un usuario nuevo en la base de datos
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Borrar usuario por ID (útil para limpieza)
    public void borrarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }
}
