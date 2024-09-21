package com.marketplace.dao;

public class IdiomaDAO extends ListasDAO {

    public IdiomaDAO() {
        super.coluna = "idioma";
        super.findAllQuery = "SELECT idioma FROM marketplace.idioma";
        super.addQuery = "INSERT INTO marketplace.idioma (idioma) VALUES (?) RETURNING id";
        super.removeQuery = "DELETE FROM marketplace.idioma WHERE id = ?";
        super.updateQuery = "UPDATE marketplace.idioma SET idioma = ? WHERE id = ?";
        super.findByIdQuery = "SELECT * FROM marketplace.idioma WHERE id = ?";
        super.getIdQuery = "SELECT id FROM marketplace.idioma WHERE idioma=?";
    }


}