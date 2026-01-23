package com.intermodular.jcc.controller;

import com.intermodular.jcc.dao.UsuarioDAO;
import com.intermodular.jcc.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}