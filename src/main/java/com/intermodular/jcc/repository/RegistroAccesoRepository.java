package com.intermodular.jcc.repository;

import com.intermodular.jcc.entities.RegistroAcceso;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistroAccesoRepository extends MongoRepository<RegistroAcceso, String> {
}
