module org.example.dataapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires static lombok;
    requires javafx.graphics;

    opens ru.mirea.dataapp to javafx.fxml;
    exports ru.mirea.dataapp;
    exports ru.mirea.dataapp.controller;
    opens ru.mirea.dataapp.controller to javafx.fxml;
}