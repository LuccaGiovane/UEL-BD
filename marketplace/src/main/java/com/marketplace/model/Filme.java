package com.marketplace.model;

public class Filme extends Midia {

    private int id;
    private int duracao;

    public int getDuracao() {
        return duracao;
    }
    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Filme: "+this.getTitulo() + ", tempo: "+duracao;
    }

}
