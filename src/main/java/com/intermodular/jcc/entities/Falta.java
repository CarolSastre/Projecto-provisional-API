package com.intermodular.jcc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "faltas")
public class Falta {

    @Id
    private String id;

    @DBRef
    private Usuario alumno; // Relación con el alumno

    private String asignatura; // Ej: "Programación", "Matemáticas"
    private LocalDate fecha;   // Solo la fecha (YYYY-MM-DD)
    private boolean retraso;   // true = retraso, false = falta completa
    private boolean justificada;
    private String observaciones;

    public Falta() {
    }

    public Falta(Usuario alumno, String asignatura, LocalDate fecha, boolean retraso, String observaciones) {
        this.alumno = alumno;
        this.asignatura = asignatura;
        this.fecha = fecha;
        this.retraso = retraso;
        this.justificada = false; // Por defecto no justificada
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getAlumno() {
        return alumno;
    }

    public void setAlumno(Usuario alumno) {
        this.alumno = alumno;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isRetraso() {
        return retraso;
    }

    public void setRetraso(boolean retraso) {
        this.retraso = retraso;
    }

    public boolean isJustificada() {
        return justificada;
    }

    public void setJustificada(boolean justificada) {
        this.justificada = justificada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
