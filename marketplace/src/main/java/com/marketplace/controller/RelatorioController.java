package com.marketplace.controller;

import com.marketplace.dao.RelatorioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private RelatorioDAO relatorioDAO;

    // Endpoint para gerar o relatório de compras em formato de tabela
    @GetMapping("/vendas/tabela")
    public ResponseEntity<String> getRelatorioVendasTabela(
            @RequestParam int anoInicio,
            @RequestParam int anoFim) {
        try {
            String resultado = relatorioDAO.porPeriodoTabela(anoInicio, anoFim, RelatorioDAO.FINALIDADE.COMPRA);
            return ResponseEntity.ok(resultado);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Erro ao gerar o relatório de vendas: " + e.getMessage());
        }
    }

    // Endpoint para gerar o relatório de aluguéis em formato de tabela
    @GetMapping("/alugueis/tabela")
    public ResponseEntity<String> getRelatorioAlugueisTabela(
            @RequestParam int anoInicio,
            @RequestParam int anoFim) {
        try {
            String resultado = relatorioDAO.porPeriodoTabela(anoInicio, anoFim, RelatorioDAO.FINALIDADE.ALUGUEL);
            return ResponseEntity.ok(resultado);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Erro ao gerar o relatório de aluguéis: " + e.getMessage());
        }
    }

    // Endpoint para gerar o relatório de top 10 mais vendidos
    @GetMapping("/mais-vendidos")
    public ResponseEntity<String> getMaisVendidos() {
        try {
            String resultado = relatorioDAO.maisVendidos();
            return ResponseEntity.ok(resultado);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Erro ao gerar o relatório de mais vendidos: " + e.getMessage());
        }
    }

    // Endpoint para gerar o relatório de top 10 mais alugados
    @GetMapping("/mais-alugados")
    public ResponseEntity<String> getMaisAlugados(
            @RequestParam int anoInicio,
            @RequestParam int anoFim) {
        try {
            String resultado = relatorioDAO.maisAlugados(anoInicio, anoFim);
            return ResponseEntity.ok(resultado);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Erro ao gerar o relatório de mais alugados: " + e.getMessage());
        }
    }

    // Endpoint para gerar o relatório de receitas em formato de tabela
    @GetMapping("/receitas/tabela")
    public ResponseEntity<String> getRelatorioReceitasTabela(
            @RequestParam int anoInicio,
            @RequestParam int anoFim) {
        try {
            String resultado = relatorioDAO.relatorioReceitas(anoInicio, anoFim);
            return ResponseEntity.ok(resultado);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Erro ao gerar o relatório de receitas: " + e.getMessage());
        }
    }


}
