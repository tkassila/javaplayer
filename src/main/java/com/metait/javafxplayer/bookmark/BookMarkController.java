package com.metait.javafxplayer.bookmark;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class BookMarkController {

    @FXML
    private Button buttonHelpClose;
    @FXML
    private Button buttonEditBookmark;
    @FXML
    private Button buttonDeleteBookmark;
    @FXML
    private Button buttonGotoBookmark;

    private List<BookMarkCollection> bookMarkCollections = null;

    public void setBookMarks(List<BookMarkCollection> items)
    {
        bookMarkCollections = items;
    }
    public List<BookMarkCollection> getBookMarks() { return bookMarkCollections; }

    @FXML
    public void initialize() {
        buttonHelpClose.defaultButtonProperty().bind(buttonHelpClose.focusedProperty());
        buttonEditBookmark.defaultButtonProperty().bind(buttonEditBookmark.focusedProperty());
        buttonDeleteBookmark.defaultButtonProperty().bind(buttonDeleteBookmark.focusedProperty());
        buttonGotoBookmark.defaultButtonProperty().bind(buttonGotoBookmark.focusedProperty());
        // buttonHelpClose.defaultButtonProperty().bind(buttonHelpClose.focusedProperty());
        // buttonHelpClose.defaultButtonProperty().bind(buttonHelpClose.focusedProperty());

        /*
        WebEngine webEngine = webViewHelp.getEngine();
        webEngine.load(getClass().getResource("help.html").toString());
         */
    }

    @FXML
    public void pressedButtonGotoBookmark()
    {
        System.out.println("pressedButtonGotoBookmark");
    }

    @FXML
    public void pressedButtonEditBookmark()
    {
        System.out.println("pressedButtonEditBookmark");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    EditBookMarkController.class.getResource("javafxplayerbookmark.fxml"));
            EditBookMarkController dialogController = new EditBookMarkController();
            fxmlLoader.setController(dialogController);

            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, 800, 600);
            Stage stage = new Stage();
            // stage.setIconified(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            buttonEditBookmark.setDisable(true);
            // m_primaryStage.setScene(scene);
            stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    // bookMarkCollections = dialogController.getBookMarks();
                    System.out.println("2 handle(WindowEvent we");
                }
            });
            stage.showAndWait();
            buttonEditBookmark.setDisable(false);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @FXML
    public void pressedButtonDeletetBookmark()
    {
        System.out.println("pressedButtonDeletetBookmark");
    }

    @FXML
    public void pressedButtonHelpClose()
    {
        System.out.println("pressedButtonHelpClose");
        Stage stage = (Stage) buttonHelpClose.getScene().getWindow();
        // do what you have to do
        stage.fireEvent( new WindowEvent( stage, WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
}