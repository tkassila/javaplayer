package com.metait.javafxplayer.bookmark;

import com.metait.javafxplayer.PlayerController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BookMarkController {

    private PlayerController playerController = null;

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
    @FXML
    private TextField textFieldDescription;

    private boolean bDescriptionChanged = false;
    private boolean bGotoBMarkPressed = false;

    enum SELECTED_LIST_CONTROL { COLLECTION_LIST, BOOKMARK_LIST };
    private SELECTED_LIST_CONTROL selectedListType = SELECTED_LIST_CONTROL.BOOKMARK_LIST;

    // private List<BookMarkCollection> bookMarkCollections = new ArrayList<BookMarkCollection>();
    private ObservableList<BookMarkCollection> bookMarkCollections = FXCollections.observableArrayList();
    public ObservableList<BookMarkCollection> getBookMarkCollections() { return bookMarkCollections; }

    public void setPlayerController(PlayerController playerController){
        this.playerController = playerController;
    }

    public void setBookMarks(List<BookMarkCollection> items)
    {
        if (items != null)
            bookMarkCollections.addAll(items);
        else
            if (bookMarkCollections != null && items == null)
                bookMarkCollections.clear();
    }

    public List<BookMarkCollection> getBookMarks() { return bookMarkCollections; }

    private void listBookMarkItemChanged(BookMark newValue) {
        // Your action here
        //   System.out.println("Selected item: " + newValue);

        if (newValue == null) {
            buttonEditBookmark.setDisable(true);
            buttonDeleteBookmark.setDisable(true);
            buttonGotoBookmark.setDisable(true);
            return;
        }
        selectedListType = SELECTED_LIST_CONTROL.BOOKMARK_LIST;
        textFieldDescription.setText(newValue.getStrDescription());
        buttonEditBookmark.setDisable(false);
        buttonDeleteBookmark.setDisable(false);
        buttonGotoBookmark.setDisable(false);
    }

    private void listViewCollectionItemChanged(BookMarkCollection newValue)
    {
        selectedListType = SELECTED_LIST_CONTROL.COLLECTION_LIST;
        textFieldDescription.setText(newValue.getStrDescription());
        BookMark [] bookMarks = newValue.getBookMarks();
        listViewBookMark.getItems().clear();
        textFieldDescription.setEditable(false);
        if (bookMarks != null && bookMarks.length > 0)
        {
            listViewBookMark.getItems().addAll(bookMarks);
        }
        if (bookMarks.length ==0)
        {
            buttonEditBookmark.setDisable(true);
            buttonDeleteBookmark.setDisable(false);
            buttonGotoBookmark.setDisable(true);
        }
        else
        {
            buttonEditBookmark.setDisable(false);
            buttonDeleteBookmark.setDisable(true);
            buttonGotoBookmark.setDisable(true);
        }
    }
    @FXML
    public void initialize() {
        buttonHelpClose.defaultButtonProperty().bind(buttonHelpClose.focusedProperty());
        buttonEditBookmark.defaultButtonProperty().bind(buttonEditBookmark.focusedProperty());
        buttonDeleteBookmark.defaultButtonProperty().bind(buttonDeleteBookmark.focusedProperty());
        buttonGotoBookmark.defaultButtonProperty().bind(buttonGotoBookmark.focusedProperty());
        buttonEditBookmark.setDisable(true);
        buttonDeleteBookmark.setDisable(true);
        buttonGotoBookmark.setDisable(true);
        textFieldDescription.setEditable(false);

        if (bookMarkCollections != null) {
            listViewCollections.setCellFactory(new Callback<ListView<BookMarkCollection>, ListCell<BookMarkCollection>>() {
                @Override
                public ListCell<BookMarkCollection> call(ListView<BookMarkCollection> p) {
                    ListCell<BookMarkCollection> cell = new ListCell<BookMarkCollection>() {
                        @Override
                        protected void updateItem(BookMarkCollection t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                String strType = t.getType().toString().replace("_", " ");
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
                                setText(strType +" " +t.getStrDescription() +" " +t.getName());
                            }
                        }
                    };
                    return cell;
                }
            });
        }

        buttonEditBookmark.setDisable(true);
        buttonDeleteBookmark.setDisable(true);
        buttonGotoBookmark.setDisable(true);

        textFieldDescription.textProperty().addListener(        new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String>  observable, String oldValue, String newValue) {
                // Your action here
                //   System.out.println("Selected item: " + newValue);
                if (oldValue != null && !oldValue.equals(newValue) ) {
                    bDescriptionChanged = true;
                    if (selectedListType == SELECTED_LIST_CONTROL.BOOKMARK_LIST) {
                        BookMark bm = listViewBookMark.getSelectionModel().getSelectedItem();
                        if (bm != null) {
                            bm.setStrDescription(newValue);
                            listViewBookMark.refresh();
                        }
                    }
                    else
                    {
                        BookMarkCollection bc = listViewCollections.getSelectionModel().getSelectedItem();
                        if (bc == null)
                            return;
                        bc.setStrDescription(newValue);
                        listViewCollections.refresh();
                    }
                }
                else
                if (newValue != null && !newValue.equals(oldValue) ) {
                    bDescriptionChanged = true;
                    if (selectedListType == SELECTED_LIST_CONTROL.BOOKMARK_LIST) {
                        BookMark bm = listViewBookMark.getSelectionModel().getSelectedItem();
                        if (bm != null) {
                            bm.setStrDescription(newValue);
                            listViewBookMark.refresh();
                        }
                    }
                    else
                    {
                        BookMarkCollection bc = listViewCollections.getSelectionModel().getSelectedItem();
                        if (bc == null)
                            return;
                        bc.setStrDescription(newValue);
                        listViewCollections.refresh();
                    }
                }
            }
        });

        listViewBookMark.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;
            if (listViewBookMark.isFocused())
            {
                BookMark newValue2 =  listViewBookMark.getSelectionModel().getSelectedItem();
                if (newValue2 == null)
                    return;
                // listViewCollectionItemChange(newValue2);
            }
        });

        listViewBookMark.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<BookMark>() {
                    @Override
                    public void changed(ObservableValue<? extends BookMark> observable,
                                        BookMark oldValue, BookMark newValue) {
                        listBookMarkItemChanged(newValue);
                    }
                });

        listViewCollections.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;
            if (listViewCollections.isFocused())
            {
                BookMarkCollection newValue2 =  listViewCollections.getSelectionModel().getSelectedItem();
                if (newValue2 == null)
                    return;
                listViewCollectionItemChanged(newValue2);
            }
        });

        listViewCollections.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<BookMarkCollection>() {
                    @Override
                    public void changed(ObservableValue<? extends BookMarkCollection> observable,
                                        BookMarkCollection oldValue, BookMarkCollection newValue) {
                        // Your action here
                     //   System.out.println("Selected item: " + newValue);
                        if (newValue == null)
                            return;
                        listViewCollectionItemChanged(newValue);
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
        if (playerController != null) {
            BookMark selBookMark = listViewBookMark.getSelectionModel().getSelectedItem();
            if (selBookMark == null)
                return;
            bGotoBMarkPressed = true;
            playerController.setPlayAfterBookMark(selBookMark);
        }
    }

    public boolean getGotoBMarkPressed() { return bGotoBMarkPressed; }

    @FXML
    public void pressedButtonEditBookmark()
    {
        System.out.println("pressedButtonEditBookmark");
        textFieldDescription.setEditable(true);

        /*
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
         */
    }

    @FXML
    protected void pressedButtonDeleteBookmark()
    {
        System.out.println("pressedButtonDeleteBookmark");
        BookMarkCollection col = listViewCollections.getSelectionModel().getSelectedItem();
        if (col == null)
            return;
        BookMark [] bmks = col.getBookMarks();
        if (bmks == null || bmks.length == 0)
        {
            bookMarkCollections.remove(col);
            listViewCollections.getItems().remove(col);
            if (bookMarkCollections.size() == 0)
            {
                buttonEditBookmark.setDisable(true);
                buttonDeleteBookmark.setDisable(true);
                buttonGotoBookmark.setDisable(true);
            }
           // listViewCollections.setItems(bookMarkCollections);
            return;
        }
        BookMark selBookMark = listViewBookMark.getSelectionModel().getSelectedItem();
        if (selBookMark == null)
            return;

        listViewBookMark.getItems().remove(selBookMark);
        int size = listViewBookMark.getItems().size();
        BookMark [] array = new BookMark[size];
        array = listViewBookMark.getItems().toArray(array);
        col.setBookMarks(array);
        if (col.getBookMarks().length == 0)
            bookMarkCollections.remove(col);
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
        String strPlayFile = newBookMark.getPlayFilePath();
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