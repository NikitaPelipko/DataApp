package ru.mirea.dataapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.mirea.dataapp.controller.MainController;
import ru.mirea.dataapp.database.DataBaseManagerImpl;
import ru.mirea.dataapp.database.AlbumRepositoryImpl;
import ru.mirea.dataapp.database.ArtistRepositoryImpl;
import ru.mirea.dataapp.database.TrackRepositoryImpl;
import ru.mirea.dataapp.util.MessageService;

import java.util.Objects;


public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var fxmlLocation = Launcher.class.getResource("MainForm.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Не найден main.fxml в resources!");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);


        var messageService = new MessageService();
        var databaseManager = new DataBaseManagerImpl(messageService);
        var artistRepo = new ArtistRepositoryImpl(messageService, databaseManager);
        var albumRepo = new AlbumRepositoryImpl(databaseManager, messageService);
        var trackRepo = new TrackRepositoryImpl(databaseManager, messageService);

        loader.setControllerFactory(c -> new MainController(
                databaseManager, artistRepo, albumRepo, trackRepo, messageService
        ));

        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 700);
        Image icon = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("icon.jpg")));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Music Database Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

