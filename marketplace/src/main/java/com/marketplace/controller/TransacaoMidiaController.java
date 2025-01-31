package com.marketplace.controller;

import com.marketplace.dao.TransacaoMidiaDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
public class TransacaoMidiaController {

    private final TransacaoMidiaDAO transacaoMidiaDAO = new TransacaoMidiaDAO();


    @PostMapping("/realizar-transacao")
    public ResponseEntity<String> realizarTransacao(@RequestParam int usuarioId,
                                                    @RequestParam String paraAlugar,
                                                    @RequestParam String paraComprar){
        try {
            transacaoMidiaDAO.criarNotaFiscal(usuarioId, paraComprar, paraAlugar);
            return ResponseEntity.ok("Mídias compradas e/ou alugadas com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao registrar NF: " + e.getMessage());
        }
    }


    // endpoint para listar compras por usuario
    @GetMapping("/listar-compras-usuario")
    public ResponseEntity<List<Integer>> listarComprasPorUsuario(@RequestParam int usuarioId) {

        try {
            List<Integer> midiasCompradas = transacaoMidiaDAO.listarComprasPorUsuarioId(usuarioId);
            return ResponseEntity.ok(midiasCompradas);

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //endpoint para listar alugueis por usuario
    @GetMapping("/listar-alugueis-usuario")
    public ResponseEntity<List<Integer>> listarAlugueisPorUsuario(@RequestParam int usuarioId) {

        try {
            List<Integer> midiasAlugadas = transacaoMidiaDAO.listarAlugueisPorUsuarioId(usuarioId);
            return ResponseEntity.ok(midiasAlugadas);

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //endpoint para listar compras por midia
    @GetMapping("/listar-compras-midia")
    public ResponseEntity<List<Integer>> listarComprasPorMidia(@RequestParam int midiaId) {

        try {
            List<Integer> usuariosQueCompraram = transacaoMidiaDAO.listarComprasPorMidiaId(midiaId);
            return ResponseEntity.ok(usuariosQueCompraram);

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //endpoint para listar alugueis por midia
    @GetMapping("/listar-alugueis-midia")
    public ResponseEntity<List<Integer>> listarAlugueisPorMidia(@RequestParam int midiaId) {

        try {
            List<Integer> usuariosQueAlugaram = transacaoMidiaDAO.listarAlugueisPorMidiaId(midiaId);
            return ResponseEntity.ok(usuariosQueAlugaram);

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
