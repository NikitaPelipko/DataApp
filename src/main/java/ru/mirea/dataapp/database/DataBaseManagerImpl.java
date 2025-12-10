package ru.mirea.dataapp.database;



import lombok.RequiredArgsConstructor;
import ru.mirea.dataapp.util.MessageService;

import java.sql.*;

@RequiredArgsConstructor
public class DataBaseManagerImpl implements DataBaseManager{

    private final MessageService messageService;
    private Connection connection;


    @Override
    public void connect(String url, String user, String password) throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                messageService.showError("Соединение с базой данных уже установлено");
                return;
            }
            connection = DriverManager.getConnection("jdbc:sqlserver://" + url + ";databaseName=MusicApp;encrypt=true;trustServerCertificate=true", user, password);
            messageService.showInfo("Соединение с базой данных установлено");
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            messageService.showError("Соединение с базой данных не установлено.");
            throw new SQLException();
        }
        return connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            messageService.showInfo("Соединение с базой данных закрыто.");
        }
    }

    @Override
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement statement = prepareStatement(sql, params);
        return statement.executeQuery();
    }

    @Override
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = prepareStatement(sql, params)) {
            return statement.executeUpdate();
        }
    }

    private PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Соединение не установлено.");
            }

            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement;
    }

    @Override
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }


}
