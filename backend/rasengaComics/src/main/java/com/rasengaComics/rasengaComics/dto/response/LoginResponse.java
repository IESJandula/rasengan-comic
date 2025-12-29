package com.rasengaComics.rasengaComics.dto.response;

public class LoginResponse {
    private String token;
    private UsuarioResponse usuario;

    public LoginResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UsuarioResponse getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponse usuario) { this.usuario = usuario; }
}

