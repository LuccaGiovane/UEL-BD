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
        String query = "SELECT * FROM marketplace.usuario WHERE ativo=TRUE";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
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
        String insertUsuarioQuery = "INSERT INTO marketplace.usuario (nome, login, senha, nasc)" +
                " VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(insertUsuarioQuery);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setDate(4, Date.valueOf(usuario.getNasc()));

            stmt.executeQuery();
        }
    }

    @Override
    public void remove(Usuario usuario) throws SQLException {
        String deleteQuery = "UPDATE marketplace.usuario SET ativo=FALSE WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            System.out.println(usuario.getId());
            stmt.setInt(1, usuario.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Usuario usuario) throws SQLException {
        String updateQuery = "UPDATE marketplace.usuario SET nome = ?, login = ?, senha = ?, " +
                "nasc = ? WHERE id = ? AND ativo=TRUE";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setDate(4, Date.valueOf(usuario.getNasc()));
            stmt.setInt(5, usuario.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public Usuario findById(int id) throws SQLException {
        String query = "SELECT * FROM marketplace.usuario WHERE id = ? AND ativo=TRUE";
        Usuario usuario = null;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setNasc(rs.getDate("nasc").toLocalDate());
            }
        }
        return usuario;
    }
}
