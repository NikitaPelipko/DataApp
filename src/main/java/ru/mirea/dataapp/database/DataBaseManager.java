package ru.mirea.dataapp.database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataBaseManager {

    void connect(String url, String user, String password) throws SQLException;

    Connection getConnection() throws SQLException;

    void closeConnection() throws SQLException;

    ResultSet executeQuery(String sql, Object... params) throws SQLException;

    int executeUpdate(String sql, Object... params) throws SQLException;

    boolean testConnection() throws SQLException;
}
