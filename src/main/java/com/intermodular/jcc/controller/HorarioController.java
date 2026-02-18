package com.intermodular.jcc.controller;

import com.intermodular.jcc.entities.Horario;
import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.HorarioRepository;
import com.intermodular.jcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Horario> crearHorario(@RequestBody Horario horario) {
        Horario nuevoHorario = horarioRepository.save(horario);
        return ResponseEntity.ok(nuevoHorario);
    }

    // 2. Crear VARIOS tramos de golpe (Endpoint Batch - ¡NUEVO!)
    // Esto es lo ideal para cargar el horario completo de un día
    @PostMapping("/batch")
    public ResponseEntity<?> crearHorariosMasivos(@RequestBody List<Horario> listaHorarios) {
        List<Horario> guardados = horarioRepository.saveAll(listaHorarios);
        return ResponseEntity.ok("Se han guardado " + guardados.size() + " tramos horarios.");
    }

    // 3. Obtener horario ordenado
    @GetMapping("/grupo/{curso}")
    public ResponseEntity<List<Horario>> getHorarioCurso(@PathVariable String curso) {
        // Aquí podrías ordenar por día y hora si lo soportas en el repositorio
        // o hacerlo con Java streams:
        List<Horario> horarios = horarioRepository.findByCurso(curso);
        // Orden simple por hora de inicio (opcional)
        // horarios.sort(Comparator.comparing(Horario::getHoraInicio));
        return ResponseEntity.ok(horarios);
    }

    // 4. Obtener horario inteligente de un ALUMNO
    // Busca el curso del alumno y devuelve ese horario
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<?> getHorarioAlumno(@PathVariable String alumnoId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(alumnoId);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Alumno no encontrado");
        }

        String cursoDelAlumno = usuarioOpt.get().getCurso();
        if (cursoDelAlumno == null) {
            return ResponseEntity.badRequest().body("El alumno no tiene curso asignado");
        }

        List<Horario> horarios = horarioRepository.findByCurso(cursoDelAlumno);
        return ResponseEntity.ok(horarios);
    }
}
