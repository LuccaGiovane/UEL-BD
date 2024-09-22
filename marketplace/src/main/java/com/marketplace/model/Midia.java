package com.marketplace.model;

import java.time.LocalDate;
import java.util.List;

public abstract class Midia {
    private int id;
    private String titulo;
    private String sinopse;
    private List<String> idiomas;
    private List<String> generos;
    private double avaliacao;
    private String poster;
    private String atores;
    private LocalDate dtLancamento;
    private double valor;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public LocalDate getDtLancamento() {
        return dtLancamento;
    }

    public void setDtLancamento(LocalDate dtLancamento) {
        this.dtLancamento = dtLancamento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if(valor<=0)
            this.valor = avaliacao * 10;
        else {
            this.valor = valor;
        }
    }

    @Override
    public String toString() {
        return "Midia{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", sinopse='" + sinopse + '\'' +
                ", idiomas=" + idiomas +
                ", generos=" + generos +
                ", avaliacao=" + avaliacao +
                ", poster='" + poster + '\'' +
                ", atores='" + atores + '\'' +
                ", dtLancamento=" + dtLancamento +
                ", valor=" + valor +
                '}';
    }
}
