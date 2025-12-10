package ru.mirea.dataapp.database;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ArtistRepository {

    void saveArtist(String artistName, Integer listeners) throws SQLException;

    void updateArtist(Integer artistId, String artistName, Integer listeners) throws SQLException;

    List<Map<String, Object>> findAll() throws SQLException;

    Map<String, Object> findById(int artistId) throws SQLException;

    List<Map<String, Object>> findByName(String name) throws SQLException;

    void deleteById(int artistId) throws SQLException;


}
