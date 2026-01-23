package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Horario;
import com.intermodular.jcc.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class HorarioDAO {

    @Autowired
    private HorarioRepository horarioRepository;

    public boolean tieneClaseAhora(String curso, String dia, LocalTime hora) {
        // Buscamos si hay algún horario que coincida
        List<Horario> horarios = horarioRepository.encontrarHorarioActual(curso, dia, hora);
        // Si la lista no está vacía, es que tiene clase
        return !horarios.isEmpty();
    }
}
