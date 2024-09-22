package com.marketplace.dao;

import com.marketplace.model.Midia;
import com.marketplace.model.Serie;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Repository
public class SerieDAO extends MidiaDAO<Serie> {
    public void add(Serie serie) throws SQLException {
        String insertQuery = "INSERT INTO marketplace.midia (titulo, sinopse, avaliacao, poster, atores, " +
                "dt_lancamento, valor, temporadas) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            prepararCamposInserir(conn,stmt,serie);

        }
    }
    public List<Serie> findAll() throws SQLException {
        String query = "SELECT * FROM marketplace.midia WHERE (temporadas IS NOT NULL AND ativo=TRUE)";
        List<Serie> series = new LinkedList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Serie serie = new Serie();
                carregaDados(conn,rs,serie);
                series.add(serie);
            }
        }
        return series;
    }

    @Override
    public void remove(Serie serie) throws SQLException {
        String deleteQuery = "UPDATE marketplace.midia SET ativo=FALSE WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, serie.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Serie serie) throws SQLException {
        String updateQuery = "UPDATE marketplace.midia SET titulo = ?, sinopse = ?, avaliacao = ?, " +
                "poster = ?, atores = ?, dt_lancamento = ?, valor = ?, temporadas = ?" +
                " WHERE id = ? AND ativo=TRUE";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            prepararCamposAtualizar(stmt, serie);
            stmt.executeUpdate();
        }
    }

    @Override
    public Serie findById(int id) throws SQLException {

        String query = "SELECT * FROM marketplace.midia WHERE id = ? AND temporadas IS NOT NULL AND ativo=TRUE";
        Serie serie = null;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                serie = new Serie();
                carregaDados(conn, rs, serie);

            }
        }

        return serie; //retorna null se não encontrar a série
    }


    @Override
    public void prepararCamposInserir(Connection conn, PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setInt(8,((Serie) midia).getTemporadas());
        super.prepararCamposInserir(conn, stmt, midia);
    }

    @Override
    public void prepararCamposAtualizar(PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setInt(8,((Serie) midia).getTemporadas());
        super.prepararCamposAtualizar(stmt, midia);
    }

    @Override
    protected void carregaDados(Connection conn, ResultSet rs, Midia midia) throws SQLException {
        ((Serie) midia).setTemporadas(rs.getInt("temporadas"));
        super.carregaDados(conn, rs, midia);
    }
}
