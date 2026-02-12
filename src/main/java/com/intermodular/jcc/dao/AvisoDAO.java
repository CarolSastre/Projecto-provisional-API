package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Aviso;
import com.intermodular.jcc.repository.AvisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvisoDAO {

    @Autowired
    private AvisoRepository avisoRepository;

    public void guardarAviso(Aviso aviso) {
        avisoRepository.save(aviso);
    }

    public List<Aviso> obtenerAvisosParaDestinatarios(List<String> destinatarios) {
        // Llama a la query especial que definimos en el repositorio
        return avisoRepository.findByDestinatarioInOrderByFechaDesc(destinatarios);
    }

    public List<Aviso> obtenerTodos() {
        return avisoRepository.findAll();
    }

    public long contarAvisos() {
        return avisoRepository.count();
    }

    public void borrarTodo() {
        avisoRepository.deleteAll();
    }

    public void guardarTodos(List<Aviso> avisos) {
        avisoRepository.saveAll(avisos);
    }
}
