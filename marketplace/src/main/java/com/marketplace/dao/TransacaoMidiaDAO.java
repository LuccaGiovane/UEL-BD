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


    // Método para registrar a compra de várias mídias
    public void comprarMidias(int usuarioId, List<Integer> midiaIds) throws SQLException {

        // Variável para somar o valor total das mídias
        double valorTotal = 0;

        // Consulta para buscar o valor de cada mídia
        String buscarValorMidiaQuery = "SELECT valor FROM marketplace.midia WHERE id = ?";

        // Consulta para verificar se a mídia já foi comprada pelo usuário
        String verificarCompraQuery = "SELECT 1 FROM marketplace.compra WHERE usuario_id = ? AND midia_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);  // Iniciar a transação

            try (PreparedStatement buscarValorStmt = conn.prepareStatement(buscarValorMidiaQuery)) {

                for (int midiaId : midiaIds) {
                    // Para cada mídia, verificar se já foi comprada
                    try (PreparedStatement verificarCompraStmt = conn.prepareStatement(verificarCompraQuery)) {
                        verificarCompraStmt.setInt(1, usuarioId);
                        verificarCompraStmt.setInt(2, midiaId);

                        try (ResultSet rsVerificar = verificarCompraStmt.executeQuery()) {
                            if (rsVerificar.next()) {
                                // O usuário já comprou esta mídia, então ignoramos
                                continue;
                            }
                        }
                    }

                    // Buscar o valor da mídia se ainda não foi comprada
                    buscarValorStmt.setInt(1, midiaId);
                    try (ResultSet rs = buscarValorStmt.executeQuery()) {
                        if (rs.next()) {
                            // Somar o valor da mídia ao total
                            valorTotal += rs.getDouble("valor");
                        } else {
                            throw new SQLException("Mídia não encontrada: " + midiaId);
                        }
                    }
                }
            }

            // Se não houver valor total (nenhuma mídia nova para comprar), não continuar
            if (valorTotal == 0) {
                throw new SQLException("Nenhuma mídia nova para comprar.");
            }

            // Criar uma nota fiscal com o valor total
            LocalDateTime dtPagamento = criarNotaFiscal(usuarioId, valorTotal);

            // Inserir as compras de cada mídia
            String compraMidiaQuery = "INSERT INTO marketplace.compra (usuario_id, midia_id, dt_compra, valor) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement compraStmt = conn.prepareStatement(compraMidiaQuery)) {
                for (int midiaId : midiaIds) {
                    // Verificar novamente se o usuário já comprou a mídia antes de tentar inserir
                    try (PreparedStatement verificarCompraStmt = conn.prepareStatement(verificarCompraQuery)) {
                        verificarCompraStmt.setInt(1, usuarioId);
                        verificarCompraStmt.setInt(2, midiaId);

                        try (ResultSet rsVerificar = verificarCompraStmt.executeQuery()) {
                            if (rsVerificar.next()) {
                                // O usuário já comprou esta mídia, então ignoramos
                                continue;
                            }
                        }
                    }

                    // Inserir a compra da mídia se não foi comprada antes
                    compraStmt.setInt(1, usuarioId);
                    compraStmt.setInt(2, midiaId);
                    compraStmt.setTimestamp(3, Timestamp.valueOf(dtPagamento));  // Usar a mesma dt_pagamento para todas
                    compraStmt.setDouble(4, valorTotal);  // Valor da mídia
                    compraStmt.executeUpdate();
                }
            }

            conn.commit();  // Commit da transação
        } catch (SQLException e) {
            // Rollback em caso de erro
            throw new SQLException("Erro ao registrar compra: " + e.getMessage());
        }
    }



    // Método para registrar o aluguel de várias mídias
    public void alugarMidias(int usuarioId, List<Integer> midiaIds) throws SQLException {

        // Consulta para buscar o valor de cada mídia
        String buscarValorMidiaQuery = "SELECT valor FROM marketplace.midia WHERE id = ?";

        // Consulta para verificar se a mídia já foi comprada pelo usuário
        String verificarCompraQuery = "SELECT 1 FROM marketplace.compra WHERE usuario_id = ? AND midia_id = ?";

        // Consulta para verificar se já existe um aluguel ativo para a mídia
        String verificarAluguelQuery = "SELECT dt_expira FROM marketplace.aluguel " +
                "WHERE usuario_id = ? AND midia_id = ? AND dt_expira > NOW()";

        // Query para atualizar o aluguel se já estiver ativo
        String atualizarAluguelQuery = "UPDATE marketplace.aluguel " +
                "SET dt_expira = dt_expira + INTERVAL '30 days' " +
                "WHERE usuario_id = ? AND midia_id = ?";

        // Query para criar um novo aluguel
        String novoAluguelQuery = "INSERT INTO marketplace.aluguel (usuario_id, midia_id, dt_inicio, dt_expira, valor) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);  // Iniciar transação

            double valorTotal = 0;  // Variável para somar o valor total das mídias

            // Preparar consultas
            try (PreparedStatement buscarValorStmt = conn.prepareStatement(buscarValorMidiaQuery);
                 PreparedStatement verificarCompraStmt = conn.prepareStatement(verificarCompraQuery);
                 PreparedStatement verificarAluguelStmt = conn.prepareStatement(verificarAluguelQuery);
                 PreparedStatement atualizarAluguelStmt = conn.prepareStatement(atualizarAluguelQuery);
                 PreparedStatement novoAluguelStmt = conn.prepareStatement(novoAluguelQuery)) {

                for (int midiaId : midiaIds) {

                    // Verificar se o usuário já comprou a mídia
                    verificarCompraStmt.setInt(1, usuarioId);
                    verificarCompraStmt.setInt(2, midiaId);
                    try (ResultSet rsCompra = verificarCompraStmt.executeQuery()) {
                        if (rsCompra.next()) {
                            // O usuário já comprou esta mídia, então ignoramos o aluguel
                            continue;
                        }
                    }

                    // Buscar o valor da mídia
                    buscarValorStmt.setInt(1, midiaId);
                    double valorMidia;
                    try (ResultSet rsValor = buscarValorStmt.executeQuery()) {
                        if (rsValor.next()) {
                            valorMidia = rsValor.getDouble("valor");
                            valorTotal += valorMidia;  // Somar ao valor total
                        } else {
                            throw new SQLException("Mídia não encontrada: " + midiaId);
                        }
                    }
                }

                // Se o valor total for 0, significa que todas as mídias foram ignoradas (compradas anteriormente)
                if (valorTotal == 0) {
                    throw new SQLException("Nenhuma mídia válida para alugar.");
                }

                // Criar uma única nota fiscal com o valor total das mídias
                LocalDateTime dtPagamento = criarNotaFiscal(usuarioId, valorTotal);

                // Agora, inserir os aluguéis para cada mídia
                for (int midiaId : midiaIds) {
                    // Verificar novamente se o usuário já comprou a mídia
                    verificarCompraStmt.setInt(1, usuarioId);
                    verificarCompraStmt.setInt(2, midiaId);
                    try (ResultSet rsCompra = verificarCompraStmt.executeQuery()) {
                        if (rsCompra.next()) {
                            // Ignorar se já comprada
                            continue;
                        }
                    }

                    // Verificar se já existe um aluguel ativo
                    verificarAluguelStmt.setInt(1, usuarioId);
                    verificarAluguelStmt.setInt(2, midiaId);
                    try (ResultSet rsAluguel = verificarAluguelStmt.executeQuery()) {
                        if (rsAluguel.next()) {
                            // Existe um aluguel ativo, então estendemos o prazo de duração
                            atualizarAluguelStmt.setInt(1, usuarioId);
                            atualizarAluguelStmt.setInt(2, midiaId);
                            atualizarAluguelStmt.executeUpdate();
                        } else {
                            // Não existe aluguel ativo, então criamos um novo
                            novoAluguelStmt.setInt(1, usuarioId);
                            novoAluguelStmt.setInt(2, midiaId);
                            novoAluguelStmt.setTimestamp(3, Timestamp.valueOf(dtPagamento));  // Usar dt_pagamento como dt_inicio
                            novoAluguelStmt.setTimestamp(4, Timestamp.valueOf(dtPagamento.plusDays(30)));  // Expiração 30 dias depois
                            novoAluguelStmt.setDouble(5, valorTotal / midiaIds.size());  // Distribuir o valor médio para cada aluguel
                            novoAluguelStmt.executeUpdate();
                        }
                    }
                }

                conn.commit();  // Commit da transação
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
