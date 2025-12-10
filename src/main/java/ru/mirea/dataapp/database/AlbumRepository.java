package ru.mirea.dataapp.database;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AlbumRepository {

    void insertAlbum(String albumName, Integer artistId, Integer listeners,  Integer duration, Integer releaseYear) throws SQLException;

    void updateAlbum(Integer albumId, String albumName, Integer artistId, Integer listeners,  Integer duration, Integer releaseYear) throws SQLException;

    List<Map<String, Object>> findAll() throws SQLException;

    Map<String, Object> findById(int albumId) throws SQLException;

    List<Map<String, Object>> findByName(String name) throws SQLException;

    List<Map<String, Object>> findByArtist(int artistId) throws SQLException;

    void deleteAlbum(Integer albumId) throws SQLException;

}
