package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Falta;
import com.intermodular.jcc.repository.FaltaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; // O @Service

import java.util.List;
import java.util.Optional;

@Component
public class FaltaDAO {

    @Autowired
    private FaltaRepository faltaRepository;

    public void guardarFalta(Falta falta) {
        faltaRepository.save(falta);
    }

    public List<Falta> obtenerFaltasPorAlumno(String alumnoId) {
        return faltaRepository.findByAlumnoId(alumnoId);
    }

    public Optional<Falta> buscarPorId(String id) {
        return faltaRepository.findById(id);
    }

    public long contarFaltas() {
        return faltaRepository.count();
    }

    public void borrarTodo() {
        faltaRepository.deleteAll();
    }
}
