package com.marketplace.model;

public class Serie extends Midia {
    private int temporadas;
    private int id;

    private int duracao;

    public int getTemporadas() { return temporadas; }

    public void setTemporadas(int temporadas) { this.temporadas = temporadas; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
}
