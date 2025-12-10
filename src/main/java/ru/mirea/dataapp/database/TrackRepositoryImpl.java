package ru.mirea.dataapp.database;

import lombok.RequiredArgsConstructor;
import ru.mirea.dataapp.util.MessageService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
public class TrackRepositoryImpl implements TrackRepository {

    private final DataBaseManager databaseManager;
    private final MessageService messageService;


    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT track_id, track_name, listeners, album_id, duration, genre FROM Track ORDER BY track_id";
        List<Map<String, Object>> tracks = new ArrayList<>();

        try (ResultSet rs = databaseManager.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("track_id", rs.getInt("track_id"));
                row.put("track_name", rs.getString("track_name"));
                row.put("listeners", rs.getInt("listeners"));
                row.put("album_id", rs.getInt("album_id"));
                row.put("duration", rs.getInt("duration"));
                row.put("genre", rs.getString("genre"));
                tracks.add(row);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при загрузке списка треков:\n" + e.getMessage());
            throw e;
        }
        return tracks;
    }

    @Override
    public Map<String, Object> findById(int trackId) throws SQLException {
        String sql = "SELECT * FROM Track WHERE track_id = ?";
        Map<String, Object> track = new HashMap<>();
        try (ResultSet rs = databaseManager.executeQuery(sql, trackId)) {
            if (rs.next()) {
                track.put("track_id", rs.getInt("track_id"));
                track.put("track_name", rs.getString("track_name"));
                track.put("listeners", rs.getInt("listeners"));
                track.put("album_id", rs.getInt("album_id"));
                track.put("duration", rs.getInt("duration"));
                track.put("genre", rs.getString("genre"));
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при поиске трека (ID=" + trackId + "):\n" + e.getMessage());
            throw e;
        }
        return track;

    }

    @Override
    public List<Map<String, Object>> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM Track WHERE track_name LIKE ?";
        List<Map<String, Object>> results = new ArrayList<>();

        try (ResultSet rs = databaseManager.executeQuery(sql, "%" + name + "%")) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("track_id", rs.getInt("track_id"));
                row.put("track_name", rs.getString("track_name"));
                row.put("listeners", rs.getInt("listeners"));
                row.put("album_id", rs.getInt("album_id"));
                row.put("duration", rs.getInt("duration"));
                row.put("genre", rs.getString("genre"));
                results.add(row);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при поиске по имени трека:\n" + e.getMessage());
            throw e;
        }

        return results;
    }

    @Override
    public List<Map<String, Object>> findByAlbum(int albumId) throws SQLException {
        String sql = "SELECT * FROM Track WHERE album_id = ?";
        List<Map<String, Object>> tracks = new ArrayList<>();

        try (ResultSet rs = databaseManager.executeQuery(sql, albumId)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("track_id", rs.getInt("track_id"));
                row.put("track_name", rs.getString("track_name"));
                row.put("listeners", rs.getInt("listeners"));
                row.put("duration", rs.getInt("duration"));
                row.put("genre", rs.getString("genre"));
                tracks.add(row);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при загрузке треков альбома:\n" + e.getMessage());
            throw e;
        }

        return tracks;
    }

    @Override
    public List<Map<String, Object>> findAllWithDetails() throws SQLException {
        String sql = """
            SELECT t.track_id, t.track_name,t.listeners, t.duration, t.genre,
                   a.album_name, ar.artist_name
            FROM Track t
            JOIN Album a ON t.album_id = a.album_id
            JOIN Artist ar ON a.artist_id = ar.artist_id
            ORDER BY ar.artist_name, a.album_name, t.track_number
        """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (ResultSet rs = databaseManager.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("track_id", rs.getInt("track_id"));
                row.put("track_name", rs.getString("track_name"));
                row.put("listeners", rs.getInt("listeners"));
                row.put("duration", rs.getInt("duration"));
                row.put("genre", rs.getString("genre"));
                row.put("album_name", rs.getString("album_name"));
                row.put("artist_name", rs.getString("artist_name"));
                results.add(row);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при загрузке списка треков с деталями:\n" + e.getMessage());
            throw e;
        }

        return results;
    }

    @Override
    public void saveTrack(String trackName, Integer listeners, Integer albumId, Integer duration, String genre) throws SQLException {
        String sql = "INSERT INTO Track (track_name, listeners,  album_id, duration, genre) VALUES (?, ?, ?, ?, ?)";

        try {
            databaseManager.executeUpdate(sql, trackName,listeners, albumId, duration, genre);
            messageService.showInfo("Трек успешно добавлен: " + trackName);
        } catch (SQLException e) {
            messageService.showError("Ошибка при добавлении трека:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateTrack(Integer trackId, String trackName, Integer listeners, Integer albumId, Integer duration, String genre) throws SQLException{
        String sql = "UPDATE Track SET track_name = ?, listeners = ?, album_id = ?, duration = ?, genre = ? WHERE track_id = ?";

        try {
            databaseManager.executeUpdate(sql, trackName, listeners, albumId, duration, genre, trackId);
            messageService.showInfo("Трек обновлён (ID=" + trackId + ")");
        } catch (SQLException e) {
            messageService.showError("Ошибка при обновлении трека:\n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteTrack(Integer trackId) throws SQLException {
        String sql = "DELETE FROM Track WHERE track_id = ?";

        try {
            int rows = databaseManager.executeUpdate(sql, trackId);
            if (rows > 0) {
                messageService.showInfo("Трек удалён (ID=" + trackId + ")");
            } else {
                messageService.showWarning("Трек с ID=" + trackId + " не найден.");
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при удалении трека:\n" + e.getMessage());
            throw e;
        }
    }
}
