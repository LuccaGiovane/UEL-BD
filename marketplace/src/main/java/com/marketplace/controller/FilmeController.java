package com.marketplace.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.marketplace.dao.FilmeDAO;
import com.marketplace.json.LocalDateAdapter;
import com.marketplace.model.Filme;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.Gson;

@Controller
public class FilmeController {
    private final FilmeDAO filmeDAO = new FilmeDAO();

    // endpoint que adiciona um filme via ajax
    @PostMapping("/add-filme")
    public ResponseEntity<String> addFilme(@RequestBody Filme filme) {
        try  {
            filmeDAO.add(filme);
            return ResponseEntity.ok("Filme adicionado com sucesso!");
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar filme: " + e.getMessage());
        }
    }

    // endpoint que lista os filmes via ajax
    @GetMapping("/list-filmes")
    public void listarFilmes(HttpServletResponse response) throws IOException {
        List<Filme> filmes;
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String json;

        try { // tenta carregar os filmes do banco
            filmes = filmeDAO.findAll();
            System.out.println(filmes.get(0).getDtLancamento());
            json = gson.toJson(filmes);
            response.getOutputStream().print(json);
        } catch (SQLException e) { // se der erro retorna lista vazia
            json = gson.toJson(new LinkedList<>());
            response.getOutputStream().print(json);
        } catch (JsonIOException j) {
            System.out.println(j.getMessage());
            System.out.println("Erro de json");



        }
    }

}
