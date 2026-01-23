package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.HorarioDAO;
import com.intermodular.jcc.dao.RegistroAccesoDAO;
import com.intermodular.jcc.dao.UsuarioDAO;
import com.intermodular.jcc.entities.RegistroAcceso;
import com.intermodular.jcc.entities.Rol;
import com.intermodular.jcc.entities.Usuario;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acceso")
@CrossOrigin(origins = "*")
public class AccesoController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RegistroAccesoDAO registroDAO; 

    @Autowired
    private HorarioDAO horarioDAO; 

    @PostMapping("/validar")
    public ResponseEntity<Map<String, Object>> validarEntrada(@RequestBody Map<String, String> jsonRecibido) {

        Map<String, Object> respuesta = new HashMap<>();
        LocalDateTime fechaCompleta = LocalDateTime.now();
        LocalTime horaActual = fechaCompleta.toLocalTime();

        String diaActual = fechaCompleta.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                .toUpperCase();

        String tokenRecibido = jsonRecibido.get("nfcToken");

        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorNfc(tokenRecibido);

        if (usuarioOpt.isEmpty()) {
            guardarLog(fechaCompleta, false, "Token Desconocido", null);
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: Tarjeta no reconocida");
            return ResponseEntity.ok(respuesta);
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getRol() == Rol.PROFESOR) {
            if (usuario.isExpulsado()) {
                guardarLog(fechaCompleta, false, "Profesor Baja", usuario);
                respuesta.put("permitido", false);
                respuesta.put("mensaje", "ACCESO DENEGADO: Contacte Dirección");
                return ResponseEntity.ok(respuesta);
            }
            guardarLog(fechaCompleta, true, "Entrada Profesor", usuario);
            respuesta.put("permitido", true);
            respuesta.put("mensaje", "BIENVENIDO PROFESOR");
            return ResponseEntity.ok(respuesta);
        }

        if (usuario.isExpulsado()) {
            guardarLog(fechaCompleta, false, "Alumno Expulsado", usuario);
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: Sancionado");
            return ResponseEntity.ok(respuesta);
        }

        if (!usuario.isVinculadoWebFamilia()) {
            guardarLog(fechaCompleta, false, "Falta Web Familia", usuario);
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: Falta Web Familia");
            return ResponseEntity.ok(respuesta);
        }

        boolean tieneClase = horarioDAO.tieneClaseAhora(usuario.getCurso(), diaActual, horaActual);

        if (!tieneClase) {
            guardarLog(fechaCompleta, false, "Fuera de Horario", usuario);
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: No tienes clase ahora");
            return ResponseEntity.ok(respuesta);
        }

        guardarLog(fechaCompleta, true, "Entrada Alumno", usuario);
        respuesta.put("permitido", true);
        respuesta.put("mensaje", "BIENVENIDO ALUMNO");

        return ResponseEntity.ok(respuesta);
    }

    private void guardarLog(LocalDateTime fecha, boolean permitido, String motivo, Usuario usuario) {
        RegistroAcceso log = new RegistroAcceso(fecha, permitido, motivo, usuario);

        registroDAO.guardarRegistro(log);
    }
}
