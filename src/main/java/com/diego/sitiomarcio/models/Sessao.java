package com.diego.sitiomarcio.models;

public class Sessao {
    private static Usuario usuarioAtual;

    public static void logar(Usuario u) {
        usuarioAtual = u;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioAtual;
    }
}
