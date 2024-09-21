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

    // endpoint que adiciona um filme via ajax
    @PostMapping("/adiciona-filme")
    public ResponseEntity<String> addFilme(@RequestBody Filme filme) {

        try  {
            filmeDAO.add(filme);
            System.out.println("AQ1");
            return ResponseEntity.ok("Filme adicionado com sucesso!");

        } catch (SQLException e) {
            System.out.println("AQ2");
            return ResponseEntity.badRequest().body("Erro ao adicionar filme: " + e.getMessage());

        }
    }

    // endpoint que deleta um filme via ajax
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


    // endpoint que lista os filmes via ajax
    @GetMapping("/lista-filmes")
    public ResponseEntity<List<Filme>> listarFilmes() {
        List<Filme> filmes;
//        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
//        String json;

        try { // tenta carregar os filmes do banco
            filmes = filmeDAO.findAll();
//            json = gson.toJson(filmes);
            return ResponseEntity.ok(filmes);
        } catch (SQLException e) { // se der erro retorna lista vazia
//            json = gson.toJson(new LinkedList<>());
            return ResponseEntity.ok(new LinkedList<>());
        }
//        } catch (JsonIOException j) {
//            response.getOutputStream().print("Erro de JSON: "+j.getMessage());
//            return ResponseEntity.ok(new LinkedList<>());
//        }

    }

    //Endpoint que atualiza os filmes via Ajax
    @PostMapping("/atualiza-filme")
    public ResponseEntity<String> updateFilme(@RequestBody Filme filme) {
        try {
            //vendo se o filme existe
            Filme filmeExistente = filmeDAO.findById(filme.getId());

            //se nao existir da erro
            if (filmeExistente == null) {
                return ResponseEntity.badRequest().body("Filme com id " + filme.getId() + " não encontrado.");
            }

            //att o filme
            filmeDAO.update(filme);

            return ResponseEntity.ok("Filme atualizado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar filme: " + e.getMessage());
        }
    }

}
