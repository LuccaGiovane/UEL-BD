package com.marketplace.dao;

import com.marketplace.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Repository
public class UsuarioDAO implements DAO<Usuario> {


    @Override
    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new LinkedList<>();
        String query = "SELECT * FROM marketplace.usuario";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Usuario usuario = new Usuario();

                usuario.setNome(rs.getString("nome"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setNasc(rs.getDate("nasc").toLocalDate());

                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    @Override
    public void add(Usuario usuario) throws SQLException {
        String insertMidiaQuery = "INSERT INTO marketplace.usuario (nome, login, senha, nasc)" +
                " VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(insertMidiaQuery);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setDate(4, Date.valueOf(usuario.getNasc()));
        }
    }
}
