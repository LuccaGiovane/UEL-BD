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
        String insertMidiaQuery = "INSERT INTO marketplace.midia (titulo, sinopse, avaliacao, poster, atores, " +
                "dt_lancamento, valor, duracao) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(insertMidiaQuery);
            prepararCamposInserir(conn,stmt,serie);
        }
    }
    public List<Serie> findAll() throws SQLException {
        List<Serie> series = new LinkedList<>();
        String query = "SELECT * FROM marketplace.midia WHERE (temporadas IS NOT NULL)";

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
    public void delete(Serie serie) throws SQLException {
        String deleteQuery = "DELETE FROM marketplace.midia WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, serie.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Serie serie) throws SQLException {

        String updateQuery = "UPDATE marketplace.midia SET titulo = ?, sinopse = ?, avaliacao = ?, " +
                "poster = ?, atores = ?, dt_lancamento = ?, valor = ?, duracao = ?, temporadas = ?" +
                " WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            //prepara os campos para atualização
            stmt.setString(1, serie.getTitulo());
            stmt.setString(2, serie.getSinopse());
            stmt.setDouble(3, serie.getAvaliacao());
            stmt.setString(4, serie.getPoster());
            stmt.setString(5, serie.getAtores());
            stmt.setDate(6, Date.valueOf(serie.getDtLancamento()));
            stmt.setDouble(7, serie.getValor());
            stmt.setInt(8, serie.getDuracao());
            stmt.setInt(9, serie.getTemporadas());
            stmt.setInt(10, serie.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public Serie findById(int id) throws SQLException {

        String query = "SELECT * FROM marketplace.midia WHERE id = ? AND temporadas IS NOT NULL";
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
    protected void carregaDados(Connection conn, ResultSet rs, Midia midia) throws SQLException {
        ((Serie) midia).setTemporadas(rs.getInt("temporadas"));
        super.carregaDados(conn, rs, midia);
    }
}
