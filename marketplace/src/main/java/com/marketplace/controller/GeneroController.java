package com.marketplace.controller;

import com.marketplace.dao.GeneroDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class GeneroController {
    private final GeneroDAO generoDAO = new GeneroDAO();


    @PostMapping("/adiciona-genero")
    public ResponseEntity<String> addGenero(@RequestBody String genero) {

        try  {
            generoDAO.add(genero);
            return ResponseEntity.ok("Genero adicionado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar genero: " + e.getMessage());

        }
    }

    @PostMapping("/remove-genero/{id}")
    public ResponseEntity<String> deleteGenero(@PathVariable int id) {
        try {
            //busca o genero
            String genero = generoDAO.findById(id);

            if (genero == null) //se der pau para encontrar o id
                return ResponseEntity.ok("Genero não encontrado!");
                //deleta o safado
            else generoDAO.remove(genero);

            return ResponseEntity.ok("Genero deletado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.ok("Erro ao deletar genero: "+ e.getMessage());
        }
    }


    @GetMapping("/lista-generos")
    public ResponseEntity<List<String>> listarGeneros() {
        List<String> generos;

        try { // tenta carregar os generos do banco
            generos = generoDAO.findAll();
            return ResponseEntity.ok(generos);
        } catch (SQLException e) { // se der erro retorna lista vazia
            return ResponseEntity.ok(new LinkedList<>());
        }
    }

    @PostMapping("/atualiza-genero")
    public ResponseEntity<String> updateGenero(@RequestBody String genero) {
        try {
            //atualiza o genero
            generoDAO.update(genero);
            return ResponseEntity.ok("Genero atualizado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar genero: " + e.getMessage());
        }
    }

}
