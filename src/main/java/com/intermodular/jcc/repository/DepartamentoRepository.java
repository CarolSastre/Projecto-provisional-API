package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.Departamento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartamentoRepository extends MongoRepository<Departamento, String> {
}
