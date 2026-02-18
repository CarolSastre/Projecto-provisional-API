package com.intermodular.jcc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "avisos")
public class Aviso {

    @Id
    private String id;

    private String titulo;
    private String mensaje;

    // Puede ser "TODOS", "PROFESORES" o el nombre de un curso "2DAM"
    private String destinatario;

    private LocalDateTime fecha;

    @DBRef
    private Usuario emisor; // Quién mandó el aviso (Director, Jefatura...)

    public Aviso() {
    }

    public Aviso(String titulo, String mensaje, String destinatario, Usuario emisor) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.emisor = emisor;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }
}
