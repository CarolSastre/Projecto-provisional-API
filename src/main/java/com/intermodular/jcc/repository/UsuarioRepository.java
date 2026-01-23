package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByNfcToken(String nfcToken);
    
    Optional<Usuario> findByUsername(String username);
}
