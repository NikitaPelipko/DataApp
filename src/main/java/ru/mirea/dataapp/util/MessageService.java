package ru.mirea.dataapp.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MessageService {

    public void showInfo(String message) {
        showAlert(AlertType.INFORMATION, "Информация", message);
    }

    public void showWarning(String message) {
        showAlert(AlertType.WARNING, "Предупреждение", message);
    }

    public void showError(String message) {
        showAlert(AlertType.ERROR, "Ошибка", message);
    }

    private static void showAlert(AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

