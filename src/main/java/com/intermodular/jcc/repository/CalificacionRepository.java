package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Calificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CalificacionRepository extends MongoRepository<Calificacion, String> {

    // Ver todas las notas de un alumno (Para el perfil del alumno)
    List<Calificacion> findByAlumnoId(String alumnoId);

    // Ver las notas de un alumno en una asignatura (Para el boletín)
    List<Calificacion> findByAlumnoIdAndAsignatura(String alumnoId, String asignatura);

    // Ver todas las notas de una asignatura (Para que el profe vea el rendimiento de la clase)
    List<Calificacion> findByAsignatura(String asignatura);
}
