package com.marketplace.controller;

import com.marketplace.dao.UsuarioDAO;
import com.marketplace.model.Usuario;
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
public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @PostMapping("/adiciona-usuario")
    public ResponseEntity<String> addUsuario(@RequestBody Usuario usuario) {
        try  {
            usuarioDAO.add(usuario);
            return ResponseEntity.ok("Usuario adicionado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar usuario: " + e.getMessage());

        }
    }
    
    @PostMapping("/remove-usuario/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable int id) {
        try {
            //busca o usuario
            Usuario usuario = usuarioDAO.findById(id);

            if (usuario == null) //se der pau para encontrar o id
                return ResponseEntity.ok("Usuario não encontrado!");
                //deleta o safado
            else usuarioDAO.remove(usuario);

            return ResponseEntity.ok("Usuario deletado com sucesso!");
        } catch (SQLException e) {
            return ResponseEntity.ok("Erro ao deletar usuario: "+ e.getMessage());
        }
    }


    @GetMapping("/lista-usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios;

        try { // tenta carregar os usuarios do banco
            usuarios = usuarioDAO.findAll();
            return ResponseEntity.ok(usuarios);
        } catch (SQLException e) { // se der erro retorna lista vazia
            return ResponseEntity.ok(new LinkedList<>());
        }
    }

    @PostMapping("/atualiza-usuario")
    public ResponseEntity<String> updateUsuario(@RequestBody Usuario usuario) {
        try {
            //vendo se o usuario existe
            Usuario usuarioExistente = usuarioDAO.findById(usuario.getId());

            //se nao existir da erro
            if (usuarioExistente == null) {
                return ResponseEntity.badRequest().body("Usuario com id " + usuario.getId() + " não encontrado.");
            }
            //atualiza o usuario
            usuarioDAO.update(usuario);

            return ResponseEntity.ok("Usuario atualizado com sucesso!");

        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar usuario: " + e.getMessage());
        }
    }

}
