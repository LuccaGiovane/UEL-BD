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
    String url = "jdbc:postgresql://sicm.dc.uel.br:5432/"+user+"?sslmode=prefer";//:
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
        LocalDate fim = LocalDate.of(anoFim, 12, 31);  // Até o final do ano
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
                    if (anoAnterior != -1 && anoAnterior != inicio.getYear()) {
                        resultado.append("-  -  -  -  -  -  -  -  -  -  -  -\n");
                    }

                    for (Month m : Month.values()) {

                        String mes = m.toString().substring(0, 3);
                        int quantidade = rs.getInt(m.toString());

                        // Adicionar a linha na tabela formatada
                        resultado.append(String.format("%-6d | %-6s | %12d |\n", inicio.getYear(), mes, quantidade));
                    }

                    // Atualizar o ano anterior
                    anoAnterior = inicio.getYear();
                }
                inicio = inicio.plusYears(1);
            }
        }
        return resultado.toString();
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

    public int[][] maisVendidos(int anoInicio, int anoFim){
        return null;
    }

    public int[][] maisAlugados(int anoInicio, int anoFim){
        return null;
    }

    public double[][] relatorioReceitas(){
        return null;
    }




}
