package com.marketplace.dao;

import com.marketplace.dto.FilmeDTO;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FilmeDAO
{

    private final String url = "jdbc:postgresql://localhost:5432/marketplace";
    private final String user = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASSWORD");

    public void inserirFilme(FilmeDTO filme) throws SQLException
    {

        String insertMidiaQuery = "INSERT INTO marketplace.midia (titulo, sinopse, avaliacao, poster, atores, dt_lancamento, valor, duracao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(insertMidiaQuery))
        {
            stmt.setString(1, filme.getTitulo());
            stmt.setString(2, filme.getSinopse());
            stmt.setDouble(3, filme.getAvaliacao());
            stmt.setString(4, filme.getPoster());
            stmt.setString(5, filme.getAtores());
            stmt.setDate(6, java.sql.Date.valueOf(filme.getDtLancamento()));
            stmt.setDouble(7, filme.getValor());
            stmt.setInt(8, filme.getDuracao());

            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                int midiaId = rs.getInt(1);

                inserirIdiomas(filme.getIdiomas(), midiaId, conn);
                inserirGeneros(filme.getGeneros(), midiaId, conn);
            }
        }
    }

    private void inserirIdiomas(List<String> idiomas, int midiaId, Connection conn) throws SQLException
    {
        String insertIdiomaQuery = "INSERT INTO marketplace.idiomas_da_midia (midia_id, idioma_id) VALUES (?, ?)";
        String findIdiomaIdQuery = "SELECT id FROM marketplace.idioma WHERE idioma = ?";

        try (PreparedStatement stmtFind = conn.prepareStatement(findIdiomaIdQuery);
             PreparedStatement stmtInsert = conn.prepareStatement(insertIdiomaQuery))
        {

            for (String idioma : idiomas)
            {
                stmtFind.setString(1, idioma);
                ResultSet rs = stmtFind.executeQuery();

                if (rs.next())
                {
                    int idiomaId = rs.getInt("id");

                    stmtInsert.setInt(1, midiaId);
                    stmtInsert.setInt(2, idiomaId);
                    stmtInsert.addBatch();
                }
            }
            stmtInsert.executeBatch();
        }
    }

    private void inserirGeneros(List<String> generos, int midiaId, Connection conn) throws SQLException
    {
        String insertGeneroQuery = "INSERT INTO marketplace.generos_da_midia (midia_id, genero_id) VALUES (?, ?)";
        String findGeneroIdQuery = "SELECT id FROM marketplace.genero WHERE genero = ?";

        try (PreparedStatement stmtFind = conn.prepareStatement(findGeneroIdQuery);
             PreparedStatement stmtInsert = conn.prepareStatement(insertGeneroQuery))
        {
            for (String genero : generos)
            {
                stmtFind.setString(1, genero);
                ResultSet rs = stmtFind.executeQuery();

                if (rs.next())
                {
                    int generoId = rs.getInt("id");

                    stmtInsert.setInt(1, midiaId);
                    stmtInsert.setInt(2, generoId);
                    stmtInsert.addBatch();
                }
            }
            stmtInsert.executeBatch();
        }
    }

    public List<FilmeDTO> listarFilmes()
    {
        List<FilmeDTO> filmes = new ArrayList<>();
        String query = "SELECT * FROM marketplace.midia";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query))
        {

            while (rs.next())
            {
                FilmeDTO filme = new FilmeDTO();

                filme.setTitulo(rs.getString("titulo"));
                filme.setSinopse(rs.getString("sinopse"));
                filme.setAvaliacao(rs.getDouble("avaliacao"));
                filme.setPoster(rs.getString("poster"));
                filme.setAtores(rs.getString("atores"));
                filme.setDtLancamento(rs.getDate("dt_lancamento").toLocalDate());
                filme.setDuracao(rs.getInt("duracao"));

                double valor = rs.getDouble("valor");
                if (!rs.wasNull())
                {
                    filme.setValor(valor);
                }

                // Recuperar idiomas e gêneros
                filme.setIdiomas(obterIdiomas(rs.getInt("id"), conn));
                filme.setGeneros(obterGeneros(rs.getInt("id"), conn));

                filmes.add(filme);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return filmes;
    }

    private List<String> obterIdiomas(int midiaId, Connection conn) throws SQLException
    {
        List<String> idiomas = new ArrayList<>();

        String query = "SELECT i.idioma FROM marketplace.idioma i " +
                "JOIN marketplace.idiomas_da_midia im ON i.id = im.idioma_id " +
                "WHERE im.midia_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setInt(1, midiaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                idiomas.add(rs.getString("idioma"));
            }
        }
        return idiomas;
    }

    private List<String> obterGeneros(int midiaId, Connection conn) throws SQLException
    {
        List<String> generos = new ArrayList<>();

        String query = "SELECT g.genero FROM marketplace.genero g " +
                "JOIN marketplace.generos_da_midia gm ON g.id = gm.genero_id " +
                "WHERE gm.midia_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setInt(1, midiaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                generos.add(rs.getString("genero"));
            }
        }
        return generos;
    }
}
