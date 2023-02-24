package com.metait.javafxplayer;

import com.metait.javafxplayer.config.PlayerConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayerApplication extends Application {

    private Stage m_primaryStage;
    private PlayerController controller = null;
    public PlayerController getController()
    {
        return controller;
    }

    public Stage  startUIAgain() throws IOException
    {
        Stage ret = new Stage();
        startUI(ret);
        return ret;
    }
    private void startUI(Stage stage) throws IOException
    {
        Locale def_locale = Locale.getDefault();
        Locale locale = new Locale("en", "UK");
        if (!def_locale.equals(locale))
            locale = new Locale("fi", "FI");
        boolean bMultiLanguage = PlayerController.bUseMultiLang;
       // PlayerController.locale = locale;
        controller = new PlayerController();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player-view.fxml"));
        if (bMultiLanguage) { // there are some differences between none i18 fxml file and i18 fxml file!
            locale = PlayerConfig.getLanguageLocale(); // The dff are size of buttons etc.
            PlayerController.locale = locale;
            ResourceBundle bundle = ResourceBundle.getBundle("com/metait/javafxplayer/lang", locale);
            fxmlLoader = new FXMLLoader(getClass().getResource("player-view18.fxml"), bundle);
        }
        // remove xml block from .fxml file: fx:controller="com.metait.javafxplayer.PlayerController"
        fxmlLoader.setController(controller);
        m_primaryStage = stage;
        controller.setPrimaryStage(m_primaryStage);
       // controller.setApplication(this);
        Parent loadedroot = fxmlLoader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        System.out.println(screenBounds);
        double width = 840;
        if (screenBounds.getWidth() != width)
            width = screenBounds.getWidth();
        double height = 300;
        if (screenBounds.getHeight() != height)
            height = screenBounds.getHeight() -250;
        Scene scene = new Scene(loadedroot, width, height);
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

        /*  boolean bMultiLanguage = JavaFxmlFileControllerI18.bUseMultiLang;
         JavaFxmlFileControllerI18.locale = locale;
         JavaFxmlFileControllerI18.setLanguageLocale();
         locale = JavaFxmlFileControllerI18.getLocale();
        if (bMultiLanguage) {
            ResourceBundle bundle = ResourceBundle.getBundle("com/metait/javafxmlfileI18convert/lang", locale);
            // System.out.println("path=" +JavaFxmlFileConver18Application.class.getPackageName().toString());
            String strPath = JavaFxmlFileConver18Application.class.getPackageName().replaceAll("\\.", "/");
            // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("javafxmlfileconverti18-view.fxml"));
            fxmlLoader = new FXMLLoader(getClass().getResource("javafxmlfileconverti18-viewtemplate.fxml"), bundle);
        }
        */
       // FXMLLoader fxmlLoader = new FXMLLoader(PlayerApplication.class.getResource("player-view.fxml"));
       // Locale locale = new Locale("fi", "FI");

        Locale locale = new Locale("en", "UK");
        boolean bMultiLanguage = PlayerController.bUseMultiLang;
        // PlayerController.locale = locale;

        ResourceBundle bundle = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player-view.fxml"));
        if (bMultiLanguage) { // there are some differences between none i18 fxml file and i18 fxml file!
            locale = PlayerConfig.getLanguageLocale(); // The dff are size of buttons etc.
            PlayerController.locale = locale;
            bundle = ResourceBundle.getBundle("com/metait/javafxplayer/lang", locale);
            fxmlLoader = new FXMLLoader(getClass().getResource("player-view18.fxml"), bundle);
        }
        // remove xml block from .fxml file: fx:controller="com.metait.javafxplayer.PlayerController"
        controller = new PlayerController();
        controller.setResourceBundle(bundle);
        double width = 840;
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        System.out.println(screenBounds);
        if (screenBounds.getWidth() != width)
            width = screenBounds.getWidth();
        double height = 300;
        if (screenBounds.getHeight() != height)
            height = screenBounds.getHeight() -250;
        controller.setScreenValues(height, height);
        fxmlLoader.setController(controller);
        m_primaryStage = stage;
        controller.setPrimaryStage(m_primaryStage);
     //   controller.setApplication(this);
        Parent loadedroot = fxmlLoader.load();

        Scene scene = new Scene(loadedroot, width, height);
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