package com.os.cpuscheduler.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MMUGUIApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MMUGUIApplication.class.getResource("CPUScheduler.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 560);
        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setTitle("CPU Scheduler Simulator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}