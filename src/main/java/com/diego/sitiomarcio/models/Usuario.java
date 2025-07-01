package com.diego.sitiomarcio.models;

public class Usuario {
    private String nome;
    private String email;
    private boolean isAdmin;

    public Usuario(String nome, String email, boolean isAdmin) {
        this.nome = nome;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
