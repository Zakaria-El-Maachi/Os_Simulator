package com.os.mmu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MMUGUIApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(com.os.cpuscheduler.gui.MMUGUIApplication.class.getResource("/com/os/mmu/gui/MMU.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 560);
//        scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
        stage.setTitle("Memory Management Unit Simulator");
        stage.setScene(scene);
        stage.show();
    }
}
