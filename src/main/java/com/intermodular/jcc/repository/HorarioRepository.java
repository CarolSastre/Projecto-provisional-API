package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Horario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalTime;
import java.util.List;

public interface HorarioRepository extends MongoRepository<Horario, String> {

    // Traducción de la query SQL a MongoDB JSON
    // Buscamos donde: curso sea ?0, dia sea ?1, inicio sea <= hora y fin sea >= hora
    @Query("{ 'curso': ?0, 'diaSemana': ?1, 'horaInicio': { $lte: ?2 }, 'horaFin': { $gte: ?2 } }")
    List<Horario> encontrarHorarioActual(String curso, String dia, LocalTime horaActual);
}
