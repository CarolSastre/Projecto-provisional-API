package com.intermodular.jcc.controller;

import com.intermodular.jcc.entities.Calificacion;
import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.CalificacionRepository;
import com.intermodular.jcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/calificaciones")
@CrossOrigin(origins = "*")
public class CalificacionesController {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Poner una nota (Profesor)
    @PostMapping
    public ResponseEntity<?> ponerNota(@RequestBody Map<String, Object> payload) {
        try {
            String alumnoId = (String) payload.get("alumno_id");
            String asignatura = (String) payload.get("asignatura");
            String evaluacion = (String) payload.get("evaluacion");
            // Aseguramos que la nota se lea como Double aunque venga como Integer
            Double nota = Double.valueOf(payload.get("nota").toString());
            String observaciones = (String) payload.get("observaciones");

            Optional<Usuario> alumnoOpt = usuarioRepository.findById(alumnoId);
            if (alumnoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Alumno no encontrado");
            }

            Calificacion nuevaCalificacion = new Calificacion(
                    alumnoOpt.get(),
                    asignatura,
                    evaluacion,
                    nota,
                    observaciones
            );

            calificacionRepository.save(nuevaCalificacion);
            return ResponseEntity.ok("Nota guardada correctamente");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar nota: " + e.getMessage());
        }
    }

    // 2. Consultar notas de un alumno (Alumno/Padres)
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<Calificacion>> getNotasAlumno(@PathVariable String alumnoId) {
        List<Calificacion> notas = calificacionRepository.findByAlumnoId(alumnoId);
        return ResponseEntity.ok(notas);
    }
}
