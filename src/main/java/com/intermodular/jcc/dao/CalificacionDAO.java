package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Calificacion;
import com.intermodular.jcc.repository.CalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalificacionDAO {

    @Autowired
    private CalificacionRepository calificacionRepository;

    public void guardarNota(Calificacion calificacion) {
        calificacionRepository.save(calificacion);
    }

    public List<Calificacion> obtenerNotasPorAlumno(String alumnoId) {
        return calificacionRepository.findByAlumnoId(alumnoId);
    }

    public long contarNotas() {
        return calificacionRepository.count();
    }

    public void borrarTodo() {
        calificacionRepository.deleteAll();
    }
}
