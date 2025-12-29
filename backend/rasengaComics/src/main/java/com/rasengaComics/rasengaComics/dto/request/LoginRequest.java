package com.rasengaComics.rasengaComics.dto.request;

public class LoginRequest {
    private String token; // firebase id token or custom token

    public LoginRequest() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

