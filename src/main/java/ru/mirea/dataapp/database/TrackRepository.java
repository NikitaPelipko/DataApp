package ru.mirea.dataapp.database;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface TrackRepository {

    void saveTrack(String trackName, Integer listeners, Integer albumId, Integer duration, String genre) throws SQLException;

    void updateTrack(Integer trackId, String trackName, Integer listeners, Integer albumId, Integer duration, String genre) throws SQLException;

    List<Map<String, Object>> findAll() throws SQLException;

    List<Map<String, Object>> findByName(String name) throws SQLException;

    void deleteTrack(Integer trackId) throws SQLException;
}
