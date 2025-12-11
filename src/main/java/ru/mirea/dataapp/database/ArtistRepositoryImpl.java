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
public class ArtistRepositoryImpl implements ArtistRepository{

    private final MessageService messageService;
    private final DataBaseManager dataBaseManager;

    @Override
    public void saveArtist(String artistName, Integer listeners) throws SQLException {
        String sql = "INSERT INTO Artist (artist_name, listeners) VALUES (?, ?)";
        try {
            dataBaseManager.executeUpdate(sql, artistName, listeners);
            messageService.showInfo("Исполнитель успешно добавлен: " + artistName);
        }
        catch (SQLException e){
            messageService.showError("Ошибка при добавлении исполнителя:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateArtist(Integer artistId, String artistName, Integer listeners) throws SQLException {
        String sql = "UPDATE Artist SET artist_name = ?, listeners = ? WHERE artist_id = ?";
        try {
            dataBaseManager.executeUpdate(sql, artistName, listeners, artistId);
            messageService.showInfo("Артист успешно обновлен.");
        }
        catch (SQLException e){
            messageService.showError("Ошибка при обновлении исполнителя:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM Artist ORDER BY artist_id";
        List<Map<String, Object>> artists = new ArrayList<>();
        try (ResultSet rs = dataBaseManager.executeQuery(sql)) {
            while(rs.next()){
                Map<String, Object> map = new HashMap<>();
                map.put("artist_id", rs.getInt("artist_id"));
                map.put("artist_name", rs.getString("artist_name"));
                map.put("listeners", rs.getInt("listeners"));
                artists.add(map);
            }
        }
        catch (SQLException e){
            messageService.showError("Ошибка при загрузке списка исполнителей:\n" + e.getMessage());
            throw e;
        }
        return artists;
    }

    @Override
    public List<Map<String, Object>> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM Artist WHERE artist_name LIKE ?";
        List<Map<String, Object>> artists = new ArrayList<>();
        try (ResultSet rs = dataBaseManager.executeQuery(sql, "%" + name + "%")) {
            while(rs.next()){
                Map<String, Object> map = new HashMap<>();
                map.put("artist_id", rs.getInt("artist_id"));
                map.put("artist_name", rs.getString("artist_name"));
                map.put("listeners", rs.getInt("listeners"));
                artists.add(map);
            }
        }
        catch (SQLException e){
            messageService.showError("Ошибка при поиске исполнителя по имена:\n" + e.getMessage());
            throw e;
        }
        return artists;
    }

    @Override
    public void deleteById(int artistId) throws SQLException {
        String sql = "DELETE FROM Artist WHERE artist_id = ?";

        try {
            int rows = dataBaseManager.executeUpdate(sql, artistId);
            if (rows > 0) {
                messageService.showInfo("Исполнитель удалён (ID=" + artistId + ")");
            } else {
                messageService.showWarning("Исполнитель с ID=" + artistId + " не найден.");
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при удалении исполнителя:\n" + e.getMessage());
            throw e;
        }
    }
}
