package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.Evento;
import com.intermodular.jcc.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class EventoDAO {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> listarPorRango(LocalDate desde, LocalDate hasta) {
        return eventoRepository.findByFechaBetween(desde, hasta);
    }

    public Evento guardar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> buscarPorId(String id) {
        return eventoRepository.findById(id);
    }

    public void borrar(String id) {
        eventoRepository.deleteById(id);
    }
}
