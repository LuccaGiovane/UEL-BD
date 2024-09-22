package com.marketplace.controller;

import com.marketplace.dao.SerieDAO;
import com.marketplace.model.Serie;
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
public class SerieController {

    private final SerieDAO serieDAO = new SerieDAO();


    @PostMapping("/adiciona-serie")
    public ResponseEntity<String> addSerie(@RequestBody Serie serie) {

        try  {
            serieDAO.add(serie);
            return ResponseEntity.ok("Serie adicionado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar serie: " + e.getMessage());
        }
    }


    @PostMapping("/remove-serie/{id}")
    public ResponseEntity<String> deleteSerie(@PathVariable int id) {
        try {
            //busca o serie
            Serie serie = serieDAO.findById(id);

            if (serie == null) //se der pau para encontrar o id
                return ResponseEntity.ok("Serie não encontrado!");

            else serieDAO.remove(serie);

            return ResponseEntity.ok("Serie deletado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.ok("Erro ao deletar serie: "+ e.getMessage());
        }
    }


    @GetMapping("/lista-series")
    public ResponseEntity<List<Serie>> listarSeries() {
        List<Serie> series;

        try { // tenta carregar os series do banco
            series = serieDAO.findAll();
            return ResponseEntity.ok(series);
        } catch (SQLException e) { // se der erro retorna lista vazia
            return ResponseEntity.ok(new LinkedList<>());
        }
    }

    @PostMapping("/atualiza-serie")
    public ResponseEntity<String> updateSerie(@RequestBody Serie serie) {
        try {
            //vendo se o serie existe
            Serie serieExistente = serieDAO.findById(serie.getId());

            //se nao existir da erro
            if (serieExistente == null) {
                return ResponseEntity.badRequest().body("Serie com id " + serie.getId() + " não encontrado.");
            }
            //atualiza o serie
            serieDAO.update(serie);

            return ResponseEntity.ok("Serie atualizado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar serie: " + e.getMessage());
        }
    }

}
