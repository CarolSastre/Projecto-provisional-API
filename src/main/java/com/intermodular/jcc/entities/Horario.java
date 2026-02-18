package com.intermodular.jcc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalTime;

@Document(collection = "horarios")
public class Horario {

    @Id
    private String id;
    private String curso;       // Ej: "2DAM"
    private String asignatura;  // NUEVO: "Programación", "Recreo", "Interfaces"
    private String diaSemana;   // "LUNES", "MARTES"...
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Horario() {
    }

    public Horario(String curso, String asignatura, String diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        this.curso = curso;
        this.asignatura = asignatura;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // ... Getters y Setters para todos los campos (incluido asignatura) ...
    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    // (Mantén los demás getters y setters igual)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
