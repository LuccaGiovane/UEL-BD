package com.marketplace.dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public abstract class ListasDAO implements DAO<String> {
    protected String coluna;
    protected String findAllQuery;
    protected String addQuery;
    protected String removeQuery;
    protected String updateQuery;
    protected String findByIdQuery;
    protected String getIdQuery;

    public ListasDAO(){}

    @Override
    public List<String> findAll() throws SQLException {
        List<String> lista = new LinkedList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(findAllQuery);

            while (rs.next())  lista.add(rs.getString(coluna));
        }
        return lista;
    }

    @Override
    public void add(String string) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(addQuery);
            stmt.setString(1, string);
            stmt.executeQuery();
        }
    }

    @Override
    public void remove(String string) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            ResultSet rs = getId(conn,string);

            if(rs.next()){
                int id = rs.getInt("id");
                PreparedStatement stmt = conn.prepareStatement(removeQuery);
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void update(String string) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            ResultSet rs = getId(conn,string);

            if(rs.next()){
                int id = rs.getInt("id");
                PreparedStatement stmt = conn.prepareStatement(updateQuery);
                stmt.setString(1, string);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public String findById(int id) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(findByIdQuery);
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next())  return rs.getString(coluna);
             else return null;
        }
    }

    /**
     * @author AllanSeidler
     * @return Retorna o ResultSet da busca pelo id.
     * */
    private ResultSet getId(Connection conn, String string) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(getIdQuery);
        stmt.setString(1,string);
        return stmt.executeQuery();
    }
}
