package com.marketplace.dao;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Date;

@Repository
public class RelatorioDAO {
    String user = System.getenv("DB_USER");
    String url = "jdbc:postgresql://sicm.dc.uel.br:5432/"+user+"?sslmode=prefer";
    String password = System.getenv("DB_PASSWORD");

    public enum FINALIDADE {COMPRA,ALUGUEL};

    public String porPeriodoTabela(int anoInicio, int anoFim, FINALIDADE f) throws SQLException {
        if (anoFim < anoInicio) return null;
        StringBuilder resultado = new StringBuilder();  // StringBuilder pra fazer a tabela

        // Cabeçalho com a descriçao do relatorio
        resultado.append("==================================\n");
        resultado.append("Relatório de ").append(f == FINALIDADE.COMPRA ? "Vendas" : "Aluguéis").append(" Por Período:\n");
        String periodo = "[" + anoInicio + "/" + anoFim + "]";
        resultado.append(String.format("%" + ((34 + periodo.length()) / 2) + "s", periodo)).append("\n");
        resultado.append("==================================\n");
        // Cabeçalho da tabela
        resultado.append(String.format("%-6s | %-6s | %-12s |\n", "Ano", "Mês", f == FINALIDADE.COMPRA ? "Compras" : "Aluguéis"));
        resultado.append("----------------------------------\n");

        LocalDate inicio = LocalDate.of(anoInicio, 1, 1);
        LocalDate fim = LocalDate.of(anoFim, 12, 1);  // Até o final do ano
        int anoAnterior = -1;  // gambizinha pra ver quando o ano muda xDD

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            while (inicio.isBefore(fim) || inicio.isEqual(fim)) {
                PreparedStatement stmt = null;
                switch (f) {
                    case COMPRA -> stmt = conn.prepareStatement(queryVendasPorPeriodo(inicio));
                    case ALUGUEL -> stmt = conn.prepareStatement(queryAlugueisPorPeriodo(inicio));
                }

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Se mudar o ano poe esses "- - - - - " pra facilitar a visuzalização
                    if (anoAnterior != -1 && anoAnterior != inicio.getYear())
                        resultado.append("-  -  -  -  -  -  -  -  -  -  -  -\n");

                    for (Month m : Month.values()) {
                        String mes = m.toString().substring(0, 3);
                        int quantidade = rs.getInt(m.toString());

                        // Adicionar a linha na tabela formatada
                        resultado.append(String.format("%-6d | %-6s | %12d |\n", inicio.getYear(), mes, quantidade));
                    }
                    // Atualizar o ano anterior
                    anoAnterior = inicio.getYear();
                } inicio = inicio.plusYears(1);
            }
        } return resultado.toString();
    }

    private String queryVendasPorPeriodo(LocalDate ano){
        StringBuilder query = new StringBuilder("select ");

        for(Month m : Month.values()){
            query.append(" count(case when date('");
            query.append(ano.toString());
            query.append("') <= dt_pagamento and dt_pagamento < (date('");
            query.append(ano.toString());
            query.append("')+interval '1 month') then 1 else null end) as ");
            query.append(m);
            if(m!=Month.DECEMBER) query.append(",");

            ano = ano.plusMonths(1);
        } query.append(" from (marketplace.nota_fiscal nf join marketplace.compra c on c.usuario_id=nf.usuario_id and c.dt_compra=dt_pagamento)");
        return query.toString();
    }

    private String queryAlugueisPorPeriodo(LocalDate ano){
        StringBuilder query = new StringBuilder("select ");

        for(Month m : Month.values()){
            query.append(" count(case when date('");
            query.append(ano.toString());
            query.append("') <= dt_pagamento and dt_pagamento < (date('");
            query.append(ano.toString());
            query.append("')+interval '1 month') then 1 else null end) as ");
            query.append(m);
            if(m!=Month.DECEMBER) query.append(",");

            ano = ano.plusMonths(1);
        } query.append(" from (marketplace.nota_fiscal nf join marketplace.aluguel a on a.usuario_id=nf.usuario_id and a.dt_inicio=dt_pagamento)");
        return query.toString();
    }

    public String maisVendidos() throws SQLException {
        StringBuilder resultado = new StringBuilder();

        int linhaTamanho = 60;

        // centralizar o titulo
        String tituloRelatorio = "Relatório de top 10 mais vendidos";
        int padding = (linhaTamanho - tituloRelatorio.length()) / 2;

        // Cabeçalho com a descrição do relatório
        resultado.append("=".repeat(linhaTamanho + 2)).append("\n");
        resultado.append(" ".repeat(padding)).append(tituloRelatorio).append("\n");
        resultado.append("=".repeat(linhaTamanho + 2)).append("\n");

        // Cabeçalho da tabela
        resultado.append(String.format("%-11s | %-30s | %-15s\n", "Posição", "Título", "Total Vendidos"));
        resultado.append("-".repeat(linhaTamanho + 2)).append("\n");

        // Query para buscar os 10 mais vendidos
        String query = "select count(1) as total, m.titulo as titulo from marketplace.nota_fiscal nf " +
                "join marketplace.compra c on c.usuario_id=nf.usuario_id and c.dt_compra=dt_pagamento " +
                "join marketplace.midia m on m.id=c.midia_id " +
                "group by m.titulo order by total desc limit 10";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String titulo = rs.getString("titulo");
                int total = rs.getInt("total");

                // Adicionar cada item na tabela formatada
                resultado.append(String.format("%-11s | %-30s | %-15d\n", rank + "º", titulo, total));
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao gerar o relatório: " + e.getMessage();
        }

        return resultado.toString();
    }


    public String maisAlugados(int anoInicio, int anoFim) throws SQLException {
        StringBuilder resultado = new StringBuilder();


        int linhaTamanho = 60;

        // centralizar o título
        String tituloRelatorio = "Relatório de top 10 mais alugados";
        int padding = (linhaTamanho - tituloRelatorio.length()) / 2;

        //cabeçalho com a descrição do relatório
        resultado.append("=".repeat(linhaTamanho + 2)).append("\n");
        resultado.append(" ".repeat(padding)).append(tituloRelatorio).append("\n");
        resultado.append("=".repeat(linhaTamanho + 2)).append("\n");

        // cabeçalho da tabela
        resultado.append(String.format("%-11s | %-30s | %-15s\n", "Colocação", "Título", "Total Alugados"));
        resultado.append("-".repeat(linhaTamanho + 2)).append("\n");

        // Query para buscar os 10 mais alugados
        String query = "select count(1) as total, m.titulo as titulo from marketplace.nota_fiscal nf " +
                "join marketplace.aluguel a on a.usuario_id=nf.usuario_id and a.dt_inicio=dt_pagamento " +
                "join marketplace.midia m on m.id=a.midia_id " +
                "group by m.titulo order by total desc limit 10";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String titulo = rs.getString("titulo");
                int total = rs.getInt("total");

                //adicionar cada item à tabela formatada
                resultado.append(String.format("%-11s | %-30s | %-15d\n", rank + "º", titulo, total));
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao gerar o relatório: " + e.getMessage();
        }

        return resultado.toString();
    }


    public String relatorioReceitas(int anoInicio, int anoFim) throws SQLException {
        if (anoFim < anoInicio) return null;
        StringBuilder resultado = new StringBuilder();

        // Cabeçalho descritivo
        resultado.append("==================================\n");
        resultado.append("Relatório de Receitas Por Período:\n");
        // Centralizar o intervalo de anos
        String periodo = "[" + anoInicio + "/" + anoFim + "]";
        int larguraLinha = 34;  // Largura da linha de texto fixa
        int larguraPeriodo = periodo.length();
        int espacoEsquerda = (larguraLinha - larguraPeriodo) / 2;
        resultado.append(" ".repeat(espacoEsquerda)).append(periodo).append("\n");
        resultado.append("==================================\n");

        // Cabeçalho da tabela
        resultado.append(String.format("%-6s | %-6s | %-12s |\n", "Ano", "Mês", "Total R$"));
        resultado.append("----------------------------------\n");

        LocalDate inicio = LocalDate.of(anoInicio, 1, 1);
        LocalDate fim = LocalDate.of(anoFim, 12, 31);  // Até o final do ano
        int anoAnterior = -1;  // Usado para ver quando o ano muda

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            while (inicio.isBefore(fim) || inicio.isEqual(fim)) {
                // Executar consulta para receita total
                PreparedStatement stmt = conn.prepareStatement(queryReceitaPorPeriodo(inicio));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Verificar se o ano mudou para inserir a linha de divisão
                    if (anoAnterior != -1 && anoAnterior != inicio.getYear()) {
                        resultado.append("-  -  -  -  -  -  -  -  -  -  -  -\n");
                    }

                    for (Month mes : Month.values()) {
                        String mesAbreviado = mes.toString().substring(0, 3);  // Abreviação do mês
                        double receitaTotal = rs.getDouble(mes.toString());  // Pegar o valor total da receita do mês

                        // Adicionar linha formatada com os dados da receita total
                        resultado.append(String.format("%-6d | %-6s | %12.2f |\n",
                                inicio.getYear(), mesAbreviado, receitaTotal));
                    }

                    anoAnterior = inicio.getYear();
                }
                inicio = inicio.plusYears(1);  // Avançar para o próximo ano
            }
        }
        return resultado.toString();  // Retornar a tabela formatada como string
    }


    private String queryReceitaPorPeriodo(LocalDate ano){
        StringBuilder query = new StringBuilder("select ");

        for(Month m : Month.values()){
            query.append(" sum(case when date('");
            query.append(ano.toString());
            query.append("') <= dt_pagamento and dt_pagamento < (date('");
            query.append(ano.toString());
            query.append("')+interval '1 month') then nf.valor_total else 0 end) as ");
            query.append(m);
            if(m!=Month.DECEMBER) query.append(",");

            ano = ano.plusMonths(1);
        } query.append(" from marketplace.nota_fiscal nf");
        return query.toString();
    }
}