package com.marketplace.dao;

import com.marketplace.model.Filme;
import com.marketplace.model.Midia;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
//import org.springframework.

@Repository
public class FilmeDAO extends MidiaDAO<Filme> {

    public void add(Filme filme) throws SQLException {
        String insertMidiaQuery = "INSERT INTO marketplace.midia (titulo, sinopse, avaliacao, poster, atores, " +
                "dt_lancamento, valor, duracao) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";


        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement stmt = conn.prepareStatement(insertMidiaQuery);
            prepararCamposInserir(conn,stmt,filme);
        }
    }

    @Override
    public List<Filme> findAll() throws SQLException {
        List<Filme> filmes = new LinkedList<>();
//        Class.forName("com.postgre.jdbc.Driver");

        String query = "SELECT * FROM marketplace.midia WHERE(duracao IS NOT NULL)";
        System.out.println("USUARIO: "+user+" SENHA: "+password+" BANCO: "+url);

        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Filme filme = new Filme();
                carregaDados(conn,rs,filme);
                filmes.add(filme);
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }


        System.out.println("OK");

        return filmes;
    }

    @Override
    public void prepararCamposInserir(Connection conn, PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setInt(8, ((Filme) midia).getDuracao());
        super.prepararCamposInserir(conn, stmt, midia);
    }

    @Override
    protected void carregaDados(Connection conn, ResultSet rs, Midia midia) throws SQLException {
        ((Filme) midia).setDuracao(rs.getInt("duracao"));
        super.carregaDados(conn, rs, midia);
    }
}
