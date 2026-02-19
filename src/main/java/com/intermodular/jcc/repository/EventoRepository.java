package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends MongoRepository<Evento, String> {

    List<Evento> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
