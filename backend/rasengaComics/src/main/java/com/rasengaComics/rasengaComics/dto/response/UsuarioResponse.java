package com.rasengaComics.rasengaComics.dto.response;

public class UsuarioResponse {

    private String uid;
    private String email;
    private String nombre;
    private String rol;

    public UsuarioResponse() {}

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}

