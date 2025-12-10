package ru.mirea.dataapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import ru.mirea.dataapp.database.AlbumRepository;
import ru.mirea.dataapp.database.TrackRepository;
import ru.mirea.dataapp.util.MessageService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class TrackEditController {

    private final MessageService messageService;
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private Integer trackId;

    public TrackEditController(MessageService messageService, TrackRepository trackRepository, AlbumRepository albumRepository) {
        this.messageService = messageService;
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
    }

    @FXML
    private TextField nameField;
    @FXML
    private TextField listenersField;
    @FXML
    private TextField durationField;
    @FXML
    private TextField albumField;
    @FXML
    private TextField genreField;

    @Setter
    private Stage dialogStage;
    @Getter
    private boolean okClicked = false;

    public void setInitialData(Map<String, Object> row) {
        if (row == null) {
            trackId = null;
            nameField.setText("");
            listenersField.setText("");
            durationField.setText("");
            albumField.setText("");
            genreField.setText("");
        } else {
            trackId = (Integer) row.get("track_id");
            nameField.setText((String) row.get("track_name"));
            listenersField.setText(String.valueOf(row.get("listeners")));
            durationField.setText(String.valueOf(row.get("duration")));
            albumField.setText(String.valueOf(row.get("album_id")));
            genreField.setText((String) row.get("genre"));
        }
    }

    @FXML
    private void onOk() {
        String name = nameField.getText();
        String listenersStr = listenersField.getText();
        String durationStr = durationField.getText();
        String albumStr = albumField.getText();
        String genre = genreField.getText();

        if (name == null || name.isBlank()) {
            messageService.showWarning("Имя исполнителя не может быть пустым.");
            return;
        }

        if (genre == null || genre.isBlank()) {
            messageService.showWarning("Имя исполнителя не может быть пустым.");
            return;
        }

        Integer listeners;
        try {
            listeners = Integer.parseInt(listenersStr);
        } catch (NumberFormatException e) {
            messageService.showWarning("Слушатели должны быть числом.");
            return;
        }

        Integer duration;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            messageService.showWarning("Длительность должна быть числом.");
            return;
        }

        Integer albumId;
        try {
            albumId = Integer.parseInt(albumStr);
        } catch (NumberFormatException e) {
            messageService.showWarning("Длительность должна быть числом.");
            return;
        }


        try {

            if (trackId == null) {
                trackRepository.saveTrack(name, listeners,albumId,duration,genre);
            } else {
                trackRepository.updateTrack(trackId, name, listeners,albumId,duration,genre);
            }

            okClicked = true;
            dialogStage.close();
        } catch (SQLException e) {
            messageService.showError("Ошибка при сохранении трека:\n" + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    @FXML
    private void onSelectAlbum() throws SQLException {
        try {
            List<Map<String, Object>> albums = albumRepository.findAll();

            List<String> names = albums.stream()
                    .map(a -> a.get("album_id") + " - " + a.get("album_name"))
                    .toList();

            ChoiceDialog<String> dialog = new ChoiceDialog<>(
                    names.isEmpty() ? null : names.get(0),
                    names
            );
            dialog.setTitle("Выбор альбома");
            dialog.setHeaderText("Выберите альбом");
            dialog.setContentText(null);

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String selected = result.get();
                String idStr = selected.split(" - ")[0].trim();
                albumField.setText(idStr);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при поиске альбомов:\n" + e.getMessage());
            throw e;

        }
    }

    }
