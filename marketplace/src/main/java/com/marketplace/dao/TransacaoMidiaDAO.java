package com.marketplace.dao;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransacaoMidiaDAO {

    String url = "jdbc:postgresql://localhost:5432/postgres?sslmode=prefer";
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");

    // metodo para registrar compra de uma midia (filme ou série)
    public void comprarMidia(int usuarioId, int midiaId, LocalDate dataCompra) throws SQLException {

        String compraMidiaQuery = "INSERT INTO marketplace.comprou (usuario_id, midia_id, dt_compra) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(compraMidiaQuery)) {
                stmt.setInt(1, usuarioId);     // id do usuario que está comprando
                stmt.setInt(2, midiaId);       // id da midia (filme ou serie)
                stmt.setDate(3, java.sql.Date.valueOf(dataCompra)); // data da compra

                stmt.executeUpdate();
            }
        }
    }

    // metodo para registrar aluguel de uma midia (filme ou serie)
    public void alugarMidia(int usuarioId, int midiaId, LocalDate dataInicio, LocalDate dataExpira) throws SQLException {

        String aluguelMidiaQuery = "INSERT INTO marketplace.alugou (usuario_id, midia_id, dt_inicio, dt_expira) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(aluguelMidiaQuery)) {
                stmt.setInt(1, usuarioId);      // id do usuario q esta alugando
                stmt.setInt(2, midiaId);        // id da midia (filme ou serie)
                stmt.setDate(3, java.sql.Date.valueOf(dataInicio)); // Data de início do aluguel
                stmt.setDate(4, java.sql.Date.valueOf(dataExpira)); // Data de expiração do aluguel

                stmt.executeUpdate();
            }
        }
    }

    // metodo para listar compras por usuario
    public List<Integer> listarComprasPorUsuarioId(int usuarioId) throws SQLException {

        String query = "SELECT midia_id FROM marketplace.comprou WHERE usuario_id = ?";
        List<Integer> midiasCompradas = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, usuarioId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        midiasCompradas.add(rs.getInt("midia_id"));
                    }
                }
            }
        }

        return midiasCompradas;
    }

    // metodo para listar alugueis por usuario
    public List<Integer> listarAlugueisPorUsuarioId(int usuarioId) throws SQLException {

        String query = "SELECT midia_id FROM marketplace.alugou WHERE usuario_id = ?";
        List<Integer> midiasAlugadas = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, usuarioId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        midiasAlugadas.add(rs.getInt("midia_id"));
                    }
                }
            }
        }

        return midiasAlugadas;
    }

    //metodo para listar compras por midia
    public List<Integer> listarComprasPorMidiaId(int midiaId) throws SQLException {

        String query = "SELECT usuario_id FROM marketplace.comprou WHERE midia_id = ?";
        List<Integer> usuariosQueCompraram = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, midiaId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        usuariosQueCompraram.add(rs.getInt("usuario_id"));
                    }
                }
            }
        }

        return usuariosQueCompraram;
    }

    //metodo para listar alugueis por midia
    public List<Integer> listarAlugueisPorMidiaId(int midiaId) throws SQLException {

        String query = "SELECT usuario_id FROM marketplace.alugou WHERE midia_id = ?";
        List<Integer> usuariosQueAlugaram = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, midiaId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        usuariosQueAlugaram.add(rs.getInt("usuario_id"));
                    }
                }
            }
        }

        return usuariosQueAlugaram;
    }
}
