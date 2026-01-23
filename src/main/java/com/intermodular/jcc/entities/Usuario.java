package com.intermodular.jcc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String dni;

    private String nombre;
    private String apellidos;
    private String nfcToken;
    private Rol rol;

    @DBRef
    private Departamento departamento;

    private String curso;
    private boolean vinculadoWebFamilia;
    private boolean expulsado;

    private String username;
    private String password;

    public Usuario() {
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNfcToken() {
        return nfcToken;
    }

    public void setNfcToken(String nfcToken) {
        this.nfcToken = nfcToken;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public boolean isVinculadoWebFamilia() {
        return vinculadoWebFamilia;
    }

    public void setVinculadoWebFamilia(boolean vinculadoWebFamilia) {
        this.vinculadoWebFamilia = vinculadoWebFamilia;
    }

    public boolean isExpulsado() {
        return expulsado;
    }

    public void setExpulsado(boolean expulsado) {
        this.expulsado = expulsado;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
