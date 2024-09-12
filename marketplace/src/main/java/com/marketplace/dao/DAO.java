package com.marketplace.dao;

import java.sql.SQLException;
import java.util.List;

public abstract class DAO<T> {
    protected final String url = "jdbc:postgresql://localhost:5432/marketplace";
    protected final String user = System.getenv("DB_USER");
    protected final String password = System.getenv("DB_PASSWORD");

    public abstract List<T> findAll() throws SQLException;

    public abstract void add(T object) throws SQLException;
}
