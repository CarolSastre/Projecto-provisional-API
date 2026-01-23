package com.intermodular.jcc.dao;

import com.intermodular.jcc.entities.RegistroAcceso;
import com.intermodular.jcc.repository.RegistroAccesoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistroAccesoDAO {

    @Autowired
    private RegistroAccesoRepository registroRepository;

    public void guardarRegistro(RegistroAcceso registro) {
        registroRepository.save(registro);
    }
}