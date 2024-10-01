package com.marketplace.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {
    String user = System.getenv("DB_USER");
    String url = "jdbc:postgresql://sicm.dc.uel.br:5432/"+user+"?sslmode=prefer";

    String password = System.getenv("DB_PASSWORD");

    /**
     * @author AllanSeidler
     * @return Returna uma lista com todos os objetos T encontrados.
     * @throws SQLException Problema na busca.
    * */
    List<T> findAll() throws SQLException;

    /**
     * @author AllanSeidler
     * @param object Objeto da Classe T que será adicionado ao banco.
     * @throws SQLException Problema na inserção.
     */
    void add(T object) throws SQLException;

    /**
     * @author AllanSeidler
     * @param object Objeto da Classe T que será removida do banco.
     * @throws SQLException Problema na remoção.
     */
    void remove(T object) throws SQLException;

    /**
     * @author lucca
     * @param object Objeto da Classe T que será atualizado no banco.
     * @throws SQLException Problema na atualização.
     */
    void update(T object) throws SQLException;

    /**
     * @author AllanSeidler
     * @param id Id do objeto sendo buscado.
     * @return Returna o objeto sendo buscado. Caso não ache retorna null.
     * @throws SQLException Erro na busca.
     */
    T findById(int id) throws SQLException;

}
