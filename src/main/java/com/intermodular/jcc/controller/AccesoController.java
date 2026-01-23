package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.HorarioDAO; 
import com.intermodular.jcc.dao.RegistroAccesoDAO;
import com.intermodular.jcc.dao.UsuarioDAO;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/acceso")
@CrossOrigin(origins = "*")
public class AccesoController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RegistroAccesoDAO registroDAO;
    
    @Autowired
    private HorarioDAO horarioDAO; // <--- INYECTAMOS EL NUEVO DAO

    @PostMapping("/validar")
    public ResponseEntity<Map<String, Object>> validarEntrada(@RequestBody Map<String, String> jsonRecibido) {
        
        Map<String, Object> respuesta = new HashMap<>();
        LocalDateTime fechaCompleta = LocalDateTime.now();
        LocalTime horaActual = fechaCompleta.toLocalTime();
        
        // Obtenemos el día de la semana en español (LUNES, MARTES...)
        String diaActual = fechaCompleta.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                .toUpperCase(); 

        String tokenRecibido = jsonRecibido.get("nfcToken"); 
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorNfc(tokenRecibido);

        // ... (CASO 1: TOKEN NO EXISTE -> IGUAL QUE ANTES) ...
        if (usuarioOpt.isEmpty()) { 
             // ... tu código de error ...
             return ResponseEntity.ok(respuesta); // (Resumido para no repetir)
        }

        Usuario usuario = usuarioOpt.get();

        // ... (CASO 2: PROFESOR -> IGUAL QUE ANTES, ellos no suelen tener restricción horaria) ...
        if (usuario.getRol() == Rol.PROFESOR) {
             // ... lógica de profe ...
             return ResponseEntity.ok(respuesta);
        }

        // == CASO 3: ES ALUMNO (Aquí metemos el Horario) ==
        
        // Check A: Expulsado (Igual)
        if (usuario.isExpulsado()) {
            guardarLog(fechaCompleta, false, "Alumno Expulsado", usuario);
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: Alumno sancionado");
            // ... resto del map ...
            return ResponseEntity.ok(respuesta);
        }

        // Check B: Web Familia (Igual)
        if (!usuario.isVinculadoWebFamilia()) {
            guardarLog(fechaCompleta, false, "Falta Web Familia", usuario);
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: No vinculado a Web Familia");
            // ... resto del map ...
            return ResponseEntity.ok(respuesta);
        }

        // == NUEVO CHECK C: HORARIOS ==
        // "Si el curso del alumno NO tiene horario ahora mismo, no entra"
        boolean tieneClase = horarioDAO.tieneClaseAhora(usuario.getCurso(), diaActual, horaActual);
        
        if (!tieneClase) {
            guardarLog(fechaCompleta, false, "Fuera de Horario", usuario);
            
            respuesta.put("permitido", false);
            respuesta.put("mensaje", "ACCESO DENEGADO: No tienes clase ahora (" + diaActual + " " + horaActual.getHour() + ":" + horaActual.getMinute() + ")");
            respuesta.put("nombreUsuario", usuario.getNombre());
            respuesta.put("fechaHora", fechaCompleta.toString());
            
            return ResponseEntity.ok(respuesta);
        }

        // Si pasa todo:
        guardarLog(fechaCompleta, true, "Entrada Alumno", usuario);
        respuesta.put("permitido", true);
        respuesta.put("mensaje", "BIENVENIDO ALUMNO");
        // ...
        return ResponseEntity.ok(respuesta);
    }

    // ... método guardarLog ...

    private void guardarLog(LocalDateTime fechaCompleta, boolean b, String alumno_Expulsado, Usuario usuario) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}