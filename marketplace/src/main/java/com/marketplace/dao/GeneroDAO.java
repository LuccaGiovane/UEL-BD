package com.marketplace.dao;

public class GeneroDAO extends ListasDAO {

    public GeneroDAO() {
        super.coluna = "genero";
        super.findAllQuery = "SELECT genero FROM marketplace.genero";
        super.addQuery = "INSERT INTO marketplace.genero (genero) VALUES (?) RETURNING id";
        super.removeQuery = "DELETE FROM marketplace.genero WHERE id = ?";
        super.updateQuery = "UPDATE marketplace.genero SET genero = ? WHERE id = ?";
        super.findByIdQuery = "SELECT * FROM marketplace.genero WHERE id = ?";
        super.getIdQuery = "SELECT id FROM marketplace.genero WHERE genero=?";
    }


}