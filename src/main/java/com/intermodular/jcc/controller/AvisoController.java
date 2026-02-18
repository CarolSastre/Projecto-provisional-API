package com.intermodular.jcc.controller;

import com.intermodular.jcc.entities.Aviso;
import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.AvisoRepository;
import com.intermodular.jcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "*")
public class AvisoController {

    @Autowired
    private AvisoRepository avisoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Crear un aviso (Solo Profesores/Admin)
    @PostMapping
    public ResponseEntity<?> crearAviso(@RequestBody Map<String, String> payload) {
        try {
            String titulo = payload.get("titulo");
            String mensaje = payload.get("mensaje");
            String destinatario = payload.get("destinatario"); // Ej: "TODOS", "2DAM"
            String emisorId = payload.get("emisor_id");

            Optional<Usuario> emisorOpt = usuarioRepository.findById(emisorId);
            if (emisorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Emisor no encontrado");
            }

            Aviso nuevoAviso = new Aviso(titulo, mensaje, destinatario, emisorOpt.get());
            avisoRepository.save(nuevoAviso);

            return ResponseEntity.ok("Aviso publicado correctamente");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al publicar aviso");
        }
    }

    // 2. Leer avisos de un ALUMNO (Inteligente)
    // Devuelve los avisos de su curso + los avisos globales ("TODOS")
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<?> obtenerAvisosAlumno(@PathVariable String alumnoId) {

        Optional<Usuario> alumnoOpt = usuarioRepository.findById(alumnoId);
        if (alumnoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Alumno no encontrado");
        }

        String cursoAlumno = alumnoOpt.get().getCurso(); // Ej: "2DAM"

        // Buscamos avisos que sean para "2DAM" O para "TODOS"
        List<String> objetivos = Arrays.asList(cursoAlumno, "TODOS");

        List<Aviso> misAvisos = avisoRepository.findByDestinatarioInOrderByFechaDesc(objetivos);

        return ResponseEntity.ok(misAvisos);
    }

    // 3. Leer todos los avisos (Para Admin/Profesores que quieren ver el historial)
    @GetMapping
    public ResponseEntity<List<Aviso>> verTodosLosAvisos() {
        return ResponseEntity.ok(avisoRepository.findAll());
    }
}
