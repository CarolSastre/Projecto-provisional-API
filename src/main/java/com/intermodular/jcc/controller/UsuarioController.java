package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.UsuarioDAO;
import com.intermodular.jcc.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Sort;
import java.util.Date;
import java.util.HashMap;
import com.intermodular.jcc.entities.Rol;
import com.mongodb.client.result.DeleteResult;
import java.time.format.DateTimeParseException;
import org.bson.types.ObjectId;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioDAO.guardarUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    public void borrarUsuario(@PathVariable String id) {
        usuarioDAO.borrarUsuario(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String dni = credentials.get("dni");
        String password = credentials.get("password");

        if (dni == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "DNI y contraseña son requeridos."));
        }

        Usuario usuario = usuarioDAO.buscarPorDni(dni);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        // Generar el token JWT
        Instant now = Instant.now();
        String scope = usuario.getRol().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600L)) // Expira en 1 hora
                .subject(usuario.getUsername())
                .claim("scope", scope)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Devolver el token y los datos del usuario
        return ResponseEntity.ok(Map.of(
                "usuario", usuario,
                "token", token));
    }

    // Login mediante NFC: recibe UID y emite JWT para el usuario asociado
    @PostMapping("/login-nfc")
    public ResponseEntity<?> loginPorNfc(@RequestBody Map<String, String> payload) {
        String uid = payload.get("uid");
        if (uid == null || uid.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "UID NFC requerido."));
        }

        Usuario usuario = usuarioDAO.buscarPorNfcToken(uid);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("error", "NFC no asociado a ningún usuario."));
        }

        Instant now = Instant.now();
        String scope = usuario.getRol().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600L))
                .subject(usuario.getUsername())
                .claim("scope", scope)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(Map.of(
                "usuario", usuario,
                "token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getDni() == null || usuario.getPassword() == null || usuario.getNombre() == null
                || usuario.getApellidos() == null || usuario.getGmail() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Todos los campos obligatorios deben ser completados."));
        }

        // Verificar si el usuario ya existe
        Usuario usuarioExistente = usuarioDAO.buscarPorEmail(usuario.getGmail());
        if (usuarioExistente != null) {
            return ResponseEntity.status(409).body(Map.of("error", "El usuario ya está registrado."));
        }

        // Encriptar la contraseña antes de guardar el usuario
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Generar un token NFC único para el usuario
        String nfcToken = "NFC" + System.currentTimeMillis();
        usuario.setNfcToken(nfcToken);

        // Guardar el nuevo usuario
        Usuario nuevoUsuario = usuarioDAO.guardarUsuario(usuario);

        // Log para confirmar que el nfcToken se generó y asignó correctamente
        System.out.println("Usuario registrado con NFC Token: " + nfcToken);

        return ResponseEntity.ok(Map.of("usuario", nuevoUsuario, "nfcToken", nfcToken));
    }

    // Asignar NFC a un usuario (garantizando unicidad del UID)
    @PostMapping("/asignar-nfc")
    public ResponseEntity<?> asignarNfc(@RequestBody Map<String, String> body) {
        String uid = body.get("uid");
        String userId = body.get("userId");
        String dni = body.get("dni");
        String gmail = body.get("gmail");

        if (uid == null || uid.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "UID NFC requerido."));
        }

        // Verificar si UID ya está asignado a otro usuario
        Usuario owner = usuarioDAO.buscarPorNfcToken(uid);
        if (owner != null) {
            // Si es el mismo usuario, idempotente
            Usuario targetCheck = null;
            if (userId != null) {
                targetCheck = usuarioDAO.buscarPorId(userId);
            }
            if (targetCheck == null && dni != null) {
                targetCheck = usuarioDAO.buscarPorDni(dni);
            }
            if (targetCheck == null && gmail != null) {
                targetCheck = usuarioDAO.buscarPorEmail(gmail);
            }
            if (targetCheck == null || !owner.getId().equals(targetCheck.getId())) {
                return ResponseEntity.status(409).body(Map.of("error", "Este NFC ya está asignado a otro usuario."));
            }
            // Mismo usuario: devolver OK
            return ResponseEntity.ok(Map.of("usuario", owner, "nfcToken", uid));
        }

        // Resolver usuario destino
        Usuario usuario = null;
        if (userId != null) {
            usuario = usuarioDAO.buscarPorId(userId);
        }
        if (usuario == null && dni != null) {
            usuario = usuarioDAO.buscarPorDni(dni);
        }
        if (usuario == null && gmail != null) {
            usuario = usuarioDAO.buscarPorEmail(gmail);
        }
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        usuario.setNfcToken(uid);
        Usuario saved = usuarioDAO.guardarUsuario(usuario);
        return ResponseEntity.ok(Map.of("usuario", saved, "nfcToken", uid));
    }

    // Registrar entrada/salida alternando por usuario del UID (opcionalmente reforzar usuario activo)
    @PostMapping("/registrar-entrada-salida")
    public ResponseEntity<?> registrarEntradaSalida(@RequestBody Map<String, String> body) {
        String uid = body.get("uid");
        String userId = body.get("userId"); // opcional: refuerza que el UID pertenezca al usuario activo

        if (uid == null || uid.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "UID NFC requerido."));
        }

        Usuario usuario = usuarioDAO.buscarPorNfcToken(uid);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("error", "NFC no asociado a ningún usuario."));
        }

        if (userId != null && !userId.isBlank() && !usuario.getId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "El NFC no pertenece al usuario activo."));
        }

        // Buscar último registro para alternar
        Query q = new Query(Criteria.where("userId").is(usuario.getId()))
                .with(Sort.by(Sort.Direction.DESC, "fechaHora"))
                .limit(1);
        Map last = mongoTemplate.findOne(q, Map.class, "registrosEntradaSalida");
        String tipo = "entrada";
        if (last != null && "entrada".equals(String.valueOf(last.get("tipo")))) {
            tipo = "salida";
        }

        Map<String, Object> registro = new HashMap<>();
        registro.put("userId", usuario.getId());
        registro.put("nombre", usuario.getNombre());
        registro.put("email", usuario.getGmail());
        registro.put("fechaHora", new Date());
        registro.put("tipo", tipo);
        registro.put("uid", uid);

        mongoTemplate.insert(registro, "registrosEntradaSalida");
        return ResponseEntity.ok(Map.of("ok", true, "registro", registro));
    }

    // Registrar entrada/salida asociada a la sesión de la app (sin depender de NFC)
    @PostMapping("/registrar-entrada-salida-app")
    public ResponseEntity<?> registrarEntradaSalidaApp(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String tipo = body.getOrDefault("tipo", "entrada"); // "entrada" o "salida"

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId requerido"));
        }

        Usuario usuario = usuarioDAO.buscarPorId(userId);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        Map<String, Object> registro = new HashMap<>();
        registro.put("userId", usuario.getId());
        registro.put("nombre", usuario.getNombre());
        registro.put("email", usuario.getGmail());
        registro.put("fechaHora", new Date());
        registro.put("tipo", tipo);
        registro.put("uid", body.getOrDefault("uid", "APP"));

        mongoTemplate.insert(registro, "registrosEntradaSalida");
        return ResponseEntity.ok(Map.of("ok", true, "registro", registro));
    }

    // Listar registros de entrada/salida para un usuario
    @GetMapping("/registros")
    public ResponseEntity<?> listarRegistros(@RequestParam("userId") String userId,
            @RequestParam(value = "limit", required = false, defaultValue = "50") int limit) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId requerido"));
        }

        Query q = new Query(Criteria.where("userId").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "fechaHora"))
                .limit(Math.max(1, Math.min(200, limit)));
        List<Map> list = mongoTemplate.find(q, Map.class, "registrosEntradaSalida");
        return ResponseEntity.ok(Map.of("ok", true, "registros", list));
    }

    // Listar registros de entrada/salida de todo el centro en un mes concreto
    @GetMapping("/registros-mes")
    public ResponseEntity<?> listarRegistrosMes(@RequestParam("year") int year,
            @RequestParam("month") int month) {
        // month: 1-12
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(Map.of("error", "month debe estar entre 1 y 12"));
        }
        if (year < 1970 || year > 9999) {
            return ResponseEntity.badRequest().body(Map.of("error", "year no válido"));
        }
        // Rango [primer día del mes, primer día del mes siguiente)
        java.time.LocalDate start = java.time.LocalDate.of(year, month, 1);
        java.time.LocalDate end = start.plusMonths(1);
        Date from = Date.from(start.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        Date to = Date.from(end.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

        Query q = new Query(Criteria.where("fechaHora").gte(from).lt(to))
                .with(Sort.by(Sort.Direction.ASC, "fechaHora"));
        List<Map> list = mongoTemplate.find(q, Map.class, "registrosEntradaSalida");

        // Enriquecer cada registro con el rol actual del usuario para poder filtrar por tipo desde el front
        java.util.Map<String, com.intermodular.jcc.entities.Rol> cache = new java.util.HashMap<>();
        for (Map reg : list) {
            Object userIdObj = reg.get("userId");
            if (userIdObj == null) {
                continue;
            }
            String uid = String.valueOf(userIdObj);
            com.intermodular.jcc.entities.Rol rol = cache.get(uid);
            if (rol == null && !cache.containsKey(uid)) {
                var u = usuarioDAO.buscarPorId(uid);
                if (u != null) {
                    rol = u.getRol();
                }
                cache.put(uid, rol);
            }
            if (rol != null) {
                reg.put("rol", rol.name());
            }
        }

        return ResponseEntity.ok(Map.of("ok", true, "registros", list));
    }

    // Actualizar un registro de entrada/salida (solo para perfiles con permisos desde el front)
    @PostMapping("/registros/actualizar")
    public ResponseEntity<?> actualizarRegistro(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "id requerido"));
        }

        Update update = new Update();

        Object tipoObj = body.get("tipo");
        if (tipoObj instanceof String) {
            String tipo = ((String) tipoObj).trim().toLowerCase();
            if (!tipo.isEmpty()) {
                update.set("tipo", tipo);
            }
        }

        Object fechaIsoObj = body.get("fechaHoraIso");
        if (fechaIsoObj instanceof String) {
            String iso = ((String) fechaIsoObj).trim();
            if (!iso.isEmpty()) {
                try {
                    Instant inst = Instant.parse(iso);
                    Date fecha = Date.from(inst);
                    update.set("fechaHora", fecha);
                } catch (DateTimeParseException ignored) {
                    return ResponseEntity.badRequest().body(Map.of("error", "fechaHoraIso no válida"));
                }
            }
        }

        if (update.getUpdateObject().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ningún cambio a aplicar"));
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "id de registro no válido"));
        }

        Query q = new Query(Criteria.where("_id").is(objectId));
        var result = mongoTemplate.updateFirst(q, update, "registrosEntradaSalida");
        if (result.getMatchedCount() == 0) {
            return ResponseEntity.status(404).body(Map.of("error", "Registro no encontrado"));
        }

        Map updated = mongoTemplate.findOne(q, Map.class, "registrosEntradaSalida");
        return ResponseEntity.ok(Map.of("ok", true, "registro", updated));
    }

    // Eliminar un registro de entrada/salida
    @PostMapping("/registros/eliminar")
    public ResponseEntity<?> eliminarRegistro(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "id requerido"));
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "id de registro no válido"));
        }

        Query q = new Query(Criteria.where("_id").is(objectId));
        DeleteResult res = mongoTemplate.remove(q, "registrosEntradaSalida");
        if (res.getDeletedCount() == 0) {
            return ResponseEntity.status(404).body(Map.of("error", "Registro no encontrado"));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Actualizar perfil de usuario (admin)
    @PostMapping("/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("id");
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "id requerido"));
        }
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
        // Campos editables básicos
        Object dniObj = body.get("dni");
        if (dniObj instanceof String) {
            usuario.setDni(((String) dniObj).trim().toUpperCase());
        }
        Object nombreObj = body.get("nombre");
        if (nombreObj instanceof String) {
            usuario.setNombre(((String) nombreObj).trim());
        }
        Object apellidosObj = body.get("apellidos");
        if (apellidosObj instanceof String) {
            usuario.setApellidos(((String) apellidosObj).trim());
        }
        Object gmailObj = body.get("gmail");
        if (gmailObj instanceof String) {
            usuario.setGmail(((String) gmailObj).trim().toLowerCase());
        }
        Object fotoObj = body.get("fotoPerfil");
        if (fotoObj instanceof String) {
            usuario.setFotoPerfil(((String) fotoObj));
        }
        Object rolObj = body.get("rol");
        if (rolObj instanceof String) {
            try {
                usuario.setRol(Rol.valueOf(((String) rolObj).trim().toUpperCase()));
            } catch (Exception ignored) {
            }
        }
        Object bajaObj = body.get("baja");
        if (bajaObj instanceof Boolean) {
            usuario.setBaja((Boolean) bajaObj);
        }
        Object verifObj = body.get("verificado");
        if (verifObj instanceof Boolean) {
            usuario.setVerificado((Boolean) verifObj);
        }

        Usuario saved = usuarioDAO.guardarUsuario(usuario);
        return ResponseEntity.ok(Map.of("ok", true, "usuario", saved));
    }
}
