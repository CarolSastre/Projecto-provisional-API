package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component 
public class UsuarioDAO {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscarPorNfc(String token) {
        return usuarioRepository.findByNfcToken(token);
    }
    
}