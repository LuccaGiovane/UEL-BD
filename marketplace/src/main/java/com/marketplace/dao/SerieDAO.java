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
