package com.marketplace.controller;

import com.marketplace.dao.FilmeDAO;
import com.marketplace.model.Filme;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/filmes")
public class FilmeController {
    private final FilmeDAO filmeDAO = new FilmeDAO();

    // endpoint que adiciona um filme via ajax
    @PostMapping("/add")
    public ResponseEntity<String> addFilme(@RequestBody Filme filme) {
        try  {
            filmeDAO.add(filme);
            return ResponseEntity.ok("Filme adicionado com sucesso!");
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar filme: " + e.getMessage());
        }
    }

    // endpoint que lista os filmes via ajax
    @GetMapping("/list")
    public ResponseEntity<List<Filme>> listarFilmes() {
        List<Filme> filmes;
        try { // tenta carregar os filmes do banco
            filmes = filmeDAO.findAll();
        } catch (SQLException e) { // se der erro retorna lista vazia
            return ResponseEntity.ok(new LinkedList<>());
        }
        return ResponseEntity.ok(filmes);
    }
}
