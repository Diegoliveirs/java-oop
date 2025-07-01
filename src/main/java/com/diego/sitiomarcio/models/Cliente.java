package com.diego.sitiomarcio.models;

public class Cliente {
    private String nome;
    private String telefone;
    private String observacao;

    public Cliente(String nome, String telefone, String observacao) {
        this.nome = nome;
        this.telefone = telefone;
        this.observacao = observacao;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getObservacao() {
        return observacao;
    }
}
