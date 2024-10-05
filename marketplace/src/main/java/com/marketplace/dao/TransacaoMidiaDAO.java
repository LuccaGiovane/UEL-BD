package com.marketplace.dao;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransacaoMidiaDAO {

    String url = "jdbc:postgresql://localhost:5432/postgres?sslmode=prefer";
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");

    // Método para criar nota fiscal e retornar a data de pagamento
    private LocalDateTime criarNotaFiscal(int usuarioId, double valorTotal) throws SQLException {
        String notaFiscalQuery = "INSERT INTO marketplace.nota_fiscal (usuario_id, valor_total) " +
                "VALUES (?, ?) RETURNING dt_pagamento";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(notaFiscalQuery)) {
            stmt.setInt(1, usuarioId);
            stmt.setDouble(2, valorTotal);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Retornar a data de pagamento (dt_pagamento)
                return rs.getTimestamp("dt_pagamento").toLocalDateTime();
            } else {
                throw new SQLException("Erro ao criar nota fiscal.");
            }
        }
    }


    // Método para registrar compra de uma mídia (filme ou série)
    public void comprarMidia(int usuarioId, int midiaId, LocalDateTime dataCompra) throws SQLException {

        //consulta o valor da midia
        String buscarValorMidiaQuery = "SELECT valor FROM marketplace.midia WHERE id = ?";
        double valorMidia = 0;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(buscarValorMidiaQuery)) {
            stmt.setInt(1, midiaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    valorMidia = rs.getDouble("valor");
                } else {
                    throw new SQLException("Mídia não encontrada.");
                }
            }
        }

        //cria a nota fiscal e pega a data de pagamento
        LocalDateTime dtPagamento = criarNotaFiscal(usuarioId, valorMidia);

        String compraMidiaQuery = "INSERT INTO marketplace.compra (usuario_id, midia_id, dt_compra, valor) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(compraMidiaQuery)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, midiaId);
            stmt.setTimestamp(3, Timestamp.valueOf(dtPagamento)); //usa a dt_pagamento retornada
            stmt.setDouble(4, valorMidia);
            stmt.executeUpdate();
        }
    }


    // Método para registrar aluguel de uma mídia (filme ou série)
    public void alugarMidia(int usuarioId, int midiaId) throws SQLException {

        //busca no banco o valor da midia
        String buscarValorMidiaQuery = "SELECT valor FROM marketplace.midia WHERE id = ?";
        double valorMidia = 0;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(buscarValorMidiaQuery)) {
            stmt.setInt(1, midiaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    valorMidia = rs.getDouble("valor");
                } else {
                    throw new SQLException("Mídia não encontrada.");
                }
            }
        }

        //cria a nota fiscal e obtem a data de pagamento
        LocalDateTime dtPagamento = criarNotaFiscal(usuarioId, valorMidia); // Retorna a data de pagamento


        String verificarAluguelQuery = "SELECT dt_expira FROM marketplace.aluguel " +
                "WHERE usuario_id = ? AND midia_id = ? " +
                "AND dt_expira > NOW()";

        // aqui é caso exista um aluguel da mesma midia ativa
        String atualizarAluguelQuery = "UPDATE marketplace.aluguel " +
                "SET dt_expira = dt_expira + INTERVAL '30 days' " +
                "WHERE usuario_id = ? AND midia_id = ?";

        //caso nao existe nenhum aluguel ATIVO da midia em uestao
        String novoAluguelQuery = "INSERT INTO marketplace.aluguel (usuario_id, midia_id, dt_inicio, dt_expira, valor) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);  // Começar transação

            try (PreparedStatement verificarStmt = conn.prepareStatement(verificarAluguelQuery)) {
                verificarStmt.setInt(1, usuarioId);
                verificarStmt.setInt(2, midiaId);

                ResultSet rs = verificarStmt.executeQuery();

                if (rs.next()) {
                    //existe um aluguel ativo, entao estende o prazo de duração
                    try (PreparedStatement atualizarStmt = conn.prepareStatement(atualizarAluguelQuery)) {
                        atualizarStmt.setInt(1, usuarioId);
                        atualizarStmt.setInt(2, midiaId);
                        atualizarStmt.executeUpdate();
                    }
                } else {
                    //nao existe aluguel ativo, então cria um novo
                    try (PreparedStatement novoAluguelStmt = conn.prepareStatement(novoAluguelQuery)) {
                        novoAluguelStmt.setInt(1, usuarioId);
                        novoAluguelStmt.setInt(2, midiaId);
                        novoAluguelStmt.setTimestamp(3, Timestamp.valueOf(dtPagamento)); // Usar dt_pagamento como dt_inicio
                        novoAluguelStmt.setTimestamp(4, Timestamp.valueOf(dtPagamento.plusDays(30))); // Expiração 30 dias depois
                        novoAluguelStmt.setDouble(5, valorMidia);
                        novoAluguelStmt.executeUpdate();
                    }
                }

                conn.commit();  // Finalizar transação
            } catch (SQLException e) {
                conn.rollback();  // Desfazer alterações em caso de erro
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
