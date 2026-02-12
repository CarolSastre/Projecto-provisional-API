package com.intermodular.jcc.controller;

import com.intermodular.jcc.entities.Falta;
import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.FaltaRepository;
import com.intermodular.jcc.repository.UsuarioRepository; // Asegúrate de tener este repo o usa tu UsuarioDAO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    @Autowired
    private FaltaRepository faltaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // O usa UsuarioDAO si prefieres

    // 1. Endpoint para PONER UNA FALTA (Lo usa el Profesor)
    @PostMapping("/falta")
    public ResponseEntity<?> ponerFalta(@RequestBody Map<String, Object> payload) {
        try {
            String alumnoId = (String) payload.get("alumno_id");
            String asignatura = (String) payload.get("asignatura_id"); // O nombre
            String observaciones = (String) payload.get("observaciones");
            boolean esRetraso = (boolean) payload.get("tipo").equals("RETRASO");

            // Parsear fecha (asumiendo que viene como String "2026-02-12")
            LocalDate fecha = LocalDate.parse((String) payload.get("fecha"));

            Optional<Usuario> alumnoOpt = usuarioRepository.findById(alumnoId);
            if (alumnoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Alumno no encontrado");
            }

            Falta nuevaFalta = new Falta(
                    alumnoOpt.get(),
                    asignatura,
                    fecha,
                    esRetraso,
                    observaciones
            );

            faltaRepository.save(nuevaFalta);
            return ResponseEntity.ok("Falta registrada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al registrar falta: " + e.getMessage());
        }
    }

    // 2. Endpoint para VER FALTAS (Lo usa el Alumno o Profesor)
    @GetMapping("/alumno/{id}")
    public ResponseEntity<List<Falta>> obtenerFaltasAlumno(@PathVariable String id) {
        List<Falta> faltas = faltaRepository.findByAlumnoId(id);
        return ResponseEntity.ok(faltas);
    }

    // 3. Endpoint para JUSTIFICAR FALTA (Lo usa Jefatura o Profesor)
    @PutMapping("/justificar/{idFalta}")
    public ResponseEntity<?> justificarFalta(@PathVariable String idFalta) {
        Optional<Falta> faltaOpt = faltaRepository.findById(idFalta);
        if (faltaOpt.isPresent()) {
            Falta falta = faltaOpt.get();
            falta.setJustificada(true);
            faltaRepository.save(falta);
            return ResponseEntity.ok("Falta justificada");
        }
        return ResponseEntity.notFound().build();
    }
}
