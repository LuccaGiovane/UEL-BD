package com.marketplace;

import com.marketplace.dao.RelatorioDAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;


public class TestMonths {
    static String user = System.getenv("DB_USER");
    static String url = "jdbc:postgresql://sicm.dc.uel.br:5432/"+user+"?sslmode=prefer";
    static String password = System.getenv("DB_PASSWORD");

    private enum FINALIDADE {COMPRA,ALUGUEL};

    private int[][] porPeriodo(int anoInicio, int anoFim, RelatorioDAO.FINALIDADE f) throws SQLException {
        if(anoFim<anoInicio) return null;
        int[][] resultado = new int[anoFim-anoInicio+1][12]; // vetor de resultados
        int i=0; // coluna dos anos

        LocalDate inicio = LocalDate.of(anoInicio,1,1);
        LocalDate fim = LocalDate.of(anoFim,1,1);

        try (Connection conn = DriverManager.getConnection(url, user, password)){
            while (inicio.isBefore(fim) || inicio.isEqual(fim)){
                PreparedStatement stmt = null;
                switch (f){
                    case COMPRA -> stmt = conn.prepareStatement(queryVendasPorPeriodo(inicio));
                    case ALUGUEL -> stmt = conn.prepareStatement(queryAlugueisPorPeriodo(inicio));
                }

                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    for (Month m : Month.values())
                        resultado[i][m.getValue()-1] = rs.getInt(m.toString());
                }
                inicio = inicio.plusYears(1); // proximo ano
                i+=1; // coluna respectiva do ano
            }
            return resultado;
        }
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



    public static void main(String[] args) throws SQLException {
        int[][] vendas;

        TestMonths t = new TestMonths();
        vendas = t.porPeriodo(2024,2025, RelatorioDAO.FINALIDADE.ALUGUEL);


        for(int i =0; i <2; i++){
            for(int j =0; j <12; j++){
                System.out.print(vendas[i][j] +" ");
            }
            System.out.println();
        }
    }
}
