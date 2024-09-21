package com.marketplace.controller;

import com.marketplace.dao.FilmeDAO;
import com.marketplace.model.Filme;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class FilmeController {
    private final FilmeDAO filmeDAO = new FilmeDAO();

    
    @PostMapping("/adiciona-filme")
    public ResponseEntity<String> addFilme(@RequestBody Filme filme) {

        try  {
            filmeDAO.add(filme);
            return ResponseEntity.ok("Filme adicionado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar filme: " + e.getMessage());

        }
    }

    
    @PostMapping("/remove-filme/{id}")
    public ResponseEntity<String> deleteFilme(@PathVariable int id) {
        try {
            //busca o filme
            Filme filme = filmeDAO.findById(id);

            if (filme == null) //se der pau para encontrar o id
                return ResponseEntity.ok("Filme não encontrado!");
            //deleta o safado
            else filmeDAO.remove(filme);

            return ResponseEntity.ok("Filme deletado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.ok("Erro ao deletar filme: "+ e.getMessage());
        }
    }


    @GetMapping("/lista-filmes")
    public ResponseEntity<List<Filme>> listarFilmes() {
        List<Filme> filmes;

        try { // tenta carregar os filmes do banco
            filmes = filmeDAO.findAll();
            return ResponseEntity.ok(filmes);
        } catch (SQLException e) { // se der erro retorna lista vazia
            return ResponseEntity.ok(new LinkedList<>());
        }
    }

    @PostMapping("/atualiza-filme")
    public ResponseEntity<String> updateFilme(@RequestBody Filme filme) {
        try {
            //vendo se o filme existe
            Filme filmeExistente = filmeDAO.findById(filme.getId());

            //se nao existir da erro
            if (filmeExistente == null) {
                return ResponseEntity.badRequest().body("Filme com id " + filme.getId() + " não encontrado.");
            }
            //atualiza o filme
            filmeDAO.update(filme);

            return ResponseEntity.ok("Filme atualizado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar filme: " + e.getMessage());
        }
    }

}
