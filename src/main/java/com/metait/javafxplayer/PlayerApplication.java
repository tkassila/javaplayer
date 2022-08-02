package com.metait.javafxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PlayerApplication extends Application {

    private Stage m_primaryStage;
    private PlayerController controller = new PlayerController();
    public PlayerController getController()
    {
        return controller;
    }

    @Override
    public void start(Stage stage) throws IOException {

        System.out.println("main start!");

        Parameters parameters = getParameters();

        Map<String, String> namedParameters = parameters.getNamed();
        List<String> rawArguments = parameters.getRaw();
        List<String> unnamedParameters = parameters.getUnnamed();

        /*
        System.out.println("\nnamedParameters -");
        for (Map.Entry<String, String> entry : namedParameters.entrySet())
            System.out.println(entry.getKey() + " : " + entry.getValue());

        System.out.println("\nrawArguments -");
        for (String raw : rawArguments)
            System.out.println(raw);

        System.out.println("\nunnamedParameters -");
        for (String unnamed : unnamedParameters)
            System.out.println(unnamed);
         */

       // FXMLLoader fxmlLoader = new FXMLLoader(PlayerApplication.class.getResource("player-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player-view.fxml"));
        // remove xml block from .fxml file: fx:controller="com.metait.javafxplayer.PlayerController"
        fxmlLoader.setController(controller);
        m_primaryStage = stage;
        controller.setPrimaryStage(m_primaryStage);
        Parent loadedroot = fxmlLoader.load();
        Scene scene = new Scene(loadedroot, 840, 500);
        stage.setTitle("Music player for video, music and daisy books");

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                controller.handleKeyEvent(event);
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}