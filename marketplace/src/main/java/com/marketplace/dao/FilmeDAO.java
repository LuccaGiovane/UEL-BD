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


        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(insertMidiaQuery);
            prepararCamposInserir(conn,stmt,filme);
        }
    }


    @Override
    public void remove(Filme filme) throws SQLException {
        String deleteQuery = "UPDATE marketplace.midia SET ativo=FALSE WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, filme.getId());
            stmt.executeUpdate();
        }
    }


    @Override
    public List<Filme> findAll() throws SQLException {
        List<Filme> filmes = new LinkedList<>();
//        Class.forName("com.postgre.jdbc.Driver");

        String query = "SELECT * FROM marketplace.midia WHERE(duracao IS NOT NULL AND ativo=TRUE)";

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
    public void update(Filme filme) throws SQLException {
        String updateQuery = "UPDATE marketplace.midia SET titulo = ?, sinopse = ?, avaliacao = ?, " +
                "poster = ?, atores = ?, dt_lancamento = ?, valor = ?, duracao = ? WHERE id = ? AND ativo=TRUE";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            prepararCamposAtualizar(stmt, filme);
            stmt.executeUpdate();
        }
    }

    @Override
    public Filme findById(int id) throws SQLException {
        String query = "SELECT * FROM marketplace.midia WHERE id = ? AND duracao IS NOT NULL AND ativo=TRUE";
        Filme filme = null;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                filme = new Filme();
                carregaDados(conn, rs, filme); //carrega os dados no objeto Filme
            }
        }

        return filme; //se nao encontrar o filme retorna null mesmo
    }


    @Override
    public void prepararCamposInserir(Connection conn, PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setInt(8, ((Filme) midia).getDuracao());
        super.prepararCamposInserir(conn, stmt, midia);
    }

    @Override
    public void prepararCamposAtualizar(PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setInt(8, ((Filme) midia).getDuracao());
        super.prepararCamposAtualizar(stmt, midia);
    }

    @Override
    protected void carregaDados(Connection conn, ResultSet rs, Midia midia) throws SQLException {
        ((Filme) midia).setDuracao(rs.getInt("duracao"));
        super.carregaDados(conn, rs, midia);
    }
}
