package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.EventoDAO;
import com.intermodular.jcc.entities.Evento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoDAO eventoDAO;

    // Devuelve los eventos de un mes concreto (month 1-12)
    @GetMapping
    public ResponseEntity<List<Evento>> listarEventosMes(@RequestParam int year, @RequestParam int month) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }
        LocalDate desde = LocalDate.of(year, month, 1);
        LocalDate hasta = desde.withDayOfMonth(desde.lengthOfMonth());
        List<Evento> lista = eventoDAO.listarPorRango(desde, hasta);
        return ResponseEntity.ok(lista);
    }

    // Crear evento (solo PROFESOR o ADMIN)
    @PostMapping
    public ResponseEntity<?> crearEvento(@RequestBody Map<String, Object> body,
                                         @AuthenticationPrincipal Jwt jwt) {
        if (!tienePermisoEdicion(jwt)) {
            return ResponseEntity.status(403).body(Map.of("error", "Permisos insuficientes"));
        }
        try {
            Evento ev = mapearDesdeBody(body, null);
            Evento guardado = eventoDAO.guardar(ev);
            return ResponseEntity.ok(guardado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // Actualizar evento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEvento(@PathVariable String id,
                                              @RequestBody Map<String, Object> body,
                                              @AuthenticationPrincipal Jwt jwt) {
        if (!tienePermisoEdicion(jwt)) {
            return ResponseEntity.status(403).body(Map.of("error", "Permisos insuficientes"));
        }
        return eventoDAO.buscarPorId(id)
                .map(existing -> {
                    try {
                        Evento actualizado = mapearDesdeBody(body, existing);
                        actualizado.setId(existing.getId());
                        Evento guardado = eventoDAO.guardar(actualizado);
                        return ResponseEntity.ok(guardado);
                    } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Borrar evento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarEvento(@PathVariable String id,
                                          @AuthenticationPrincipal Jwt jwt) {
        if (!tienePermisoEdicion(jwt)) {
            return ResponseEntity.status(403).body(Map.of("error", "Permisos insuficientes"));
        }
        eventoDAO.borrar(id);
        return ResponseEntity.ok(new HashMap<>());
    }

    private boolean tienePermisoEdicion(Jwt jwt) {
        if (jwt == null) return false;
        Object scopeObj = jwt.getClaim("scope");
        if (scopeObj == null) return false;
        String scope = scopeObj.toString();
        return "PROFESOR".equalsIgnoreCase(scope) || "ADMIN".equalsIgnoreCase(scope);
    }

    private Evento mapearDesdeBody(Map<String, Object> body, Evento base) {
        Evento ev = (base != null) ? base : new Evento();

        String titulo = asString(body.get("titulo"));
        String descripcion = asString(body.get("descripcion"));
        String tipo = asString(body.get("tipo"));
        String fechaStr = asString(body.get("fecha"));
        String horaStr = asString(body.get("horaInicio"));
        Integer duracion = asInt(body.get("duracionMinutos"));

        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Titulo requerido");
        if (fechaStr == null || fechaStr.isBlank()) throw new IllegalArgumentException("Fecha requerida");

        ev.setTitulo(titulo.trim());
        ev.setDescripcion(descripcion != null ? descripcion.trim() : "");
        ev.setTipo(tipo != null && !tipo.isBlank() ? tipo.trim().toUpperCase() : "OTRO");

        LocalDate fecha = LocalDate.parse(fechaStr);
        ev.setFecha(fecha);

        LocalTime hora = (horaStr != null && !horaStr.isBlank()) ? LocalTime.parse(horaStr) : LocalTime.of(8, 0);
        ev.setHoraInicio(hora);

        ev.setDuracionMinutos(duracion != null && duracion > 0 ? duracion : 60);

        return ev;
    }

    private String asString(Object obj) {
        return (obj instanceof String) ? (String) obj : (obj != null ? obj.toString() : null);
    }

    private Integer asInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) {
            try { return Integer.parseInt((String) obj); } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
