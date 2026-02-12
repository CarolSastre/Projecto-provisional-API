package com.intermodular.jcc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "calificaciones")
public class Calificacion {

    @Id
    private String id;

    @DBRef
    private Usuario alumno;

    private String asignatura; // "Programación", "Bases de Datos"
    private String evaluacion; // "1ª Evaluación", "2ª Evaluación", "Final"
    private Double nota;       // 8.5, 5.0, etc.
    private String observaciones; // "Buen trabajo", "Falta entregar práctica"
    private LocalDate fecha;

    public Calificacion() {
    }

    public Calificacion(Usuario alumno, String asignatura, String evaluacion, Double nota, String observaciones) {
        this.alumno = alumno;
        this.asignatura = asignatura;
        this.evaluacion = evaluacion;
        this.nota = nota;
        this.observaciones = observaciones;
        this.fecha = LocalDate.now();
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

    public String getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(String evaluacion) {
        this.evaluacion = evaluacion;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
