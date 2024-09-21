package com.marketplace.dao;

import com.marketplace.model.Midia;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public abstract class MidiaDAO<M extends Midia> implements DAO<M> {

    public void prepararCamposInserir(Connection conn, PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setString(1, midia.getTitulo());
        stmt.setString(2, midia.getSinopse());
        stmt.setDouble(3, midia.getAvaliacao());
        stmt.setString(4, midia.getPoster());
        stmt.setString(5, midia.getAtores());
        stmt.setDate(6, java.sql.Date.valueOf(midia.getDtLancamento()));
        stmt.setDouble(7, midia.getValor());

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int midiaId = rs.getInt(1);

            inserirIdiomas(midia.getIdiomas(), midiaId, conn);
            inserirGeneros(midia.getGeneros(), midiaId, conn);
        }
    }

    public void prepararCamposAtualizar(PreparedStatement stmt, Midia midia) throws SQLException {
        stmt.setString(1, midia.getTitulo());
        stmt.setString(2, midia.getSinopse());
        stmt.setDouble(3, midia.getAvaliacao());
        stmt.setString(4, midia.getPoster());
        stmt.setString(5, midia.getAtores());
        stmt.setDate(6, Date.valueOf(midia.getDtLancamento()));
        stmt.setDouble(7, midia.getValor());
        stmt.setInt(9, midia.getId());

        stmt.executeUpdate();
    }

    protected void carregaDados(Connection conn, ResultSet rs, Midia midia) throws SQLException {
        midia.setId(rs.getInt("id"));
        midia.setTitulo(rs.getString("titulo"));
        midia.setSinopse(rs.getString("sinopse"));
        midia.setAvaliacao(rs.getDouble("avaliacao"));
        midia.setPoster(rs.getString("poster"));
        midia.setAtores(rs.getString("atores"));
        midia.setDtLancamento(rs.getDate("dt_lancamento").toLocalDate());

        double valor = rs.getDouble("valor");
        if (!rs.wasNull())
            midia.setValor(valor);
        // Recuperar idiomas e gêneros
        midia.setIdiomas(obterIdiomas(rs.getInt("id"), conn));
        midia.setGeneros(obterGeneros(rs.getInt("id"), conn));
    }

    protected void inserirIdiomas(List<String> idiomas, int midiaId, Connection conn) throws SQLException {
        String insertIdiomaQuery = "INSERT INTO marketplace.idiomas_da_midia (midia_id, idioma_id) VALUES (?, ?)";
        String findIdiomaIdQuery = "SELECT id FROM marketplace.idioma WHERE idioma = ?";

        try (PreparedStatement stmtFind = conn.prepareStatement(findIdiomaIdQuery);
             PreparedStatement stmtInsert = conn.prepareStatement(insertIdiomaQuery)) {

            for (String idioma : idiomas) {
                stmtFind.setString(1, idioma);
                ResultSet rs = stmtFind.executeQuery();

                if (rs.next()) {
                    int idiomaId = rs.getInt("id");

                    stmtInsert.setInt(1, midiaId);
                    stmtInsert.setInt(2, idiomaId);
                    stmtInsert.addBatch();
                }
            }
            stmtInsert.executeBatch();
        }
    }

    protected void inserirGeneros(List<String> generos, int midiaId, Connection conn) throws SQLException {
        String insertGeneroQuery = "INSERT INTO marketplace.generos_da_midia (midia_id, genero_id) VALUES (?, ?)";
        String findGeneroIdQuery = "SELECT id FROM marketplace.genero WHERE genero = ?";

        try (PreparedStatement stmtFind = conn.prepareStatement(findGeneroIdQuery);
             PreparedStatement stmtInsert = conn.prepareStatement(insertGeneroQuery)) {

            for (String genero : generos) {
                stmtFind.setString(1, genero);
                ResultSet rs = stmtFind.executeQuery();

                if (rs.next()) {
                    int generoId = rs.getInt("id");

                    stmtInsert.setInt(1, midiaId);
                    stmtInsert.setInt(2, generoId);
                    stmtInsert.addBatch();
                }
            }
            stmtInsert.executeBatch();
        }
    }

    protected List<String> obterIdiomas(int midiaId, Connection conn) throws SQLException {
        List<String> idiomas = new ArrayList<>();

        String query = "SELECT i.idioma FROM marketplace.idioma i " +
                "JOIN marketplace.idiomas_da_midia im ON i.id = im.idioma_id " +
                "WHERE im.midia_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, midiaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                idiomas.add(rs.getString("idioma"));

        }
        return idiomas;
    }

    protected List<String> obterGeneros(int midiaId, Connection conn) throws SQLException {
        List<String> generos = new ArrayList<>();

        String query = "SELECT g.genero FROM marketplace.genero g " +
                "JOIN marketplace.generos_da_midia gm ON g.id = gm.genero_id " +
                "WHERE gm.midia_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, midiaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                generos.add(rs.getString("genero"));

        } return generos;
    }

}
