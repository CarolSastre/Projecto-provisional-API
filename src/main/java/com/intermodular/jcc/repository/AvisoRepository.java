package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Aviso;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface AvisoRepository extends MongoRepository<Aviso, String> {

    // Buscar avisos dirigidos a un destinatario exacto (ej: solo "2DAM")
    List<Aviso> findByDestinatario(String destinatario);

    // Buscar avisos para una lista de destinatarios (ej: "2DAM" Y "TODOS")
    // Esta es la query potente que usaremos
    List<Aviso> findByDestinatarioIn(List<String> destinatarios);

    // Opcional: Obtener los avisos ordenados por fecha (del más nuevo al más viejo)
    List<Aviso> findByDestinatarioInOrderByFechaDesc(List<String> destinatarios);
}
