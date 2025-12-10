package ru.mirea.dataapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import ru.mirea.dataapp.database.ArtistRepository;
import ru.mirea.dataapp.util.MessageService;

import java.sql.SQLException;
import java.util.Map;


public class ArtistEditController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField listenersField;

    @Setter
    private Stage dialogStage;
    @Getter
    private boolean okClicked = false;

    private final MessageService messageService;
    private final ArtistRepository artistRepository;
    private Integer artistId;

    public ArtistEditController(MessageService messageService, ArtistRepository artistRepository) {
        this.messageService = messageService;
        this.artistRepository = artistRepository;
    }


    public void setInitialData(Map<String, Object> row) {
        if (row == null) {
            artistId = null;
            nameField.setText("");
            listenersField.setText("");
        } else {
            artistId = (Integer) row.get("artist_id");
            nameField.setText((String) row.get("artist_name"));
            listenersField.setText(String.valueOf(row.get("listeners")));
        }
    }

    @FXML
    private void onOk() {
        String name = nameField.getText();
        String listenersStr = listenersField.getText();

        if (name == null || name.isBlank()) {
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

        try {

            if (artistId == null) {
                artistRepository.saveArtist(name, listeners);
            } else {
                artistRepository.updateArtist(artistId, name, listeners);
            }

            okClicked = true;
            dialogStage.close();
        } catch (SQLException e) {
            messageService.showError("Ошибка при сохранении исполнителя:\n" + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }
}
