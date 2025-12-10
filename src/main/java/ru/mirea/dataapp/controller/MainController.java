package ru.mirea.dataapp.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mirea.dataapp.database.AlbumRepository;
import ru.mirea.dataapp.database.ArtistRepository;
import ru.mirea.dataapp.database.DataBaseManager;
import ru.mirea.dataapp.database.TrackRepository;
import ru.mirea.dataapp.util.MessageService;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML
    private TextField urlField;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton;

    @FXML
    private ComboBox<String> tableSelector;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @FXML
    private TableView<Map<String, Object>> tableView;

    private final DataBaseManager databaseManager;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final MessageService messageService;

    public MainController(DataBaseManager db,
                          ArtistRepository ar,
                          AlbumRepository al,
                          TrackRepository tr,
                          MessageService ms) {
        this.databaseManager = db;
        this.artistRepository = ar;
        this.albumRepository = al;
        this.trackRepository = tr;
        this.messageService = ms;
    }

    @FXML
    private void initialize() {
        tableSelector.getItems().addAll("Artist", "Album", "Track");
        tableSelector.getSelectionModel().selectFirst();

        setDataControlsDisabled(true);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tableSelector.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        loadSelectedTable();
                    }
                });
    }

    private void setDataControlsDisabled(boolean disabled) {
        tableSelector.setDisable(disabled);
        addButton.setDisable(disabled);
        editButton.setDisable(disabled);
        deleteButton.setDisable(disabled);
        tableView.setDisable(disabled);
    }

    @FXML
    private void onConnect() {
        String url = urlField.getText();
        String user = userField.getText();
        String password = passwordField.getText();

        try {
            databaseManager.connect(url, user, password);
            setDataControlsDisabled(false);
            loadSelectedTable();
        } catch (SQLException e) {
            messageService.showError("Не удалось подключиться к базе.\n" + e.getMessage());
        }
    }

    private void loadSelectedTable() {
        String selected = tableSelector.getValue();
        try {
            List<Map<String, Object>> data;
            switch (selected) {
                case "Artist" -> data = artistRepository.findAll();
                case "Album" -> data = albumRepository.findAll();
                case "Track" -> data = trackRepository.findAll();
                default -> {
                    return;
                }
            }
            setupTableColumns(data);
            tableView.getItems().setAll(data);
        } catch (SQLException e) {
            messageService.showError("Ошибка загрузки данных.\n" + e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        String selectedTable = tableSelector.getValue();
        Map<String, Object> row = getSelectedRow();
        if (row == null) {
            messageService.showWarning("Выберите строку в таблице.");
            return;
        }

        try {
            switch (selectedTable) {
                case "Artist" -> {
                    int id = (int) row.get("artist_id");
                    artistRepository.deleteById(id);
                }
                case "Album" -> {
                    int id = (int) row.get("album_id");
                    albumRepository.deleteAlbum(id);
                }
                case "Track" -> {
                    int id = (int) row.get("track_id");
                    trackRepository.deleteTrack(id);
                }
            }
            loadSelectedTable();
        } catch (SQLException e) {
            messageService.showError("Ошибка при удалении.\n" + e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        String selected = tableSelector.getValue();
        if (selected == null) return;

        try {
            boolean ok = switch (selected) {
                case "Artist" -> showArtistEditDialog(null);
                case "Album"  -> showAlbumEditDialog(null);
                case "Track"  -> showTrackEditDialog(null);
                default       -> false;
            };
            if (ok) {
                loadSelectedTable();
            }
        } catch (Exception e) {
            messageService.showError("Ошибка при добавлении:\n" + e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        String selected = tableSelector.getValue();
        Map<String, Object> row = tableView.getSelectionModel().getSelectedItem();
        if (selected == null || row == null) {
            messageService.showWarning("Выберите строку в таблице.");
            return;
        }

        try {
            boolean ok = switch (selected) {
                case "Artist" -> showArtistEditDialog(row);
                case "Album"  -> showAlbumEditDialog(row);
                case "Track"  -> showTrackEditDialog(row);
                default       -> false;
            };
            if (ok) {
                loadSelectedTable();
            }
        } catch (Exception e) {
            messageService.showError("Ошибка при изменении:\n" + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String selected = tableSelector.getValue();
        String text = searchField.getText();
        if (selected == null || text == null || text.isBlank()) {
            loadSelectedTable(); // если строка пустая — показать всё
            return;
        }

        try {
            List<Map<String, Object>> data;
            switch (selected) {
                case "Artist" -> data = artistRepository.findByName(text);
                case "Album"  -> data = albumRepository.findByName(text);
                case "Track"  -> data = trackRepository.findByName(text);
                default       -> { return; }
            }
            setupTableColumns(data);
            tableView.getItems().setAll(data);
        } catch (SQLException e) {
            messageService.showError("Ошибка при поиске:\n" + e.getMessage());
        }
    }

    private boolean showAlbumEditDialog(Map<String, Object> row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/mirea/dataapp/AlbumEditForm.fxml")
            );

            loader.setControllerFactory(c -> new AlbumEditController(
                    messageService, albumRepository, artistRepository
            ));

            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Добавить альбом" : "Изменить альбом");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            AlbumEditController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setInitialData(row);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (Exception e) {
            messageService.showError("Не удалось открыть окно редактирования:\n" + e);
            return false;
        }
    }


    private boolean showTrackEditDialog(Map<String, Object> row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/mirea/dataapp/TrackEditForm.fxml")
            );

            loader.setControllerFactory(c -> new TrackEditController(
                    messageService, trackRepository, albumRepository
            ));

            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Добавить трек" : "Изменить трек");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            TrackEditController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setInitialData(row);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (Exception e) {
            messageService.showError("Не удалось открыть окно редактирования:\n" + e);
            return false;
        }
    }

    private boolean showArtistEditDialog(Map<String, Object> row) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/mirea/dataapp/ArtistEditForm.fxml")
            );

            loader.setControllerFactory(c -> new ArtistEditController(
                    messageService, artistRepository
            ));

            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(row == null ? "Добавить исполнителя" : "Изменить исполнителя");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ArtistEditController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setInitialData(row);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (Exception e) {
            messageService.showError("Не удалось открыть окно редактирования:\n" + e);
            return false;
        }

    }

    private Map<String, Object> getSelectedRow() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void setupTableColumns(List<Map<String, Object>> data) {
        tableView.getColumns().clear();

        if (data == null || data.isEmpty()) {
            return;
        }

        String selected = tableSelector.getValue();
        List<String> columnsOrder;

        switch (selected) {
            case "Artist" -> columnsOrder = List.of("artist_id", "artist_name", "listeners");
            case "Album" ->
                    columnsOrder = List.of("album_id", "album_name", "artist_id", "listeners", "duration", "release_year");
            case "Track" ->
                    columnsOrder = List.of("track_id", "track_name", "listeners", "album_id", "duration", "genre");
            default -> {
                return;
            }
        }

        Map<String, Object> firstRow = data.get(0);

        for (String key : columnsOrder) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(key);
            column.setCellValueFactory(param ->
                    new ReadOnlyObjectWrapper<>(param.getValue().get(key)));
            column.setPrefWidth(150);
            tableView.getColumns().add(column);
        }
    }





}


