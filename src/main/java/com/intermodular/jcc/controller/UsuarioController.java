package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.UsuarioDAO;
import com.intermodular.jcc.entities.Usuario;
import com.intermodular.jcc.repository.UsuarioRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioDAO usuarioDAO;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/{dni}/foto_perfil")
    public ResponseEntity<?> subirFotoPerfil(
            @PathVariable String dni,
            @RequestParam("foto_perfil") MultipartFile archivo // <--- Nombre exacto
    ) {
        // 1. Comprobar usuario
        Usuario usuario = usuarioRepository.findByDni(dni).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Comprobar archivo
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try {
            // 3. Limpiar nombre del archivo
            String nombreOriginal = StringUtils.cleanPath(archivo.getOriginalFilename());

            // Generar nombre único: "DNI_timestamp_nombreOriginal"
            // Ejemplo: "12345678Z_1709988200_perfil.jpg"
            String nombreFichero = dni + "_" + System.currentTimeMillis() + "_" + nombreOriginal;

            // 4. Ruta de destino: Carpeta "uploads" en la raíz del proyecto
            Path rutaUploads = Paths.get("uploads");
            if (!Files.exists(rutaUploads)) {
                Files.createDirectories(rutaUploads);
            }

            // 5. Guardar el archivo físico
            // copy(origen, destino, opciones)
            Path rutaCompleta = rutaUploads.resolve(nombreFichero);
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

            // 6. Guardar SOLO el nombre en la base de datos
            usuario.setFotoPerfil(nombreFichero);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Foto subida correctamente: " + nombreFichero);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al guardar la foto: " + e.getMessage());
        }
    }
}
