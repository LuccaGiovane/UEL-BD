package com.marketplace.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {
    String url = "jdbc:postgresql://localhost:5432/postgres?sslmode=prefer";
    String user = "postgres"; //System.getenv("DB_USER");
    String password = "csaewendy"; // System.getenv("DB_PASSWORD");

    public abstract List<T> findAll() throws SQLException, ClassNotFoundException;

    public abstract void add(T object) throws SQLException;
}
