package com.marketplace.controller;

import com.marketplace.dao.IdiomaDAO;
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
public class IdiomaController {
    private final IdiomaDAO idiomaDAO = new IdiomaDAO();


    @PostMapping("/adiciona-idioma")
    public ResponseEntity<String> addIdioma(@RequestBody String idioma) {

        try  {
            idiomaDAO.add(idioma);
            return ResponseEntity.ok("Idioma adicionado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar idioma: " + e.getMessage());
        }
    }
    
    @PostMapping("/remove-idioma/{id}")
    public ResponseEntity<String> deleteIdioma(@PathVariable int id) {
        try {
            //busca o idioma
            String idioma = idiomaDAO.findById(id);

            if (idioma == null) //se der pau para encontrar o id
                return ResponseEntity.ok("Idioma não encontrado!");
                //deleta o safado
            else idiomaDAO.remove(idioma);

            return ResponseEntity.ok("Idioma deletado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.ok("Erro ao deletar idioma: "+ e.getMessage());
        }
    }


    @GetMapping("/lista-idiomas")
    public ResponseEntity<List<String>> listarIdiomas() {
        List<String> idiomas;

        try { // tenta carregar os idiomas do banco
            idiomas = idiomaDAO.findAll();
            return ResponseEntity.ok(idiomas);
        } catch (SQLException e) { // se der erro retorna lista vazia
            return ResponseEntity.ok(new LinkedList<>());
        }
    }

    @PostMapping("/atualiza-idioma")
    public ResponseEntity<String> updateIdioma(@RequestBody String idioma) {
        try {
            //atualiza o idioma
            idiomaDAO.update(idioma);
            return ResponseEntity.ok("Idioma atualizado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar idioma: " + e.getMessage());
        }
    }

}
