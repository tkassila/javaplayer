package com.metait.javafxplayer.bookmark;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    @FXML
    private ListView<BookMarkCollection> listViewCollections;
    @FXML
    private ListView<BookMark> listViewBookMark;

    private List<BookMarkCollection> bookMarkCollections = new ArrayList<BookMarkCollection>();

    public void setBookMarks(List<BookMarkCollection> items)
    {
        if (items != null)
            bookMarkCollections = items;
        else
            if (bookMarkCollections != null && items == null)
                bookMarkCollections.clear();
    }

    public List<BookMarkCollection> getBookMarks() { return bookMarkCollections; }

    @FXML
    public void initialize() {
        buttonHelpClose.defaultButtonProperty().bind(buttonHelpClose.focusedProperty());
        buttonEditBookmark.defaultButtonProperty().bind(buttonEditBookmark.focusedProperty());
        buttonDeleteBookmark.defaultButtonProperty().bind(buttonDeleteBookmark.focusedProperty());
        buttonGotoBookmark.defaultButtonProperty().bind(buttonGotoBookmark.focusedProperty());

        if (bookMarkCollections.size()==0)
        {
            buttonEditBookmark.setDisable(true);
            buttonDeleteBookmark.setDisable(true);
            buttonGotoBookmark.setDisable(true);
        }

        if (bookMarkCollections != null) {
            listViewCollections.setCellFactory(new Callback<ListView<BookMarkCollection>, ListCell<BookMarkCollection>>() {
                @Override
                public ListCell<BookMarkCollection> call(ListView<BookMarkCollection> p) {
                    ListCell<BookMarkCollection> cell = new ListCell<BookMarkCollection>() {
                        @Override
                        protected void updateItem(BookMarkCollection t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                String strType = "";
                                /*
                                BookMarkCollection.BOOKMARK_TYPE type = getBookMarkType(t.getBookMarks());
                                switch (type)
                                {
                                    case BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR:
                                        strType = "AUDIO DIR";
                                        break;
                                    case BookMarkCollection.BOOKMARK_TYPE.DAISY:
                                        strType = "DAISY";
                                        break;
                                    case BookMarkCollection.BOOKMARK_TYPE.AUDIO_FILE:
                                        strType = "FILE";
                                        break;
                                }
                                 */
                                setText(strType +" " +t.getPlayFilePath() +" ");
                            }
                        }
                    };
                    return cell;
                }
            });
        }

        listViewCollections.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<BookMarkCollection>() {
                    @Override
                    public void changed(ObservableValue<? extends BookMarkCollection> observable,
                                        BookMarkCollection oldValue, BookMarkCollection newValue) {
                        // Your action here
                        System.out.println("Selected item: " + newValue);
                        BookMark [] bookMarks = newValue.getBookMarks();
                        listViewBookMark.getItems().clear();
                        if (bookMarks != null && bookMarks.length > 0)
                        {
                            listViewBookMark.getItems().addAll(bookMarks);
                        }
                    }
        });
        listViewCollections.getItems().addAll(bookMarkCollections);

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
        BookMarkCollection col = listViewCollections.getSelectionModel().getSelectedItem();
        if (col == null)
            return;
        BookMark [] bmks = col.getBookMarks();
        if (bmks == null || bmks.length == 0)
        {
            bookMarkCollections.remove(col);
            return;
        }
        BookMark selBookMark = listViewBookMark.getSelectionModel().getSelectedItem();
        if (selBookMark == null)
            return;
        listViewCollections.getItems().remove(selBookMark);
    }

    @FXML
    public void pressedButtonHelpClose()
    {
        System.out.println("pressedButtonHelpClose");
        Stage stage = (Stage) buttonHelpClose.getScene().getWindow();
        // do what you have to do
        stage.fireEvent( new WindowEvent( stage, WindowEvent.WINDOW_CLOSE_REQUEST ));
    }

    private BookMarkCollection.BOOKMARK_TYPE getBookMarkType(BookMark newBookMark)
    {
        BookMarkCollection.BOOKMARK_TYPE ret = null;
        if (newBookMark == null)
            return null;
        String strPlayFile = newBookMark.getPlayfilepath();
        if (strPlayFile != null) {
            File fPlay = new File(strPlayFile);
            if (!fPlay.exists())
                return null;
            if (fPlay.getName().endsWith(".html")
                    || fPlay.getName().endsWith(".html")
                    || fPlay.getName().endsWith(".htm")) {
                ret = BookMarkCollection.BOOKMARK_TYPE.DAISY;
            } else if (fPlay.isDirectory()) {
                ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR;
            } else {
                ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_FILE;
            }
        }
        return ret;
    }
}