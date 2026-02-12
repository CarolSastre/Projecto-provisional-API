package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Falta;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FaltaRepository extends MongoRepository<Falta, String> {

    // Buscar faltas de un alumno específico
    List<Falta> findByAlumnoId(String alumnoId);

    // Buscar faltas de un alumno en una asignatura concreta
    List<Falta> findByAlumnoIdAndAsignatura(String alumnoId, String asignatura);
}
