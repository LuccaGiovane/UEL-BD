package com.marketplace.model;

public class Filme extends Midia {
    private int duracao;

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    @Override
    public String toString() {
        return "Filme{" +
                "duracao=" + duracao +
                "} " + super.toString();
    }
}
