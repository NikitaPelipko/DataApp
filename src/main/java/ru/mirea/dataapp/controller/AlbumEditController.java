package ru.mirea.dataapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mirea.dataapp.database.AlbumRepository;
import ru.mirea.dataapp.database.ArtistRepository;
import ru.mirea.dataapp.util.MessageService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AlbumEditController {

    @FXML private TextField nameField;
    @FXML private TextField artistIdField;
    @FXML private TextField listenersField;
    @FXML private TextField durationField;
    @FXML private TextField yearField;

    private final MessageService messageService;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    private Stage dialogStage;
    private boolean okClicked = false;

    private Integer albumId;          // null при добавлении
    private Integer currentArtistId;
    private Integer currentListeners;
    private Integer currentDuration;
    private Integer currentYear;

    public AlbumEditController(MessageService messageService,
                               AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.messageService = messageService;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setInitialData(Map<String, Object> row) {
        if (row == null) {
            albumId = null;
            currentArtistId = null;
            currentListeners = null;
            currentDuration = null;
            currentYear = null;

            nameField.setText("");
            artistIdField.setText("");
            listenersField.setText("");
            durationField.setText("");
            yearField.setText("");
        } else {
            albumId         = (Integer) row.get("album_id");
            String name     = (String)  row.get("album_name");
            currentArtistId = (Integer) row.get("artist_id");
            currentListeners= (Integer) row.get("listeners");
            currentDuration = (Integer) row.get("duration");
            currentYear     = Integer.valueOf(row.get("release_year").toString());

            nameField.setText(name);
            artistIdField.setText(currentArtistId.toString());
            listenersField.setText(currentListeners.toString());
            durationField.setText(currentDuration.toString());
            yearField.setText(currentYear.toString());
        }
    }

    @FXML
    private void onOk() {
        String nameText       = nameField.getText();
        String artistIdText   = artistIdField.getText();
        String listenersText  = listenersField.getText();
        String durationText   = durationField.getText();
        String yearText       = yearField.getText();

        if (nameText == null || nameText.isBlank()) {
            messageService.showWarning("Название альбома не может быть пустым.");
            return;
        }

        Integer artistIdNew    = parseIntOrKeep(artistIdText, currentArtistId, "ID исполнителя");
        if (artistIdNew == null && currentArtistId == null) return;

        Integer listenersNew   = parseIntOrKeep(listenersText, currentListeners, "Слушатели");
        if (listenersNew == null && currentListeners == null) return;

        Integer durationNew    = parseIntOrKeep(durationText, currentDuration, "Длительность");
        if (durationNew == null && currentDuration == null) return;

        Integer yearNew        = parseIntOrKeep(yearText, currentYear, "Год релиза");
        if (yearNew == null && currentYear == null) return;

        try {
            if (albumId == null) {
                albumRepository.insertAlbum(nameText, artistIdNew, listenersNew, durationNew, yearNew);
            } else {
                albumRepository.updateAlbum(albumId, nameText, artistIdNew, listenersNew, durationNew, yearNew);
            }
            okClicked = true;
            dialogStage.close();
        } catch (SQLException e) {
            messageService.showError("Ошибка при сохранении альбома:\n" + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    private Integer parseIntOrKeep(String text, Integer current, String fieldName) {
        if (text == null || text.isBlank()) {
            return current; // оставить старое значение
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            messageService.showWarning(fieldName + " должно быть целым числом.");
            return current;
        }
    }

    @FXML
    private void onSelectArtist() {
        try {
            List<Map<String, Object>> artists = artistRepository.findAll();

            List<String> names = artists.stream()
                    .map(a -> a.get("artist_id") + " - " + a.get("artist_name"))
                    .toList();

            ChoiceDialog<String> dialog = new ChoiceDialog<>(
                    names.isEmpty() ? null : names.get(0),
                    names
            );
            dialog.setTitle("Выбор исполнителя");
            dialog.setHeaderText("Выберите исполнителя");
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/ru/mirea/dataapp/styles.css").toExternalForm()
            );
            dialog.setContentText(null);

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String selected = result.get();
                String idStr = selected.split(" - ")[0].trim();
                artistIdField.setText(idStr);
            }
        } catch (SQLException e) {
            messageService.showError("Ошибка при загрузке исполнителей:\n" + e.getMessage());
        }
    }

}
