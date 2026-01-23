package com.intermodular.jcc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "registro_acceso")
public class RegistroAcceso {

    @Id
    private String id;
    private LocalDateTime fechaHora;
    private boolean accesoPermitido;
    private String mensaje;

    @DBRef
    private Usuario usuario;

    public RegistroAcceso() {
    }

    public RegistroAcceso(LocalDateTime fechaHora, boolean accesoPermitido, String mensaje, Usuario usuario) {
        this.fechaHora = fechaHora;
        this.accesoPermitido = accesoPermitido;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isAccesoPermitido() {
        return accesoPermitido;
    }

    public void setAccesoPermitido(boolean accesoPermitido) {
        this.accesoPermitido = accesoPermitido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
