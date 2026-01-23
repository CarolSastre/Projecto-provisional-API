package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// Fíjate: <Usuario, String> porque el ID ahora es String
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByNfcToken(String nfcToken);
}
