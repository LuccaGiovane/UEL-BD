package com.marketplace.controller;

import com.marketplace.dto.FilmeDTO;
import com.marketplace.service.FilmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filmes")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    // endpoint que adiciona um filme via ajax
    @PostMapping("/add")
    public ResponseEntity<String> addFilme(@RequestBody FilmeDTO filme)
    {
        try
        {
            filmeService.inserirFilme(filme);
            return ResponseEntity.ok("Filme adicionado com sucesso!");
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Erro ao adicionar filme: " + e.getMessage());
        }
    }

    // endpoint que lista os filmes via ajax
    @GetMapping("/list")
    public ResponseEntity<List<FilmeDTO>> listarFilmes()
    {
        List<FilmeDTO> filmes = filmeService.listarFilmes();

        return ResponseEntity.ok(filmes);
    }
}
