package ru.mirea.dataapp.database;

import lombok.RequiredArgsConstructor;
import ru.mirea.dataapp.util.MessageService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository{

    private final DataBaseManager databaseManager;
    private final MessageService messageService;
    @Override
    public void insertAlbum(String albumName, Integer artistId, Integer listeners, Integer duration, Integer releaseYear) throws SQLException {
        String sql = "INSERT INTO Album (album_name, artist_id, listeners, duration, release_year) VALUES (?, ?, ?, ?, ?)";

        try {
            databaseManager.executeUpdate(sql, albumName,artistId,listeners, duration, releaseYear);
            messageService.showInfo("Альбом успешно добавлен: " + albumName);
        } catch (SQLException e) {
            messageService.showError("Ошибка при добавлении альбома:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateAlbum(Integer albumId, String albumName, Integer artistId, Integer listeners, Integer duration, Integer releaseYear) throws SQLException {
        String sql = "UPDATE Album SET album_name = ?, artist_id = ?, listeners = ?, duration = ?, release_year = ? WHERE album_id = ?";

        try {
            databaseManager.executeUpdate(sql, albumName, artistId, listeners, duration, releaseYear, albumId);
            messageService.showInfo("Альбом обновлён (ID=" + albumId + ")");
        } catch (SQLException e) {
            messageService.showError("Ошибка при обновлении альбома:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT album_id, album_name, artist_id, listeners, duration, release_year FROM Album ORDER BY album_id";
        List<Map<String, Object>> albums = new ArrayList<>();

        try (ResultSet rs = databaseManager.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("album_id", rs.getInt("album_id"));
                row.put("album_name", rs.getString("album_name"));
                row.put("listeners", rs.getInt("listeners"));
                row.put("duration", rs.getInt("duration"));
                row.put("release_year", rs.getString("release_year"));
                albums.add(row);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при загрузке списка треков:\n" + e.getMessage());
            throw e;
        }
        return albums;
    }

    @Override
    public List<Map<String, Object>> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM Album WHERE album_name LIKE ?";
        List<Map<String, Object>> albums = new ArrayList<>();
        try (ResultSet rs = databaseManager.executeQuery(sql, "%" + name + "%")) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("album_id", rs.getInt("album_id"));
                row.put("album_name", rs.getString("album_name"));
                row.put("listeners", rs.getInt("listeners"));
                row.put("artist_id", rs.getInt("artist_id"));
                row.put("duration", rs.getInt("duration"));
                row.put("release_year", rs.getString("release_year"));
                albums.add(row);
            }
        }
        catch (SQLException e) {
            messageService.showError("Ошибка при поиске по имени альбома");
            throw e;
        }
        return albums;
    }


    @Override
    public void deleteAlbum(Integer albumId) throws SQLException {
        String sql = "DELETE FROM Album WHERE album_id = ?";
        try {
            int rows = databaseManager.executeUpdate(sql, albumId);
            if (rows > 0) {
                messageService.showInfo("Альбом удалён (ID=" + albumId + ")");
            } else {
                messageService.showWarning("Альбом с ID=" + albumId + " не найден.");
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при удалении альбома:\n" + e.getMessage());
            throw e;
        }
    }
}
