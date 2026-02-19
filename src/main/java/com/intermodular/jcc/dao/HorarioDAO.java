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

    // Método original (NFC)
    public boolean tieneClaseAhora(String curso, String dia, LocalTime hora) {
        // Buscamos si hay algún horario que coincida
        List<Horario> horarios = horarioRepository.encontrarHorarioActual(curso, dia, hora);
        // Si la lista no está vacía, es que tiene clase
        return !horarios.isEmpty();
    }

    // --- NUEVOS MÉTODOS PARA COMPATIBILIDAD CON LA API Y ESCRITORIO ---

    // Obtener todo el horario de un curso
    public List<Horario> obtenerHorarioPorCurso(String curso) {
        return horarioRepository.findByCurso(curso);
    }

    // Guardar un solo tramo horario
    public Horario guardarHorario(Horario horario) {
        return horarioRepository.save(horario);
    }

    // Guardar varios tramos a la vez (útil para la carga masiva)
    public List<Horario> guardarTodos(List<Horario> horarios) {
        return horarioRepository.saveAll(horarios);
    }

    // Borrar todos (útil para pruebas)
    public void borrarTodo() {
        horarioRepository.deleteAll();
    }
}