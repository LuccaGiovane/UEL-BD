package com.marketplace.dao;

import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Repository
public class TransacaoMidiaDAO {

    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");
    String url = "jdbc:postgresql://sicm.dc.uel.br:5432/"+user+"?sslmode=prefer";

    // Método para criar nota fiscal e retornar a data de pagamento
    public void criarNotaFiscal(int usuarioId, String paraCompra, String paraAlugar) throws SQLException {
        String compra = paraCompra;
        if(!paraCompra.isEmpty())  paraCompra+=",";

        String totalValorQuery = "SELECT SUM(midia.valor) AS total FROM marketplace.midia midia WHERE NOT ( midia.id IN ( " +
                "SELECT m.id FROM marketplace.compra c JOIN marketplace.midia m ON c.midia_id=m.id " +
                "WHERE usuario_id = ? AND midia_id IN ("+paraCompra+paraAlugar+"))) " +
                "AND midia.id IN ("+paraCompra+paraAlugar+")";



        String notaFiscalQuery = "INSERT INTO marketplace.nota_fiscal (usuario_id, valor_total) " +
                "VALUES (?, ?) RETURNING dt_pagamento";

        LocalDateTime dtPagamento;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(totalValorQuery)) {
            stmt.setInt(1, usuarioId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                try(PreparedStatement stmtInsert = conn.prepareStatement(notaFiscalQuery)){
                    stmtInsert.setInt(1,usuarioId);
                    stmtInsert.setDouble(2, rs.getDouble("total"));


                    ResultSet rs1 = stmtInsert.executeQuery();
                    if(rs1.next()) {
                        dtPagamento = rs1.getTimestamp("dt_pagamento").toLocalDateTime();

                        if(!compra.isEmpty()) {
                            comprarMidias(usuarioId, compra, dtPagamento);
                        }

                        if(!paraAlugar.isEmpty()) {
                            alugarMidias(usuarioId, paraAlugar, dtPagamento);
                        }
                        // return
                    }
                    else throw new SQLException("Erro ao criar nota fiscal.");
                }
            } else throw new SQLException("Erro ao criar nota fiscal.");
        }
    }


    // Método para registrar a compra de várias mídias
    private void comprarMidias(int usuarioId, String midiaIds, LocalDateTime dtPagamento) throws SQLException {
        // Consulta para verificar se a mídia já foi comprada pelo usuário
        String verificarCompraQuery = "select midia.id as id, midia.valor as valor from marketplace.midia midia where not ( midia.id in ( " +
                "select m.id from marketplace.compra c join marketplace.midia m on c.midia_id=m.id " +
                "where usuario_id = ? and midia_id in ("+midiaIds+"))) " +
                "and midia.id in ("+midiaIds+") ";

        String compraMidiaQuery = "INSERT INTO marketplace.compra (usuario_id, midia_id, dt_compra, valor) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);  // Iniciar a transação
            try (PreparedStatement checkStmt = conn.prepareStatement(verificarCompraQuery)) {
                checkStmt.setInt(1,usuarioId);

                /* aq pega os ids e os valores de cada midia adicionavel */
                try (ResultSet rsVerificar = checkStmt.executeQuery()) {
                    while (rsVerificar.next()) { // para cada midia disponivel...
                        try (PreparedStatement compraStmt = conn.prepareStatement(compraMidiaQuery)) { // insira...
                            compraStmt.setInt(1, usuarioId);
                            compraStmt.setInt(2, rsVerificar.getInt("id"));
                            compraStmt.setTimestamp(3, Timestamp.valueOf(dtPagamento));  // Usar a mesma dt_pagamento para todas
                            compraStmt.setDouble(4, rsVerificar.getDouble("valor"));  // Valor da mídia

                            compraStmt.executeUpdate();
                        }
                    }
                }
            }
            conn.commit();  // Commit da transação
        } catch (SQLException e) { // Rollback em caso de erro
            throw new SQLException("Erro ao registrar compra: " + e.getMessage());
        }
    }


    // Método para registrar o aluguel de várias mídias
    private void alugarMidias(int usuarioId, String midiaIds, LocalDateTime dtPagamento) throws SQLException {

        // Consulta para buscar o valor de cada mídia
        String verificarCompraQuery = "select midia.id as id, midia.valor as valor from marketplace.midia midia where not ( midia.id in ( " +
                "select m.id from marketplace.compra c join marketplace.midia m on c.midia_id=m.id " +
                "where usuario_id = ? and midia_id in ("+midiaIds+"))) " +
                "and midia.id in ("+midiaIds+") ";

        // Query para criar um novo aluguel
        String novoAluguelQuery = "INSERT INTO marketplace.aluguel (usuario_id, midia_id, dt_inicio, dt_expira, valor) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);  // Iniciar transação

            try (PreparedStatement checkStmt = conn.prepareStatement(verificarCompraQuery)) {
                checkStmt.setInt(1,usuarioId);

                /* aq pega os ids e os valores de cada midia adicionavel */
                try (ResultSet rsVerificar = checkStmt.executeQuery()) {
                    while (rsVerificar.next()) { // para cada midia disponivel...
                        try (PreparedStatement novoAluguelStmt = conn.prepareStatement(novoAluguelQuery)) { // insira...
                            novoAluguelStmt.setInt(1, usuarioId);
                            novoAluguelStmt.setInt(2, rsVerificar.getInt("id"));
                            novoAluguelStmt.setTimestamp(3, Timestamp.valueOf(dtPagamento));  // Usar dt_pagamento como dt_inicio
                            novoAluguelStmt.setTimestamp(4, Timestamp.valueOf(dtPagamento.plusDays(30)));  // Expiração 30 dias depois
                            novoAluguelStmt.setDouble(5, rsVerificar.getDouble("valor")*0.15);  // valor = 10% do valor de compra

                            novoAluguelStmt.executeUpdate();
                        }
                    }
                } conn.commit();  // Commit da transação
            } catch (SQLException e) {
                conn.rollback();  // Rollback em caso de erro
                throw e;
            }
        }
    }

    // Método para listar compras por usuário
    public List<Integer> listarComprasPorUsuarioId(int usuarioId) throws SQLException {
        String query = "SELECT midia_id FROM marketplace.compra WHERE usuario_id = ?";
        List<Integer> midiasCompradas = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    midiasCompradas.add(rs.getInt("midia_id"));
                }
            }
        }

        return midiasCompradas;
    }

    // Método para listar aluguéis por usuário
    public List<Integer> listarAlugueisPorUsuarioId(int usuarioId) throws SQLException {
        String query = "SELECT midia_id FROM marketplace.aluguel WHERE usuario_id = ?";
        List<Integer> midiasAlugadas = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    midiasAlugadas.add(rs.getInt("midia_id"));
                }
            }
        }

        return midiasAlugadas;
    }

    // Método para listar compras por mídia
    public List<Integer> listarComprasPorMidiaId(int midiaId) throws SQLException {
        String query = "SELECT usuario_id FROM marketplace.compra WHERE midia_id = ?";
        List<Integer> usuariosQueCompraram = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, midiaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuariosQueCompraram.add(rs.getInt("usuario_id"));
                }
            }
        }

        return usuariosQueCompraram;
    }

    // Método para listar aluguéis por mídia
    public List<Integer> listarAlugueisPorMidiaId(int midiaId) throws SQLException {
        String query = "SELECT usuario_id FROM marketplace.aluguel WHERE midia_id = ?";
        List<Integer> usuariosQueAlugaram = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, midiaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuariosQueAlugaram.add(rs.getInt("usuario_id"));
                }
            }
        }

        return usuariosQueAlugaram;
    }
}