package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import dto.FilmeDTO;

public class FilmeDAO
{
    // Usando variáveis de ambiente para o usuário e a senha
    private final String url = "jdbc:postgresql://localhost:5432/marketplace";
    private final String user = System.getenv("DB_USER");           //Usuario do BD
    private final String password = System.getenv("DB_PASSWORD");   //Senha do BD

    public void inserirFilme(FilmeDTO filme) throws SQLException
    {
        String query = "INSERT INTO marketplace.midia (titulo, sinopse, idiomas, generos, avaliacao, poster, atores, dt_lancamento, valor, duracao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query))
        {

            stmt.setString(1, filme.getTitulo());
            stmt.setString(2, filme.getSinopse());
            stmt.setArray(3, conn.createArrayOf("IDIOMA", filme.getIdiomas().toArray()));
            stmt.setArray(4, conn.createArrayOf("GENERO", filme.getGeneros().toArray()));
            stmt.setDouble(5, filme.getAvaliacao());
            stmt.setString(6, filme.getPoster());
            stmt.setString(7, filme.getAtores());
            stmt.setDate(8, java.sql.Date.valueOf(filme.getDtLancamento()));
            stmt.setDouble(9, filme.getValor());
            stmt.setInt(10, filme.getDuracao());

            stmt.executeUpdate();
        }
    }
}
