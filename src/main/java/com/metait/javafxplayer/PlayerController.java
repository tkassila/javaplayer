package com.metait.javafxplayer;

import com.metait.javafxplayer.bookmark.BookMark;
import com.metait.javafxplayer.bookmark.BookMarkCollection;
import com.metait.javafxplayer.bookmark.BookMarkController;
import com.metait.javafxplayer.daisy2.*;
import com.metait.javafxplayer.help.HelpController;

// import com.sun.jndi.toolkit.url.Uri;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/*
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Dialog;
import javafx.collections.ObservableList;
 */
import javafx.scene.control.Alert.AlertType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import javafx.scene.control.Control;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Enumeration;

import com.google.gson.Gson;

import com.metait.javafxplayer.config.PlayerConfig;

/*
import com.gtranslate.Audio;
import com.gtranslate.Language;
import javazoom.jl.decoder.JavaLayerException;
*/

// import eu.tjago.speechfxapp.util.ResourceSingleton;
// import eu.tjago.speechfxapp.util.VoiceReaderService;

/**
 * This class can open javafx media supported audio and video files, and also daisy 2 html files to listen.
 * If a daisybook is selected as form of .html file, you can listen book with automactic play, select next
 * and previous .mp3 file or click from webview component web pageg link (= per book xx page or from the
 * beginning of mp3 file).
 * When playing, you can stop, pause or select music postion to play.
 */
public class PlayerController implements IFileContainer, IParLevelSetter {
    @FXML
    private Label labelSeleectedFile;
   // @FXML
   // private Button buttonAudioDir;
    //@FXML
    //private Button buttonAudioFile;
    @FXML
    private Button buttonPlay;
    @FXML
    private Button buttonPreviousTrack;
    @FXML
    private Button buttonNextTrack;
    @FXML
    private ScrollPane mediaScrollPane;
    @FXML
    private WebView webView;
    @FXML
    private ScrollPane scrollPaneMain;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button buttonPrevLink;
    @FXML
    private Button buttonNextLink;
    @FXML
    private Label labelMsg;
    @FXML
    private RadioButton radioButtonFreeTts;
    @FXML
    private RadioButton radioButtonESpeak;
    @FXML
    private RadioButton radioButtonNoTTS;
    @FXML
    private Button buttonHelp;
    @FXML
    private Button buttonBookMarks;
    @FXML
    private Button buttonNewBookMark;
    @FXML
    private Button buttonLowerLevel;
    @FXML
    private Button buttonUpperLevel;
    @FXML
    private Label labelLevel;
    @FXML
    private MenuItem menuItemOpenDir;
    @FXML
    private MenuItem menuItemOpenFile;
    @FXML
    private MenuItem menuItemOpenDaisyFile;
    @FXML
    private Button buttonPrevLevelLink;
    @FXML
    private Button buttonNextLevelLink;
    @FXML
    private Label labelLinkMsg;
    @FXML
    private RadioButton radioButtonOwnTTS;
    @FXML
    private ComboBox<String> comboBoxLanguage;

    private boolean isFocusNotInWebView = true;
    private Stage bookMarkStage = null;

    private PlayerApplication m_application = null;
    private File filePlay;
    enum PAR_LEVEL_DIRECTION { UPWARD_PAR_LEVEL_DIRECTION, DOWNWARD_PAR_LEVEL_DIRECTION  };
    private PAR_LEVEL_DIRECTION par_level_direction = PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION;
    private int iSelectedSmilParLevel = 0; // user selectetd smil par item level; 0 = all!
    private int iMaxSmilParLevel = 0; // readed max  smil levels
    private int iMaxSmilParLevelNormalPage = 0;
    private BookMarkController dialogController = null;

    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private File dirChooser = null;
    private File fChooser = null;
    private FileChooser fileChooser = new FileChooser();
    private Stage primaryStage;

    private String strVolume_UI = "Volume";
    private String strTime_UI = "Time";
    private String str_mediapane_1 = "Media meta information";
    private String str_mediapane_2 = "Web control";
    private String str_mediapane_3 = "previous 10 sec button";
    private String str_mediapane_4 = "next 10 sec button";
    private String str_mediapane_5 = "play or pause button";
    private String str_mediapane_6 = "audio time slider";
    private String str_mediapane_7 = "play time value";
    private String str_mediapane_8 = "volume slider";
    private ResourceBundle m_bundle = null;
    private MediaControlPane mediaControlPane = null;
    private final String [] arrFileExtenstions = new String[]
            {".mp3", ".html",".htm", ".wav",".mp4",".aiff", ".aif", ".aifc", ".m4a", ".m4b", ".m4p", ".m4r", ".m4v", ".3gp",
            ".flv", ".f4v", ".f4p", ".f4a", ".f4b"}; // add also html files, because this class
            // can open daisy html files to listen!

    private ExtensionsFilter audiofilter = new ExtensionsFilter(arrFileExtenstions);
    private File m_selectedFile = null;
    private File selectedDirectory = null;
    private HashMap<String,String> hashMapSmils = new HashMap<>();
    private List<String> listSmils = new ArrayList<>();
    private HashMap<String, SmilData> hashMapHRefs = new HashMap<>();
    private HashMap<String,String> hashMapTexts = new HashMap<>();
    private HashMap<String,SmilData> hashMapSmilData = new HashMap<>();
    private List<File> listMp3Files = new ArrayList<File>();
    private HashMap<String,File> hashMapMp3Files = new HashMap<>();
    private boolean bWebViewLoaded = false;
    private File [] arrAudioFiles = null;
    private volatile int iIndArrDirFiles = -1;
    private AtomicBoolean bPreventIncreaseArrayIndex = new AtomicBoolean(false);
    private String execJs = null;
    private Thread voiceReadingThread;
    private String speechLanaguage = "fi";
    private List<BookMarkCollection> bookMarkCollections = null;
    private static String java_user_home = System.getProperty("user.home");
    public static final String cnstUserDirOfApp = ".javaplayerfx";
    public static final String cnstUserJsonFileOfApp = ".javaplayerfx.json";
    private static final String cnstNoTTS = "notts";
    private static final String cnstFreeTTS = "freetts";
    private static final String cnstEspeakTTS = "espeaktts";;
    private BookMark autoBookMark = null;
    private File daisyIndexFile = null;
    private double dCurrentPosition = -1.0;

    private HelpController helpController = new HelpController();
    private Stage stageHelp;
    private HashMap<String,Integer> hashMapLevelWords = new HashMap<String,Integer>();
    private HashMap<String,Integer> hashMapLevelHeaderWords = new HashMap<String,Integer>();
    private HashMap<String,SmilIndexLink> hashMapSmilIndexLinks = new HashMap<String,SmilIndexLink>();
    private List<SmilData_Par> allSmilData_ParItems = new ArrayList<>();
    private SmilData_ParMeta metadataSmilDataPar = new SmilData_ParMeta();
    private HashMap<String, SmilData_ParGroup> metaGroups = new HashMap<String, SmilData_ParGroup>();
    private SmilData_ParGroup currentMetaGroup = null;
    private final String cnstAllPages = "all-pages";
    private final String cnstSentence = "sentence";
    private final String cnstTimeShift = "timeshift";
    private final String cnstParItemsAbovePages = "paritems-above-than-pages";
    private final String cnstParItemsLowerPages = "paritems-lower-than-pages";
    private SmilData_Par currentNormalPage = null;
    private SmilData_Par currentHeader = null;
    private boolean bCreateMetaLevelsAbovePages = false;
    private int iMaxMetaLevel = 0;
    private SmilData_Par logicalRoot = new SmilData_Par(SmilData_Par.cnstLogicalRoot);
    private HashMap<String, SmilData_Par> lastParentSmilData_Pars = new HashMap<>();
    private boolean isDaisyNcc = false;
    public static boolean bUseMultiLang = true;
    public static Locale locale = null;
    public boolean getMultiLanguage() { return bUseMultiLang; }
    private static final String cnst_last_fxml_file = "last_fxml_file=";
    private static File static_selectedFxmlFile;
    private static final String cnst_last_lang_properties_file = "last_lang_properties_file=";
    private static File static_properties_file;
    private static File static_properties_file2;
    private double screen_height = 0.0;
    private double screen_wight = 0.0;
    public void setScreenValues(double height, double width){
        screen_height = height;
        screen_wight = width;  }

    public static Locale getLocale()
    {
        return locale;
    }
    final String cnstStringAutomatic = "Automatic play location";

    private static final String cnst_last_lang2_properties_file = "last_lang2_properties_file=";

  //  public void setApplication(PlayerApplication app) { this.m_application = app; }

    public static void setLanguageLocale_Old()
    {
        String strUserHome = System.getProperty("user.home");
        if (strUserHome != null && strUserHome.trim().length()>0)
        {
            File userDir = new File(strUserHome);
            if (userDir.exists())
            {
                File appDir = new File(userDir.getAbsolutePath() +File.separator
                        +".javaplayerfx");
                if (!appDir.exists())
                    if (!appDir.mkdir())
                    {
                        System.err.println("Cannot create app dir in: ");
                        return;
                    }
                File appFile = new File(appDir.getAbsolutePath() +File.separator
                        +"config.properties");
                if (!appFile.exists())
                    return;

                try {
                    FileReader myReader = new FileReader(appFile);
                    BufferedReader bufferedReader = new BufferedReader(myReader);
                    String line = null;
                    while((line = bufferedReader.readLine()) != null)
                    {
                        if (line != null && line.trim().length()>0)
                        {
                            String search = "gui_language=";
                            int ind = line.indexOf(search);
                            if (ind > -1) {
                                String langValue = line.substring(ind +search.length());
                                if (langValue != null && langValue.trim().length()>0)
                                {
                                    String [] langparts = langValue.split("_");
                                    if (langparts != null && langparts.length ==2)
                                    {
                                        Locale locale = new Locale(langparts[0], langparts[1]);
                                        if (locale != null)
                                            PlayerController.locale = locale;
                                    }
                                }
                            }
                            else {
                                search = cnst_last_fxml_file;
                                ind = line.indexOf(search);
                                if (ind > -1) {
                                    String strValue = line.substring(ind +search.length());
                                    if (strValue != null && strValue.trim().length()>0) {
                                        static_selectedFxmlFile = new File(strValue);
                                    }
                                }
                                else {
                                    search = cnst_last_lang_properties_file;
                                    ind = line.indexOf(search);
                                    if (ind > -1) {
                                        String strValue = line.substring(ind +search.length());
                                        if (strValue != null && strValue.trim().length()>0) {
                                            static_properties_file = new File(strValue);
                                        }
                                    }
                                    else {
                                        search = cnst_last_lang2_properties_file;
                                        ind = line.indexOf(search);
                                        if (ind > -1) {
                                            String strValue = line.substring(ind +search.length());
                                            if (strValue != null && strValue.trim().length()>0) {
                                                static_properties_file2 = new File(strValue);
                                            }
                                        }
                                    }
                                }
                            }
                           /*
                           if (ind > -1) {
                               if (selectedFxmlFile != null)
                               sb.append(cnst_last_fxml_file +selectedFxmlFile.getAbsolutePath() +"\n");
                           if (propertiesFile != null)
                               sb.append(cnst_last_lang_properties_file +propertiesFile.getAbsolutePath() +"\n");
                           if (propertiesFile2 != null)
                               sb.append(cnst_last_lang2_properties_file +propertiesFile2.getAbsolutePath() +"\n");
                           */
                        }
                    }
                    bufferedReader.close();
                    System.out.println("Successfully read from the file: " +appFile.getAbsolutePath());
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }
    }

    private class DescribeMetaGroups {
        public int iNumberMetaGroups = 0;
        public int iNumberOfPage = 0;
        public int iNumberOfSentence = 0;
        public int iNumberOfTimeShift = 0;
        public HashMap<String, String> hmNumberMetaGroup = new HashMap<>();
    }
    private DescribeMetaGroups describeMetaGroups = new DescribeMetaGroups();

    enum USER_SELECTED_OPEN_DIR_OR_FILE {
        DAISY_HTML, AUDIO_FILE, AUDIO_DIRECTORY
    }

    private USER_SELECTED_OPEN_DIR_OR_FILE user_selected_to_open;

    private final int currentTimeShift = 60 * 1000;
    public void setPrimaryStage(Stage stage)
    {
        primaryStage = stage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                appIsClosing();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void deleteAllEarlierData()
    {
        hashMapHRefs.clear();
        listSmils.clear();
        hashMapHRefs.clear();
        hashMapTexts.clear();
        hashMapSmilData.clear();
        listMp3Files.clear();
        hashMapMp3Files.clear();
        bWebViewLoaded = false;
        arrAudioFiles = null;
        iIndArrDirFiles = -1;
    }

    protected void appIsClosing()
    {
        if (mediaControlPane == null)
            return;
        dCurrentPosition = mediaControlPane.getStopAndCurrentTime();
        File playFile = m_selectedFile; // mediaControlPane.getPlayFile();
       // bookMarkCollections = mediaControlPane.getBookMarkList();
        savePlayPositionAndBookMarks(iIndArrDirFiles, selectedDirectory, playFile,
                       dCurrentPosition, bookMarkCollections,
                       ( radioButtonNoTTS.isSelected() ? cnstNoTTS : (
                               radioButtonFreeTts.isSelected() ? cnstFreeTTS : cnstEspeakTTS
                               )), daisyIndexFile);
    }

    private String getAutoBookMarkString(BookMark autoBookMark)
    {
        String ret = "";
        if (autoBookMark != null)
        {
            ret = getStringOfOneBookMark(autoBookMark);
        }
        return ret;
    }

    /*
    private String getUserBookMarkString()
    {
        String ret = "";
        if (this.bookMarkCollections != null && this.bookMarkCollections.size()>0)
        {
            int max = this.bookMarkCollections.size();
            StringBuffer sb = new StringBuffer();
            BookMarkCollection [] arrbm = new BookMarkCollection[max];
            arrbm = bookMarkCollections.toArray(arrbm);
            int ind = 0;
            for(BookMarkCollection bm : arrbm) {
                if (ind > 0)
                    sb.append("\n,");
                sb.append(getStringOfOneBookMark(bm));
                ind++;
            }
            return sb.toString();
        }
        return ret;
    }
     */

    private String getJsonString(String strJson)
    {
        String ret = strJson;
        if (ret == null)
            ret = "";
        ret = ret.replaceAll("\\\\","\\\\\\\\");
        return ret;
    }

    private String getStringOfOneBookMark(BookMark bm)
    {
        String ret = "";
        if (bm != null)
        {
            Gson gson = new Gson();
            String json = gson.toJson(bm);
            ret = json;
            /*
            ret = " \"" +BookMark.cnstBookMark_name + "\": \"" +getJsonString(bm.getName()) +"\",\n"
                    + "\"" + BookMark.cnstBookMark_dirpath +"\": \"" +getJsonString(bm.getDirpath()) +"\",\n"
                    + "\"" + BookMark.cnstBookMark_playpath + "\": \"" +getJsonString(bm.getPlayfilepath()) +"\",\n"
                    + "\"" + BookMark.cnstBookMark_playdirindex + "\": " +bm.getIndarrdirfiles() +",\n"
                    +" \"" +BookMark.cnstBookMark_position+ "\": " +bm.getPosition() +"\n";
             */
        }
        return ret;
    }

    private BookMark getOneBookMarkFromString(String strValue)
    {
        BookMark ret = null;
        if (strValue != null && strValue.trim().length() > 0)
        {
            ret = new BookMark();
            // ret.setName(getBookMarkStringValue(, strValue));
        }
        return ret;
    }

    private String getBookMarkStringValue(String fieldname, String strJson)
    {
        String ret = "";
        if (fieldname != null && fieldname.trim().length()> 0
            && strJson != null && strJson.trim().length()> 0) {
            String search = "\"" + fieldname + "\":";
            int ind = strJson.indexOf(search);
            if (ind > -1) {
                String strFound = strJson.substring(ind + search.length());
                if (strFound != null && strFound.trim().length() > 0) {
                    int ind2 = strFound.indexOf('[');
                    if (ind2 > -1) {
                        int ind3 = strFound.indexOf(']', ind2);
                        if (ind3 > -1) {
                            strFound = strFound.substring(ind2, ind3);
                            return strFound;
                        }
                    }
                } else {
                    int ind2 = strFound.indexOf('{');
                    if (ind2 > -1) {
                        int ind3 = strFound.indexOf('}', ind2);
                        if (ind3 > -1) {
                            strFound = strFound.substring(ind2, ind3);
                            return strFound;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void savePlayPositionAndBookMarks(int iIndArrDirFiles,
            File selectedDirectory, File playFile, double currentTime,
            List<BookMarkCollection> bookMarkCollections,
            String strReaioButtonSelected, File daisyIndexFile)
    {
        if (java_user_home == null)
            return ;
        File appUserDir = new File(java_user_home +File.separator +cnstUserDirOfApp);
        if (!appUserDir.exists())
            if (!appUserDir.mkdir())
                return;
    //    String strBookMarks = "";
        BookMark autoBookMark = null;
        String strAutoBookMark = "";
        if (playFile != null) {
            autoBookMark = new BookMark();
          //  autoBookMark.setBookMarkDate(new Date(Calendar.getInstance().getTime()));
            // autoBookMarkCollection.setBookMarkPosition(currentTime);
            autoBookMark.setPlayFilePath(playFile.getAbsolutePath());
            if (selectedDirectory != null)
                autoBookMark.setDirPath(selectedDirectory.getAbsolutePath());
            autoBookMark.setName("autobookmark");
            autoBookMark.setPosition(currentTime / 1000);
            autoBookMark.setIndArrDirFiles("" +iIndArrDirFiles);
            if (daisyIndexFile != null)
                autoBookMark.setDaisyBookIndexPath(daisyIndexFile.getAbsolutePath());
            // autoBookMarkCollection.setStrIndArrDirFiles(""+iIndArrDirFiles);
        //    strAutoBookMark = getAutoBookMarkString(autoBookMark);
         //   strBookMarks = getUserBookMarkString();
            autoBookMark.setType(getUserSelectedOpenFileType(user_selected_to_open));
        }
        String strRadioButtonSelected = (strReaioButtonSelected != null && strReaioButtonSelected.trim().length()!=0
                                         ? strReaioButtonSelected : "");
        File appFile = new File(appUserDir.getAbsolutePath() +File.separator +cnstUserJsonFileOfApp);
       String strJsonTemplate = "{\n\"userBookmarks\": [$bookmarks],\"userRadiobutoonselected\": \"$radiobutoonselected\",\n\"autobookmark\":{$autobm}\n}\n";
        /*
        Path patNio = Paths.get(appFile.getAbsolutePath());
         */
        /*
        strJsonTemplate = strJsonTemplate.replace("$bookmarks", strBookMarks);
        strJsonTemplate = strJsonTemplate.replace("$radiobutoonselected", strRadioButtonSelected);
        strJsonTemplate = strJsonTemplate.replace("$autobm", strAutoBookMark);
        */

        Gson gson = new Gson();
        PlayerConfig config = new PlayerConfig();
        config.userSelectedLocale = locale;
        config.userRadiobuttonSelected = strRadioButtonSelected;
        config.autoBookmark = autoBookMark;
        if (bookMarkCollections != null) {
            BookMarkCollection[] arrBMarks = new BookMarkCollection[bookMarkCollections.size()];
            arrBMarks = bookMarkCollections.toArray(arrBMarks);
            config.userBookmarks = arrBMarks;
        }
        strJsonTemplate = gson.toJson(config);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(appFile);
            byte[] bytes = strJsonTemplate.getBytes();
            outputStream.write(bytes);
            // writer.close();
        }catch (IOException ioe) {
            ioe.printStackTrace();
            labelMsg.setText("Write file " + appFile.getAbsolutePath() + " error: " + ioe.getMessage());
        }finally {
            if (outputStream != null)
            {
                try {
                    outputStream.close();
                }catch (IOException ioe2) {
                    ioe2.printStackTrace();
                    labelMsg.setText("Closing file " + appFile.getAbsolutePath() + " error: " + ioe2.getMessage());
                }
            }
        }
    }

    private String getUserSelectedOpenFileType( USER_SELECTED_OPEN_DIR_OR_FILE user_selected_to_open)
    {
        if (user_selected_to_open == null)
            return "";
        if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY)
            return "DIR";
        if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_FILE)
            return "FILE";
        if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML)
            return "DAISY";
        return "";
    }

    private void setRemoveWarningLabelMsg(Boolean newValue, RadioButton rb)
    {
        String firstCBName = "FreeTTS";
        String secondCBName = "Espeak";
        if (newValue == null || rb == null)
            return;
        if (newValue && rb.isSelected())
        {
            Platform.runLater(new Runnable() {
                public void run() {
                    labelMsg.setText("Warning: you have both checkBoxies checked: " +firstCBName +" AND " +secondCBName +". Shoud be only one checkbox selected!");
                    labelMsg.requestFocus();
                }
            });
        }
        else
        {
            String text = labelMsg.getText();
            if (text != null && text.trim().length()>0 && text.startsWith("Warning"))
                labelMsg.setText("");
        }
    }

    @FXML
    protected void pressedButtonUpperLevel()
    {
    //    System.out.println("pressedButtonUpperLevel");
        changeParDirectionLevel(PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION);
    }

    @FXML
    protected void pressedButtonLowerLevel()
    {
       // System.out.println("pressedButtonLowerLevel");
        changeParDirectionLevel(PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION);
    }

    @FXML
    protected void pressedButtonPrevLevelLink()
    {
        System.out.println("pressedButtonPrevLevelLink");
        if (currentMetaGroup == null)
            return;
        seekPrevOrNextMediaGroupDatoToPlay(currentMetaGroup, this.iSelectedSmilParLevel,
                PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION);
    }

    @FXML
    protected void pressedButtonNextLevelLink()
    {
        System.out.println("pressedButtonNextLevelLink");
        seekPrevOrNextMediaGroupDatoToPlay(currentMetaGroup, this.iSelectedSmilParLevel,
                PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION);
    }

    static <T> List<T> getReverseNewArray(final List<T> list) {
        final List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }

    private SmilData_Par seekPrevAfterClassName(SmilData_Par prev, String metaClass)
    {
        if (prev == null || metaClass == null)
            return null;
        SmilData_Par ret = null;
        boolean bPageClassSelected = "page".equals(metaClass);
        int ind = -1; // allSmilData_ParItems.size();
        List<SmilData_Par> revArray = getReverseNewArray(allSmilData_ParItems);
        SmilData_Par founded = null;
        for (SmilData_Par par : revArray)
        {
            ind++;
            if (founded == null && par.getPar_id() == prev.getPar_id())
                founded = par;
            else
            if (founded != null && ((bPageClassSelected && par.getClassName() != null
                   && par.getClassName().contains(metaClass))
                    || metaClass.equals(par.getClassName()))) {
                ret = par;
                break;
            }
        }
        if (ret == null && founded != null)
            return founded;

        return ret;
    }

    private void seekPrevOrNextSentenceDatoToPlay(SmilData_Par sdpar, double timeSec, File mp3PlayFile,
                                                  PAR_LEVEL_DIRECTION level_direction)
    {
        if (sdpar == null)
            return;
        List<SmilData_Par_Audio> sentencies = sdpar.getListSmilDataParAudioItems();
        SmilData_Par_Audio founded = null, prev = null, next = null;

        for(SmilData_Par_Audio audio : sentencies)
        {
            if (audio == null)
                continue;
            if (founded != null) {
                next = audio;
                if (level_direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION)
                    break;
            }
            if (founded == null && audio.durationIsBetweenBeginAndEndTimes(timeSec)) {
                founded = audio;
                if (level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION)
                    break;
                continue;
            }
            prev = audio;
        }

        if (founded != null)
        {
            if (level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION) {
                if (prev != null) {
                    mp3PlayFile = getParFile(mp3PlayFile, prev.getAudio_src());
                    selectAudioLinkFileAndStartTime(prev, mp3PlayFile, -1);
                }
                else
                {
                    mp3PlayFile = getParFile(mp3PlayFile, founded.getAudio_src());
                    selectAudioLinkFileAndStartTime(founded, mp3PlayFile, +1);
                }
            }
            else {
                if (next != null) {
                    mp3PlayFile = getParFile(mp3PlayFile, next.getAudio_src());
                    selectAudioLinkFileAndStartTime(next, mp3PlayFile, +1);
                }
                else
                {
                    mp3PlayFile = getParFile(mp3PlayFile, founded.getAudio_src());
                    selectAudioLinkFileAndStartTime(founded, mp3PlayFile, +1);
                }
            }
        }
    }

    private double getRestOfEndTime(File newF, double restTime)
    {
        double ret = 0.0;
        if (newF != null && restTime > 0)
        {
            String strResource = newF.toURI().toString();
            Media buzzer = new Media(strResource);
            MediaPlayer mp = new MediaPlayer(buzzer);
            mp.setAutoPlay(false);
            return mp.getTotalDuration().toMillis();
        }
        return ret;
    }
    private void seekPrevOrNextMediaGroupDatoToPlay(SmilData_ParGroup mediagroup, int iSelectedMetaLevel,
                                                    PAR_LEVEL_DIRECTION level_direction)
    {
        //ddddd
        if (mediagroup == null || iSelectedMetaLevel == 0)
            return;
        if (iSelectedSmilParLevel == this.describeMetaGroups.iNumberOfTimeShift) {
            double playTime = mediaControlPane.getCurrentTime();
            double playDuration = mediaControlPane.getDuration();
            if (level_direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION) {
                if (playDuration < (playTime +this.currentTimeShift))
                {
                    NewSelectedFile newFile = getPossibleNextPlayfile();
                    if (newFile == null)
                        return;
                    File newF = newFile.newSelectedfile;
                    if (newF == null)
                        return;
                    double newBeginTime = playDuration - (playTime -this.currentTimeShift);
                    if (newBeginTime < 1)
                        mediaControlPane.playThisFile(newF, -1.0, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                    else {
                        mediaControlPane.playThisFile(newF, newBeginTime, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                    }
                }
                else
                    mediaControlPane.setForwardAfterMilliSecs(this.currentTimeShift);
            }
            else {
                if ((playTime - this.currentTimeShift) < 1 )
                {
                    double newBeginTime = (this.currentTimeShift - playTime);
                    if (newBeginTime < 1)
                        newBeginTime = (playTime -this.currentTimeShift);
                    File newF = getPossibleEarlierFile();
                    if (newF == null)
                        return;
                    newBeginTime = getRestOfEndTime(newF, newBeginTime);
                    if (newBeginTime < 1)
                        mediaControlPane.playThisFile(newF, -1.0, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                    else {
                        mediaControlPane.playThisFile(newF, newBeginTime, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                    }
                }
                else
                    mediaControlPane.setBackwardAfterMilliSecs(this.currentTimeShift);
            }
            return;
        }

        double current_time = mediaControlPane.getStopAndCurrentTime();
        double currSecs = current_time / 1000;
        File mp3PlayFile = mediaControlPane.getPlayFile();
        SmilData_Par sdpar = getCurrentLinkIndex(current_time, mp3PlayFile);
//        SmilData_Par parent = sdpar.get
        System.out.println("pressedButtonNextLink m_selectedFile = " +m_selectedFile.getAbsolutePath());
        System.out.println("pressedButtonNextLink current time = " +current_time /1000);
        System.out.println("pressedButtonNextLink sdpar = " +(sdpar != null ? sdpar.toString() : "null"));

        String metaClass = null;
        boolean bPageClassSelected = false;
        boolean bSentenceSelected = true;
        if (iSelectedMetaLevel == this.describeMetaGroups.iNumberOfPage) {
            metaClass = "page";
            bPageClassSelected = true;
        }
        else
        if (iSelectedMetaLevel == this.describeMetaGroups.iNumberOfSentence) {
            bSentenceSelected = true;
            seekPrevOrNextSentenceDatoToPlay(sdpar, currSecs, mp3PlayFile, level_direction);
            return;
        }
        else
            metaClass = this.describeMetaGroups.hmNumberMetaGroup.get("" +iSelectedMetaLevel);
        if (metaClass == null){
            metaClass = "page";
            bPageClassSelected = true;
        }

        String currentPlayedClass = sdpar.getClassName();

        SmilData_Par next = null, prev = null, founded = null;
        int iInd = -1;
        boolean bRigthClassItemFounded = false;

        for(SmilData_Par par : allSmilData_ParItems)
        {
            iInd++;
            if (par == null)
                continue;
            if (founded != null)
            {
                next = par;
               //if (mediagroup != null)
                 //   break;
                if (level_direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION)
                {
                    if ((bPageClassSelected && next.getClassName() != null &&
                            next.getClassName().contains(metaClass))
                            || metaClass.equals(next.getClassName()))
                    {
                        bRigthClassItemFounded = true;
                        break;
                    }
                }
                /*
                else
                {
                    if ((bPageClassSelected && metaClass.equals(prev.getClassName()))
                            || metaClass.equals(prev.getClassName()))
                    {
                        bRigthClassItemFounded = true;
                        break;
                    }
                }
                 */
            }
            // if (par.durationIsBetweenBeginAndEndTimes(currSecs))
            if (founded == null && par.getPar_id() == sdpar.getPar_id())
            {
                founded = par;
                if (level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION)
                    if ((bPageClassSelected && prev.getClassName() != null &&
                            prev.getClassName().contains(metaClass)))
                    {
                        bRigthClassItemFounded = true;
                        break;
                    }
                    else {
                        prev = seekPrevAfterClassName(founded, metaClass);
                        if (prev != null)
                            bRigthClassItemFounded = true;
                        break;
                    }
                continue;
            }
            prev = par;
        }

        if (founded == null)
            return;

        bPreventIncreaseArrayIndex.set(true);

        if (!bRigthClassItemFounded)
        {
            if (level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION) {
                selectMediaGroupLinkFileAndStartTime(founded, mp3PlayFile, -1);
            }
            else
            {
                selectMediaGroupLinkFileAndStartTime(founded, mp3PlayFile, +1);
            }
            bPreventIncreaseArrayIndex.set(false);
        }
        else
        {
            if (level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION) {
                mp3PlayFile = getParFile(mp3PlayFile, prev.getAudio_src());
                selectMediaGroupLinkFileAndStartTime(prev, mp3PlayFile, -1);
            }
            else {
                mp3PlayFile = getParFile(mp3PlayFile, next.getAudio_src());
                selectMediaGroupLinkFileAndStartTime(next, mp3PlayFile, +1);
            }
            bPreventIncreaseArrayIndex.set(false);
            return;
        }

        if (founded.getClassName() == null)
        {
            if (level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION) {
                mp3PlayFile = getParFile(mp3PlayFile, founded.getAudio_src());
                selectMediaGroupLinkFileAndStartTime(founded, mp3PlayFile, -1);
            }
            else {
                mp3PlayFile = getParFile(mp3PlayFile, founded.getAudio_src());
                selectMediaGroupLinkFileAndStartTime(founded, mp3PlayFile, +1);
            }
        }

        bPreventIncreaseArrayIndex.set(false);

        /*
        List<SmilData_Par> items = mediagroup.getListItems();
        if (items == null || items.size() == 0)
            return;
        SmilData_Par founded = null;
        SmilData_Par prev = null;
        SmilData_Par next = null;
        int iInd = -1;
        for(SmilData_Par par : items)
        {
            iInd++;
            if (par == null)
                continue;
            if (founded != null)
            {
                next = par;
                break;
            }
            if (par.durationIsBetweenBeginAndEndTimes(currSecs))
            {
                founded = par;
                continue;
            }
            prev = par;
        }

        if (level_direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION)
        {
            if (founded != null && founded != null && mp3PlayFile != null) {
                mp3PlayFile = getParFile(mp3PlayFile, founded.getAudio_src());
                selectLinkFileAndStartTime(founded, mp3PlayFile, -1);
            }
        }
        else
        {
; //ffff
            if (founded != null && founded != null && mp3PlayFile != null) {
                mp3PlayFile = getParFile(mp3PlayFile, founded.getAudio_src());
                selectLinkFileAndStartTime(founded, mp3PlayFile, +1);
            }
        }
         */
    }

    private void selectAudioLinkFileAndStartTime(SmilData_Par_Audio audio, File mp3PlayFile,
                                                      int addOrSubtract)
    {
        //dddd
        File m_selectedFile = mp3PlayFile;
        File new_selectedFile = mp3PlayFile;
        int [] arrAudioFiles = null;
        int newInd = audio.getInd();
        SmilData sd = hashMapHRefs.get(new_selectedFile.getAbsolutePath());
        if (sd != null)
        {
            double beginClip = audio.getClip_beginSeconds();
            int indArr = getIndArrOfListMp3Files(m_selectedFile);
            if (new_selectedFile == null)
                new_selectedFile = m_selectedFile;
            mediaControlPane.setIndArrDirFiles(indArr);
            mediaControlPane.playThisFile(new_selectedFile, beginClip, indArr, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
            int indSelectedFile = indArr; // mediaControl.possibleNextTrack();
            if (indSelectedFile > -1 && bWebViewLoaded) {
                scrollIntoWebView(indSelectedFile);
            }
        }
    }

    private void selectMediaGroupLinkFileAndStartTime(SmilData_Par sdpar, File mp3PlayFile,
                                                      int addOrSubtract)
    {
        //dddd
        File m_selectedFile = mp3PlayFile;
        File new_selectedFile = mp3PlayFile;
        int [] arrAudioFiles = null;
        int newInd = sdpar.getInd();
        SmilData sd = hashMapHRefs.get(new_selectedFile.getAbsolutePath());
        if (sd != null) {
            SmilData_Par par = null;
            List<SmilData_Par> pars = null;
            pars = sd.getSmilDataParsItems();
            if (pars != null) {
                if (addOrSubtract == -1)
                    par = pars.get(newInd);
                else
                    par = pars.get(newInd);
            }

            if (par != null) {
                double beginClip = par.getClip_beginSeconds();
                int indArr = getIndArrOfListMp3Files(m_selectedFile);
                if (new_selectedFile == null)
                    new_selectedFile = m_selectedFile;
                mediaControlPane.setIndArrDirFiles(indArr);
                mediaControlPane.playThisFile(new_selectedFile, beginClip, indArr, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                int indSelectedFile = indArr; // mediaControl.possibleNextTrack();
                if (indSelectedFile > -1 && bWebViewLoaded) {
                    scrollIntoWebView(indSelectedFile);
                }
            }
        }
    }

    private File getParFile(File currentMp3PlayFile, String nmp2filename)
    {
        File ret = null;
        if (currentMp3PlayFile != null && currentMp3PlayFile.exists() && nmp2filename != null
           || nmp2filename.trim().length()>0)
        {
            if (currentMp3PlayFile.getParentFile() != null)
            {
                ret = new File(currentMp3PlayFile.getParentFile(), nmp2filename);
            }
        }
        return ret;
    }

    private void changeParDirectionLevel(PAR_LEVEL_DIRECTION p_par_level_direction)
    {
        if (iMaxSmilParLevel != 0)
        {
            if (iSelectedSmilParLevel == 0)
                iSelectedSmilParLevel = 1;

            if (p_par_level_direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION) {
                if (iSelectedSmilParLevel < iMaxMetaLevel)
                    iSelectedSmilParLevel++;
                else {
                    // par_level_direction = PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION;
                    if (iSelectedSmilParLevel == iMaxMetaLevel)
                        iSelectedSmilParLevel = 1;
                //    else
                  //      changeParDirectionLevel(PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION);
                }
            }
            else
            if (p_par_level_direction == PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION) {
                if (iSelectedSmilParLevel > 1)
                    iSelectedSmilParLevel--;
                else {
                    // par_level_direction = PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION;
                    if (iSelectedSmilParLevel == 1)
                        iSelectedSmilParLevel = iMaxMetaLevel;
                    // else
                       // changeParDirectionLevel(PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION);
                }
            }
            chengeLabelLevel(iSelectedSmilParLevel, p_par_level_direction);
        }
    }

    private boolean isInGroupParLevel(int iSelectedSmilParLevel, String search)
    {
        boolean ret = false;
        if (iSelectedSmilParLevel == describeMetaGroups.iNumberOfPage)
            return true;

        if (search != null && search.trim().length()!= 0) {
            String value = null;
            int ind = 1;
            for (String key : this.describeMetaGroups.hmNumberMetaGroup.keySet()) {
                value = this.describeMetaGroups.hmNumberMetaGroup.get(key);
                if (ind == iSelectedSmilParLevel && value != null && search.equals(value))
                    return true;
            }
        }
        return ret;
    }

    private void chengeLabelLevel(int iSelectedSmilParLevel, PAR_LEVEL_DIRECTION p_par_level_direction)
    {
        SmilData_ParGroup earlierGroup = currentMetaGroup;
        String textToSpeek = "";
        if (isInGroupParLevel(iSelectedSmilParLevel, "page")) {
            currentMetaGroup = metaGroups.get(cnstAllPages);
            textToSpeek = "Page";
        }
        else
        if (iSelectedSmilParLevel == this.describeMetaGroups.iNumberOfSentence) {
            currentMetaGroup = metaGroups.get(cnstSentence);
            textToSpeek = cnstSentence;
        }
        else
        if (iSelectedSmilParLevel == this.describeMetaGroups.iNumberOfTimeShift) {
            currentMetaGroup = metaGroups.get("timehift");
            textToSpeek = "Time Shift";
        }
        else
        if (iSelectedSmilParLevel == 1 || iSelectedSmilParLevel == 2
                || iSelectedSmilParLevel == 3 || iSelectedSmilParLevel == 4) {
            currentMetaGroup = metaGroups.get("" + iSelectedSmilParLevel);
            if (currentMetaGroup == null)
            {
                currentMetaGroup = metaGroups.get(cnstAllPages);
                textToSpeek = "Page";
            }
            else
                textToSpeek = "" +iSelectedSmilParLevel +"   "
                        +describeMetaGroups.hmNumberMetaGroup.get("" +iSelectedSmilParLevel);
        }

        this.par_level_direction = p_par_level_direction;
        if (currentMetaGroup == null)
            currentMetaGroup = earlierGroup;
        final String strSpeak = " \n\n   " +textToSpeek +" \n\n   ";
        Platform.runLater(new Runnable() {
            public void run() {
                labelLevel.setText(getLabelText(iSelectedSmilParLevel));
            }
        });
        if (currentMetaGroup != null /*&& earlierGroup != currentMetaGroup */)
        {
            possibleSpeekText(strSpeak);
        }
    }

    private String getLabelText(int iSelectedSmilParLevel)
    {
        String ret = "Link level set into " +iSelectedSmilParLevel;
        return ret;
    }

    public void setPlayAfterBookMark(BookMark bookMark)
    {
        if (bookMark != null)
        {
            if (mediaControlPane.isPlaying())
                mediaControlPane.pause();
            String playFilePath = bookMark.getPlayFilePath();
            filePlay = new File(playFilePath);
            if (bookMark.getType().equals(BookMarkCollection.BOOKMARK_TYPE.DAISY.toString()))
                filePlay = new File(bookMark.getDaisyBookIndexPath());
            if (playFilePath != null) {
                if (!filePlay.exists())
                    return;
                Platform.runLater(new Runnable() {
                    public void run() {
                        bookMarkStage.close();
                        callPressedButtonNewBookMark(true);
                        loadFile(filePlay, bookMark);
                        updateSplitPaneDividerPosition();
                    }
                });
            }
        }
    }

    @FXML
    protected void pressedButtonBookMarks()
    {
        System.out.println("pressedButtonBookMarks");
        try {


            boolean isPlaying = mediaControlPane.isPlaying();
            if (isPlaying)
                mediaControlPane.pause();

            boolean bMultiLanguage = PlayerController.bUseMultiLang;
            FXMLLoader fxmlLoader = new FXMLLoader(
                    BookMarkController.class.getResource("javafxplayerbookmark.fxml"));
            if (bMultiLanguage) { // there are some differences between none i18 fxml file and i18 fxml file!
           //     locale = PlayerController.getLocale();
                ResourceBundle bundle = ResourceBundle.getBundle("com/metait/javafxplayer/bookmark/lang", locale);
                fxmlLoader = new FXMLLoader(BookMarkController.class.getResource("javafxplayerbookmark18.fxml"), bundle);
            }

            dialogController = new BookMarkController();
            dialogController.setPlayerController(this);
            dialogController.setBookMarks(bookMarkCollections);
            fxmlLoader.setController(dialogController);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, 800, 600);
            Stage stage = new Stage();
            // stage.setIconified(true);
            // stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            buttonBookMarks.setDisable(true);
            // m_primaryStage.setScene(scene);
            stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    // bookMarkCollections = dialogController.getBookMarks();
                    System.out.println("handle(WindowEvent we");
                    if (dialogController != null)
                        bookMarkCollections = dialogController.getBookMarkCollections();
                    boolean bValue = dialogController.getGotoBMarkPressed();
                    if (!bValue && !mediaControlPane.isPlaying())
                        mediaControlPane.play10sMiinus();
                }
            });
            bookMarkStage = stage;
            Platform.runLater(new Runnable() {
                public void run() {
                    stage.showAndWait();
                    buttonBookMarks.setDisable(false);
                }
            });
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private boolean isTheSameBookMarkCollection(String newBookCollectionName, BookMarkCollection bc, BookMark newBookMark)
    {
        boolean ret = false;
        if (bc != null && newBookMark != null)
        {
            if ((newBookCollectionName != null && bc.getName() != null && bc.getName().equals(newBookCollectionName))
                || (bc.getName() != null && newBookMark.getName().startsWith(bc.getName())))
                return true;
        }
        return ret;
    }
    private BookMarkCollection getBookMarkCollectionOf(String newBookCollectionName, BookMark newBookMark)
    {
        BookMarkCollection ret = null;
        BookMarkCollection bc;
        for(Object obj : this.bookMarkCollections)
        {
            bc = (BookMarkCollection)obj;
            if (isTheSameBookMarkCollection(newBookCollectionName, bc, newBookMark)) {
                ret = bc;
                break;
            }
        }
        return ret;
    }

    private BookMarkCollection.BOOKMARK_TYPE getBookMarkType(BookMark newBookMark)
    {
        BookMarkCollection.BOOKMARK_TYPE ret = null;
        if (newBookMark == null)
            return null;
        String strPlayFile = newBookMark.getPlayFilePath();
        String strType = newBookMark.getType();
        if (strPlayFile != null) {
            File fPlay = new File(strPlayFile);
            if (!fPlay.exists())
                return null;
            if ((fPlay.getName().endsWith(".html")
                    || fPlay.getName().endsWith(".html")
                    || fPlay.getName().endsWith(".htm"))
              || (newBookMark.getType() != null
                    && newBookMark.getType().equals(BookMarkCollection.BOOKMARK_TYPE.DAISY.toString()))){
                ret = BookMarkCollection.BOOKMARK_TYPE.DAISY;
                isDaisyNcc = true;
            } else if (fPlay.isDirectory()) {
                ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR;
                isDaisyNcc = false;
            } else {
                if (strType != null && strType.trim().length()!= 0) {
                    if (strType.equals("FILE"))
                        ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_FILE;
                    else if (strType.equals("DIR"))
                        ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR;
                    else if (strType.equals("DAISY"))
                        ret = BookMarkCollection.BOOKMARK_TYPE.DAISY;
                }
                else
                {
                    isDaisyNcc = false;
                    if (strType == null || strType.trim().length() == 0) {
                        if ((fPlay.getName().endsWith(".html")
                                || fPlay.getName().endsWith(".html")
                                || fPlay.getName().endsWith(".htm")))
                        {
                            ret = BookMarkCollection.BOOKMARK_TYPE.DAISY;
                            isDaisyNcc = true;
                        }
                        else
                        {
                            if (fPlay.isDirectory())
                                ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR;
                            else
                                ret = BookMarkCollection.BOOKMARK_TYPE.AUDIO_FILE;
                        }
                    }
                }

            }
        }
        return ret;
    }

    private BookMark [] getCorrectedOldBookMarks(BookMark [] oldBookMarks)
    {
        BookMark [] ret = oldBookMarks;
        int iRemoveAutomaticOldBmark = -1;
        int len = oldBookMarks.length;
        for (int i = 0; i < len; i++ ) {
            if (oldBookMarks[i].getName().contains(cnstStringAutomatic)) {
                iRemoveAutomaticOldBmark = i;
                break;
            }
        }
        if (iRemoveAutomaticOldBmark == -1)
            return ret;
        ret = new BookMark[oldBookMarks.length -1];
        int j = 0;
        for (int i = 0; i < len; i++ ) {
            if (iRemoveAutomaticOldBmark != i) {
                ret[j++] = oldBookMarks[i];
            }
        }
        iRemoveAutomaticOldBmark = -1;
        len = ret.length;
        for (int i = 0; i < len; i++ ) {
            if (ret[i].getName().contains(cnstStringAutomatic)) {
                iRemoveAutomaticOldBmark = i;
                break;
            }
        }
        if (iRemoveAutomaticOldBmark > -1)
            return getCorrectedOldBookMarks(ret);
        return ret;
    }

    private BookMark [] getAutomaticBookMarkAndOldBookMarks(BookMark newBookMark, BookMark [] oldBookMarks)
    {
        BookMark [] ret = oldBookMarks;
        if (newBookMark != null)
        {
            newBookMark.setName("0 " +cnstStringAutomatic);
            if (newBookMark.getType().equals("DAISY"))
                newBookMark.setName("0 " +cnstStringAutomatic);
            if (ret == null  || ret.length == 0) {
                ret = new BookMark[1];
                ret[0] = newBookMark;
            }
            else {
                int len = oldBookMarks.length;
                int iRemoveAutomaticOldBmark = -1;
                int iAddNewBMark = 1;
                BookMark[] correctedOldBMarks = getCorrectedOldBookMarks(oldBookMarks);
                //if (correctedOldBMarks.length != oldBookMarks.length)
                //  iAddNewBMark = 0;

                ret = new BookMark[correctedOldBMarks.length + iAddNewBMark];
                int j = 0;
                ret[j++] = newBookMark;
                len = correctedOldBMarks.length;
                if (len == 0) {
                    return ret;
                }

                for (int i = 0; i < len; i++) {
                    ret[j++] = correctedOldBMarks[i];
                }
            }
        }
        return ret;
    }

    private String getDaisyBookMarkIndexValue(String strName)
    {
        if (strName == null || strName.trim().length() == 0)
            return "";
        int ind = strName.indexOf(' ');
        if (ind > -1)
        {
            return strName.substring(0, ind).trim();
        }
        return strName;
    }

    private void callPressedButtonNewBookMark(boolean bCreateLastPlayedBookMark)
    {
        dCurrentPosition = mediaControlPane.getCurrentTime();
        File playFile = mediaControlPane.getPlayFile();
        BookMark newBookMark = new BookMark();
        if (user_selected_to_open != null)
        {
            if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY)
                newBookMark.setType(BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR.toString());
            else
            if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_FILE)
                newBookMark.setType(BookMarkCollection.BOOKMARK_TYPE.AUDIO_FILE.toString());
            else
            if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML)
                newBookMark.setType(BookMarkCollection.BOOKMARK_TYPE.DAISY.toString());
        }

        if (bookMarkCollections == null)
            bookMarkCollections = FXCollections.observableArrayList();
        int max = bookMarkCollections.size();
        String newBookCollectionName = "" +(selectedDirectory != null ? selectedDirectory.getAbsolutePath() :
                m_selectedFile.getAbsolutePath());

        if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY)
        {
            newBookCollectionName = playFile.getParentFile().getAbsolutePath();
        }
        // newBookCollectionName = newBookCollectionName +" " +max +1;
        if (selectedDirectory != null)
            newBookMark.setDirPath(selectedDirectory.getAbsolutePath());
        else
        if (dirChooser != null)
            newBookMark.setDirPath(dirChooser.getAbsolutePath());
        newBookMark.setPlayFilePath(m_selectedFile.getAbsolutePath());
        newBookMark.setPosition(dCurrentPosition / 1000);
        if (metadataSmilDataPar != null && metadataSmilDataPar.getStrTitle() != null) {
            newBookCollectionName = metadataSmilDataPar.getStrTitle();
            SmilData_Par data_par =  getCurrentLinkIndex(dCurrentPosition, m_selectedFile);
            if (data_par != null && data_par.getText() != null) {;
                newBookMark.setName("" +data_par.getText());
            }
            else
                newBookMark.setName(newBookCollectionName);
            newBookMark.setType(BookMarkCollection.BOOKMARK_TYPE.DAISY.toString());
            if (daisyIndexFile != null)
                newBookMark.setDaisyBookIndexPath(daisyIndexFile.getAbsolutePath());
        }
        else
            newBookMark.setName("" +max +1);

        BookMarkCollection.BOOKMARK_TYPE bmType = getBookMarkType(newBookMark);
        if (iIndArrDirFiles > -1)
            newBookMark.setIndArrDirFiles("" +iIndArrDirFiles);
        BookMarkCollection bc = getBookMarkCollectionOf(newBookCollectionName, newBookMark);
        if (bc == null) {
            //    BookMarkCollection.BOOKMARK_TYPE bmtype = getBookMarkType(newBookMark);
            bc = new BookMarkCollection(bmType);
            bc.setDirPath(newBookMark.getDirPath());
            bc.setPlayFilePath(newBookMark.getPlayFilePath());
            bc.setName(newBookCollectionName);
            if (bmType != BookMarkCollection.BOOKMARK_TYPE.DAISY)
                newBookMark.setName("1");
            if (bCreateLastPlayedBookMark)
                bc.setBookMarks(getAutomaticBookMarkAndOldBookMarks(newBookMark, bc.getBookMarks()));
            else
                bc.addBookMark(newBookMark);
            this.bookMarkCollections.add(bc);
        }
        else {
            if (bmType != BookMarkCollection.BOOKMARK_TYPE.DAISY) {
                if (bc.getBookMarks().length == 0)
                    max = 0;
                else {
                    max = bc.getBookMarks().length;
                    int localMax = 0;
                    int bmMax = 0;
                    for(BookMark bm : bc.getBookMarks())
                    {
                        bmMax = Integer.parseInt(getDaisyBookMarkIndexValue(bm.getName()));
                        if (bmMax > localMax)
                        {
                            localMax = bmMax;
                        }
                    }
                    if (localMax > 0)
                        max = localMax;
                }
                newBookMark.setName("" +(max +1));
            }
            if (bCreateLastPlayedBookMark)
                bc.setBookMarks(getAutomaticBookMarkAndOldBookMarks(newBookMark, bc.getBookMarks()));
            else
                bc.addBookMark(newBookMark);
        }

        // dddddd newBookMark.setDaisybookindexpath();
        //  bookMarkCollections = mediaControlPane.getBookMarkList();
        /*
        savePlayPositionAndBookMarks(iIndArrDirFiles, selectedDirectory, playFile,
                dCurrentPosition, bookMarkCollections,
                ( radioButtonNoTTS.isSelected() ? cnstNoTTS : (
                        radioButtonFreeTts.isSelected() ? cnstFreeTTS : cnstEspeakTTS
                )));
        */
    }

    @FXML
    protected void pressedButtonNewBookMark()
    {
        System.out.println("pressedButtonNewBookMark");
        boolean bCreateLastPlayedBookMark = false;
        callPressedButtonNewBookMark(bCreateLastPlayedBookMark);
    }

    private void readLevelReservedWordsAndLevels()
    {
        File propsLevels = new File("smilparlevels.properties");
        if (propsLevels.exists())
        {
            Properties props = new  Properties();
            try {
                FileInputStream stream = new FileInputStream(propsLevels);
                props.load(stream);
                Enumeration<?> keys = props.propertyNames();
                String value, key;
                while (keys.hasMoreElements())
                {
                        key = (String) keys.nextElement();
                        if (key == null || key.trim().length()==0)
                            continue;
                        value = props.getProperty(key);
                        if (value != null && value.trim().length()>0)
                        {
                            hashMapLevelWords.put(key, Integer.parseInt(value));
                        }
                }
            }catch (Exception e){
                labelMsg.setText("File had try to read: " +propsLevels.getAbsolutePath()) ;
            }
        }
        else
            labelMsg.setText("File is missing: " +propsLevels.getAbsolutePath()) ;
        setHashMapLevelHeaderWords();
    }

    private void setHashMapLevelHeaderWords()
    {
        hashMapLevelHeaderWords.put("H1", new Integer(1));
        hashMapLevelHeaderWords.put("H2", new Integer(2));
        hashMapLevelHeaderWords.put("H3", new Integer(3));
        hashMapLevelHeaderWords.put("H4", new Integer(4));
        hashMapLevelHeaderWords.put("H5", new Integer(5));
        hashMapLevelHeaderWords.put("H6", new Integer(6));
    }

    private void initializeHelp()
    {
        try {
            webView.setAccessibleHelp("Contains daisy index file content as html");
            webView.setAccessibleRole(AccessibleRole.TEXT);
            SmilData.m_setter = this;
            readLevelReservedWordsAndLevels();

            buttonPrevLevelLink.setDisable(true);
            buttonNextLevelLink.setDisable(true);

            boolean bMultiLanguage = PlayerController.bUseMultiLang;
            FXMLLoader fxmlLoader = new FXMLLoader(HelpController.class.getResource("javafxplayerhelp.fxml"));
         //   helpController = new HelpController();
            helpController.setLocale(this.locale);
            fxmlLoader.setController(helpController);
            if (bMultiLanguage) { // there are some differences between none i18 fxml file and i18 fxml file!
             //   locale = PlayerConfig.getLanguageLocale(); // The dff are size of buttons etc.
            //    PlayerController.locale = locale;
                ResourceBundle bundle = ResourceBundle.getBundle("com/metait/javafxplayer/help/lang", locale);
                fxmlLoader = new FXMLLoader(HelpController.class.getResource("javafxplayerhelp18.fxml"), bundle);
                helpController.setLocale(this.locale);
                fxmlLoader.setController(helpController);
            }
            labelLevel.setFocusTraversable(true);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, screen_wight, screen_height);
            stageHelp = new Stage();
            helpController.setHelpStage(stageHelp);
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    helpController.handleKeyEvent(event);
                }
            });
            // stageHelp.setIconified(true);
            stageHelp.initModality(Modality.WINDOW_MODAL);
            stageHelp.setScene(scene);

        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @FXML
    protected void pressedButtonHelp()
    {
        System.out.println("pressedButtonHelp");
            /*
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelpController.class.getResource("javafxplayerhelp.fxml"));
            HelpController dialogController = new HelpController();
            fxmlLoader.setController(dialogController);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, 500, 400);
            Stage stage = new Stage();
            // stage.setIconified(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            buttonHelp.setDisable(true);
            // m_primaryStage.setScene(scene);
            stage.showAndWait();
            buttonHelp.setDisable(false);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
             */

        boolean isPlaying = mediaControlPane.isPlaying();
            if (isPlaying)
                mediaControlPane.pause();
            helpController.setLocale(locale);
            helpController.scrollInto("main_header");
            openHelpCtrl();
        if (isPlaying)
            mediaControlPane.pauseOrPlay();
    }

    private void openHelpCtrl()
    {
        // buttonHelp.setDisable(true);
        //stageHelp.showAndWait();
        Platform.runLater(new Runnable() {
            public void run() {
                if (stageHelp.isShowing()) {
                    // stageHelp.close();
                    stageHelp.requestFocus();
                    try {
                        Thread.sleep(500);
                        stageHelp.show();
                    }catch (Exception e){
                    }
                }
                else
                    stageHelp.show();
            }
        });
        //  buttonHelp.setDisable(false);
    }

    private List<BookMarkCollection> getBookMarks()
    {
        List<BookMarkCollection> ret = null;
        if (java_user_home == null)
            return null;
        File appUserDir = new File(java_user_home +File.separator +cnstUserDirOfApp);
        if (!appUserDir.exists())
            if (!appUserDir.mkdir())
                return null;
        File appFile = new File(appUserDir.getAbsolutePath() +File.separator +cnstUserJsonFileOfApp);
        if (!appFile.exists())
            return null;

        List<String> listContent = null;
        String strJson = null;
        PlayerConfig config = null;
        try {
            try {
                listContent = Files.readAllLines(Paths.get(appFile.getAbsolutePath()));
            }catch (IOException ioe2){
                if (ioe2.getMessage().contains("Input length = 1"))
                    listContent = Files.readAllLines(Paths.get(appFile.getAbsolutePath()), StandardCharsets.ISO_8859_1);
                else
                    throw ioe2;
            }

            if (listContent != null && listContent.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (String line : listContent)
                    sb.append(line);
                strJson = sb.toString();
            }
            if (strJson == null || strJson.trim().length()==0)
                return null;

            Gson gson = new Gson();
            config = gson.fromJson(strJson, PlayerConfig.class);
            if (config != null)
            {
                autoBookMark = config.autoBookmark;
                if (autoBookMark != null)
                {
                    String strFile = autoBookMark.getPlayFilePath();
                    File fFile = new File(strFile);
                    File fParent = fFile.getParentFile();
                    BookMarkCollection.BOOKMARK_TYPE type = getBookMarkType(autoBookMark);
                    if (type == BookMarkCollection.BOOKMARK_TYPE.DAISY) {
                        user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML;
                        if (fParent.isDirectory() && !fFile.getName().endsWith(".html"))
                        {
                            File fNccFile = new File(fParent.getAbsolutePath()
                                    +File.separatorChar +"ncc.html");
                            if (fNccFile.exists())
                                m_selectedFile = fNccFile;
                        }
                    }
                    else
                    if (type == BookMarkCollection.BOOKMARK_TYPE.AUDIO_DIR) {
                        user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY;
                    }
                    else
                    if (type == BookMarkCollection.BOOKMARK_TYPE.AUDIO_FILE) {
                        user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_FILE;
                    }

                    if (fFile != null && fFile.isDirectory()) {
                        directoryChooser.setInitialDirectory(fFile);
                        fileChooser.setInitialDirectory(fFile);
                    }
                    else
                        if (fFile != null)
                        {
                            if (fParent != null && fParent.isDirectory()) {
                                directoryChooser.setInitialDirectory(fParent);
                                fileChooser.setInitialDirectory(fParent);
                            }
                        }
                }

                BookMarkCollection [] arrBms = config.userBookmarks;
                if (arrBms == null || arrBms.length == 0)
                    return null;
                ret = new ArrayList<BookMarkCollection>(arrBms.length);
                for(BookMarkCollection bmc : arrBms)
                    ret.add(bmc);
                Locale tmp_locale = config.userSelectedLocale;
                if (tmp_locale != null) {
                    locale = tmp_locale;
                    PlayerController.locale = tmp_locale;
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            labelMsg.setText("Reading error in file: " +appFile.getAbsolutePath() +" " +ioe.getMessage());
        }
        return ret;
    }

    @FXML
    protected void pressedExitApp()
    {
        primaryStage.close();
    }

    @FXML
    protected void pressedAboutApp()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About JavaFx Player");
        alert.setHeaderText("About JavaFx Player v 1.0");
        String s ="This is a javafx player for listening music files, directories or daisy 2 book at the time.";
        alert.setContentText(s);
        // ObservableList<ButtonType> buttons = alert.getDialogPane().getButtonTypes();
        alert.show();
    }

    public void setResourceBundle(ResourceBundle bundle)
    {
        m_bundle = bundle;
    }

    private String getResourceBungleString(String def_strToolTip, String key)
    {
        String ret = def_strToolTip;
        String tmp_strToolTip;
        if (m_bundle != null && !locale.getCountry().equals("en")) {
            try {
                tmp_strToolTip = m_bundle.getString(key);
                if (tmp_strToolTip != null && tmp_strToolTip.trim().length() > 0)
                    ret = tmp_strToolTip;
            }catch (Exception e){
                return ret;
            }
        }
        return ret;
    }

    @FXML
    public void initialize() {

        comboBoxLanguage.getItems().add("English");
        comboBoxLanguage.getItems().add("Suomi");
        if (locale.getCountry().equals("FI"))
            comboBoxLanguage.getSelectionModel().select(1);
        else
            comboBoxLanguage.getSelectionModel().select(0);
        /*
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F1), menuItemOpenDir::fire);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F1), menuItemOpenFile::fire);
        //setOnKeyPresse
        */
      //  menuItemOpenFile.
        // buttonAudioDir.setId("open_audio_dir");
        // buttonAudioFile.setId("open_audio_file_or_daisy_book_index_html");
      //  menuItemOpenDir.setId("open_audio_dir");
      //  menuItemOpenFile.setId("open_audio_file_or_daisy_book_index_html");
        buttonPlay.setId("from_start");
        buttonPreviousTrack.setId("from_prev_file");
        buttonNextTrack.setId("from_next_file");
        buttonPrevLink.setId("from_prev_link");
        buttonNextLink.setId("from_next_link");
        buttonHelp.setId("from_help");
        radioButtonNoTTS.setId("radiobt_no_tts");
        radioButtonFreeTts.setId("radiobt_free_tts");
        radioButtonNoTTS.setId("radiobt_no_tts");
        radioButtonESpeak.setId("radiobt_epeak_tts");
        buttonBookMarks.setId("buttonBookMarks");
        labelMsg.setId("labelMsg");

        strVolume_UI = getResourceBungleString(strVolume_UI, "mediapane_volume");
        strTime_UI = getResourceBungleString(strTime_UI, "mediapane_time");
        str_mediapane_1 = getResourceBungleString(str_mediapane_1, "mediapane_1");
        str_mediapane_2 = getResourceBungleString(str_mediapane_2, "mediapane_2");
        str_mediapane_3 = getResourceBungleString(str_mediapane_3, "mediapane_3");
        str_mediapane_4 = getResourceBungleString(str_mediapane_4, "mediapane_4");
        str_mediapane_5 = getResourceBungleString(str_mediapane_5, "mediapane_5");
        str_mediapane_6 = getResourceBungleString(str_mediapane_6, "mediapane_6");
        str_mediapane_7 = getResourceBungleString(str_mediapane_7, "mediapane_7");
        str_mediapane_8 = getResourceBungleString(str_mediapane_8, "mediapane_8");
        String strToolTip = "Previous link inside of selected level links";
        strToolTip = getResourceBungleString(strToolTip, "tooltip_1");
        Tooltip tooltip = new Tooltip(strToolTip);
        tooltip.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow; -fx-font-size: 14");
        buttonPrevLevelLink.setTooltip(tooltip);
        buttonPrevLevelLink.setAccessibleHelp(tooltip.getText());

        strToolTip = "Next link inside of selected level links";
        strToolTip = getResourceBungleString(strToolTip, "tooltip_2");
        tooltip = new Tooltip(strToolTip);
        tooltip.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow; -fx-font-size: 14");
        buttonNextLevelLink.setTooltip(tooltip);
        buttonNextLevelLink.setAccessibleHelp(tooltip.getText());

        strToolTip = "Previous link in daisy index page";
        strToolTip = getResourceBungleString(strToolTip, "tooltip_3");
        tooltip = new Tooltip(strToolTip);
        tooltip.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow; -fx-font-size: 14");
        buttonPrevLink.setTooltip(tooltip);

        buttonPlay.defaultButtonProperty().bind(buttonPlay.focusedProperty());
        buttonHelp.defaultButtonProperty().bind(buttonHelp.focusedProperty());
        // buttonAudioDir.defaultButtonProperty().bind(buttonAudioDir.focusedProperty());
        buttonNextLink.defaultButtonProperty().bind(buttonNextLink.focusedProperty());
        buttonPrevLink.defaultButtonProperty().bind(buttonPrevLink.focusedProperty());
        buttonLowerLevel.defaultButtonProperty().bind(buttonLowerLevel.focusedProperty());
        buttonUpperLevel.defaultButtonProperty().bind(buttonUpperLevel.focusedProperty());
        // buttonAudioFile.defaultButtonProperty().bind(buttonAudioFile.focusedProperty());
        buttonNextTrack.defaultButtonProperty().bind(buttonNextTrack.focusedProperty());
        buttonPreviousTrack.defaultButtonProperty().bind(buttonPreviousTrack.focusedProperty());
        // .defaultButtonProperty().bind(buttonHelp.focusedProperty());
        buttonPrevLevelLink.defaultButtonProperty().bind(buttonPrevLevelLink.focusedProperty());
        buttonNextLevelLink.defaultButtonProperty().bind(buttonNextLevelLink.focusedProperty());
        buttonNewBookMark.defaultButtonProperty().bind(buttonNewBookMark.focusedProperty());
        buttonBookMarks.defaultButtonProperty().bind(buttonBookMarks.focusedProperty());


        radioButtonNoTTS.setFocusTraversable(true);
        radioButtonESpeak.setFocusTraversable(true);
        radioButtonFreeTts.setFocusTraversable(true);
        radioButtonNoTTS.setSelected(true);

        initializeHelp();

        bookMarkCollections = getBookMarks();
        labelMsg.setAccessibleRole(AccessibleRole.TEXT);
        labelMsg.setFocusTraversable(true);

        comboBoxLanguage.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                changeUILanguage(t1);
            }
        });

        radioButtonESpeak.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                setRemoveWarningLabelMsg(newValue, radioButtonFreeTts);
            }
        });

        radioButtonFreeTts.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                setRemoveWarningLabelMsg(newValue, radioButtonESpeak);
            }
        });

        labelSeleectedFile.setFocusTraversable(true);
     //   labelSeleectedFile.setAccessibleHelp("selected open file or directory");
        labelSeleectedFile.setAccessibleRole(AccessibleRole.TEXT);

        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.getLoadWorker().stateProperty().addListener(new HyperLinkRedirectListener(webView, this));

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
              //  System.out.println("Width: " + newSceneWidth);
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
            //    System.out.println("Height: " + newSceneHeight);
            }
        });

        splitPane.setDividerPositions(0.2);

        buttonPrevLink.setDisable(true);
        buttonNextLink.setDisable(true);
        buttonLowerLevel.setDisable(true);
        buttonUpperLevel.setDisable(true);

        directoryChooser.setTitle("Open audit directory to play");
        fileChooser.setTitle("Open audit file to play");
        directoryChooser.setInitialDirectory(new File("."));
        fileChooser.setInitialDirectory(new File("."));
        // fileChooser.selectedExtensionFilterProperty()
        FileChooser.ExtensionFilter [] extFilters = getFileExtenstionFilters();
        for(FileChooser.ExtensionFilter ef : extFilters)
            fileChooser.getExtensionFilters().add(ef);

        buttonPreviousTrack.setDisable(true);
        buttonPlay.setDisable(true);
        buttonNextTrack.setDisable(true);
        // mp = new MediaPlayer();
        mediaControlPane = new MediaControlPane(this, strVolume_UI, strTime_UI, str_mediapane_1,
                str_mediapane_2, str_mediapane_3, str_mediapane_4, str_mediapane_5, str_mediapane_6,
                str_mediapane_7, str_mediapane_8);
        // mediaControl.setExtensionFilter(audiofilter);
        mediaScrollPane.setContent(mediaControlPane);

        if (autoBookMark != null)
        {
            String strAudioFile = autoBookMark.getPlayFilePath();
            if (m_selectedFile == null && user_selected_to_open != USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML)
                m_selectedFile = new File(strAudioFile);
         //   if (strAudioFile != null)
           //     m_selectedFile = new File(strAudioFile);
            if (m_selectedFile != null && m_selectedFile.exists()) {
                labelSeleectedFile.setText(m_selectedFile.getAbsolutePath());
                String strDir = autoBookMark.getDirPath();
                File dirFile = null;
                if (strDir != null && strDir.trim().length()>0 ) {
                    dirFile = new File(strDir);
                    dirChooser = dirFile;
                }
                else
                {
                    File fParent = m_selectedFile.getParentFile();
                    if (fParent.isDirectory()) {
                        dirFile = fParent;
                        dirChooser = dirFile;
                    }
                }
                if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY &&
                        dirFile != null && dirFile.exists() && dirFile.isDirectory()) {
                    labelSeleectedFile.setText(dirFile.getAbsolutePath());
                    selectedDirectory = dirFile;
                    arrAudioFiles = dirFile.listFiles(audiofilter);
                    fileChooser.setInitialDirectory(dirChooser);
                    if (dirChooser != null)
                        directoryChooser.setInitialDirectory(dirChooser);
                    autoBookMarkPressedPlayButton(autoBookMark);
                }
                else {
                    String htmlDirPath = autoBookMark.getDaisyBookIndexPath();
                    if (htmlDirPath != null && htmlDirPath.trim().length() > 0) {
                        labelSeleectedFile.setText(htmlDirPath);
                        dirFile = new File(htmlDirPath).getParentFile();
                        selectedDirectory = null;
                        dirChooser = dirFile;
                        arrAudioFiles = dirFile.listFiles(audiofilter);
                        daisyIndexFile = new File(htmlDirPath);
                        fileChooser.setInitialDirectory(dirChooser);
                        if (dirChooser != null)
                            directoryChooser.setInitialDirectory(dirChooser);
                    }
                    else
                        if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML)
                        {
                            labelSeleectedFile.setText(m_selectedFile.getAbsolutePath());
                            dirFile = m_selectedFile.getParentFile();
                            selectedDirectory = null;
                            dirChooser = dirFile;
                            arrAudioFiles = dirFile.listFiles(audiofilter);
                            daisyIndexFile = m_selectedFile;
                        }

                    autoBookMarkPressedPlayButton(autoBookMark);
                }
            }
        }
    }

    private FileChooser.ExtensionFilter [] getFileExtenstionFilters()
    {
        String strExts =  String.join(",", arrFileExtenstions);
        strExts = strExts.replaceAll("\\."," *.");
        strExts = "*.mp3";
        FileChooser.ExtensionFilter  [] ret = new FileChooser.ExtensionFilter[arrFileExtenstions.length];
        FileChooser.ExtensionFilter cur = null;
        int i = 0;
        for(String strExt : arrFileExtenstions)
        {
            cur = new FileChooser.ExtensionFilter("Audio files (*" +strExt +")", "*" +strExt);;
            ret[i++] = cur;
        }

        return ret;
    }

    private void changeUILanguage(String selectedLang)
    {
        // kkkkk
        if (selectedLang == null || selectedLang.trim().length() == 0)
            return;
      //  Platform.runLater(new Runnable() {
      //      public void run() {
                switch ((selectedLang))
                {
                    case "English":
                        locale = new Locale("en", "EN");
                        PlayerController.locale = locale;
                        break;
                    case "Suomi":
                        locale = new Locale("fi", "FI");
                        PlayerController.locale = locale;
                        break;
                }
                try {
                    // appIsClosing();
                    appIsClosing();
                    mediaControlPane.stop();
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("JavaFx Player");
                    alert.setHeaderText("Changing user interface language by stopping and staring this application again.");
                    String s ="Start this application again and it will change gui lanugage..";
                    alert.setContentText(s);
                    // ObservableList<ButtonType> buttons = alert.getDialogPane().getButtonTypes();
                    alert.show();
                    primaryStage.close();
                    Platform.runLater(new Runnable() {
                        public void run() {
                         //  primaryStage.close();
                            try {
                      //       Thread.sleep(1000);
                              Stage newStage = m_application.startUIAgain();
                              Thread.sleep(1000);
                               newStage.requestFocus();
                           //     Thread.sleep(1000);
                            }catch (Exception e2){
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    Platform.runLater(new Runnable() {
                        public void run() {
                            labelMsg.setText("Error: " +e.getMessage());
                            labelMsg.requestFocus();
                        }
                    });
                }
         //   }
     //   });

    }

    @FXML
    public void pressedButtonPrevLink()
    { // todo
        System.out.println("pressedButtonPrevLink");
        System.out.println("bPreventIncreaseArrayIndex=" +bPreventIncreaseArrayIndex.toString());
        System.out.println("arrAudioFiles=" +arrAudioFiles);
        if (arrAudioFiles != null)
            System.out.println("arrAudioFiles.length=" +arrAudioFiles.length);
        System.out.println("iIndArrDirFiles=" +iIndArrDirFiles);
        System.out.println("");

        if (m_selectedFile == null || !bWebViewLoaded )
            return;
        boolean bValue = bPreventIncreaseArrayIndex.compareAndSet(false, true);
        if (!bValue)
            return;

        File mp3PlayFile = mediaControlPane.getPlayFile();
        double current_time = mediaControlPane.getStopAndCurrentTime();
        System.out.println("pressedButtonPrevLink current time = " +current_time /1000);
        SmilData_Par sdpar = getCurrentLinkIndex(current_time, mp3PlayFile);
        System.out.println("pressedButtonPrevLink sdpar = " +(sdpar != null ? sdpar.toString() : "null"));

        NewSelectedFile ret = null;
        System.out.println("bPreventIncreaseArrayIndex=" +bPreventIncreaseArrayIndex.toString());
        System.out.println("arrAudioFiles=" +arrAudioFiles);
        if (arrAudioFiles != null)
            System.out.println("arrAudioFiles.length=" +arrAudioFiles.length);
        System.out.println("iIndArrDirFiles=" +iIndArrDirFiles);
        System.out.println("");

        selectLinkFileAndStartTime(sdpar, mp3PlayFile, -1);
        bPreventIncreaseArrayIndex.set(false);
    }

    @FXML
    public void pressedButtonNextLink()
    {
        System.out.println("pressedButtonNextLink");

        System.out.println("bPreventIncreaseArrayIndex=" +bPreventIncreaseArrayIndex.toString());
        System.out.println("arrAudioFiles=" +arrAudioFiles);
        if (arrAudioFiles != null)
            System.out.println("arrAudioFiles.length=" +arrAudioFiles.length);
        System.out.println("iIndArrDirFiles=" +iIndArrDirFiles);
        System.out.println("");

        if (m_selectedFile == null || !bWebViewLoaded )
            return;
        boolean bValue = bPreventIncreaseArrayIndex.compareAndSet(false, true);
        if (!bValue)
            return;
// ffff
        double current_time = mediaControlPane.getStopAndCurrentTime();
        File mp3PlayFile = mediaControlPane.getPlayFile();
        SmilData_Par sdpar = getCurrentLinkIndex(current_time, mp3PlayFile);
        System.out.println("pressedButtonNextLink m_selectedFile = " +m_selectedFile.getAbsolutePath());
        System.out.println("pressedButtonNextLink current time = " +current_time /1000);
        System.out.println("pressedButtonNextLink sdpar = " +(sdpar != null ? sdpar.toString() : "null"));

        NewSelectedFile ret = null;
        System.out.println("bPreventIncreaseArrayIndex=" +bPreventIncreaseArrayIndex.toString());
        System.out.println("arrAudioFiles=" +arrAudioFiles);
        if (arrAudioFiles != null)
            System.out.println("arrAudioFiles.length=" +arrAudioFiles.length);
        System.out.println("iIndArrDirFiles=" +iIndArrDirFiles);
        System.out.println("pressedButtonNextLink m_selectedFile = " +m_selectedFile.getAbsolutePath());
        System.out.println("");

        selectLinkFileAndStartTime(sdpar, mp3PlayFile, +1);
        bPreventIncreaseArrayIndex.set(false);
    }

    private SmilData_Par getCurrentLinkIndex(double current_time, File mp3PlayFile)
    {
        SmilData_Par ret = null;
        if (mp3PlayFile == null)
            return null;
        SmilData sd = hashMapHRefs.get(mp3PlayFile.getAbsolutePath());
        if (sd == null)
            return null;
        String smilPath = sd.getSmilFilePath(); // .replaceAll("\\\\","/");
        ret = getCurrentIndexOfSmilData_Par(sd, current_time);
        return ret;
    }

    private SmilData_Par getCurrentIndexOfSmilData_Par(SmilData sd, double current_time)
    {
        SmilData_Par ret = null;
        if (sd == null)
            return null;
        if (current_time < 0.0)
            return null;
        double timeSec = current_time / 1000;
        List<SmilData_Par> list = sd.getSmilDataParsItems();
        int max = list.size();
        SmilData_Par sdpar = null;
        for(int i = 0; i < max; i++)
        {
            sdpar = list.get(i);
            if (sdpar == null)
                continue;
            if (sdpar.durationIsBetweenBeginAndEndTimes(timeSec)) {
                ret = sdpar;
                break;
            }
        }
        return ret;
    }

    public void selectLinkFileAndStartTime(SmilData_Par sdpar, File mp3PlayFile,
                                           int addOrSubtract)
    {
        int newInd = -1;
        boolean bNextFile_OR_PrevFile = false;
        if (sdpar == null)
            bNextFile_OR_PrevFile = true;
        else
        {
            newInd = sdpar.getInd();
            System.out.println("sdpar.getInd =" +newInd);
            if (addOrSubtract == 1)
                newInd++;
            else
                newInd--;

            if (newInd < 0)
                bNextFile_OR_PrevFile = true;
            else {
                SmilData sd = hashMapHRefs.get(mp3PlayFile.getAbsolutePath());
                if (sd == null)
                    return ;
                List<SmilData_Par> params = sd.getSmilDataParsItems();
                if (newInd < 0 || params == null || params.size() == 0 || params.size() <= newInd)
                    bNextFile_OR_PrevFile = true;
            }
        }

        if (bNextFile_OR_PrevFile)
        {
            System.out.println("sdpar == null");
            File new_selectedFile = getNewSelectedFileAfterCurrentFile(mp3PlayFile, addOrSubtract);
            if (new_selectedFile == null) {
       //         bPreventIncreaseArrayIndex.set(false);
                return;
            }
            if (addOrSubtract == 1 && iIndArrDirFiles < arrAudioFiles.length)
                iIndArrDirFiles++;
            else
            if (iIndArrDirFiles > 0)
                iIndArrDirFiles--;
            else
                return;

            m_selectedFile = new_selectedFile;
            SmilData sd = hashMapHRefs.get(new_selectedFile.getAbsolutePath());
            if (sd != null) {
                SmilData_Par par = null;
                List<SmilData_Par> pars = null;
                pars = sd.getSmilDataParsItems();
                if (pars != null)
                {
                    if (addOrSubtract == -1)
                        par = pars.get(pars.size()-1);
                    else
                        par = pars.get(0);
                    if (par != null)
                    {
                        double beginClip = par.getClip_beginSeconds();
                        int indArr = getIndArrOfListMp3Files(m_selectedFile);
                        if (new_selectedFile == null)
                            new_selectedFile = m_selectedFile;
                        mediaControlPane.setIndArrDirFiles(indArr);
                        mediaControlPane.playThisFile(new_selectedFile, beginClip, indArr, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                        int indSelectedFile = indArr; // mediaControl.possibleNextTrack();
                        if (indSelectedFile > -1 && bWebViewLoaded)
                        {
                            scrollIntoWebView(indSelectedFile);
                        }
                    }
                }
            }
            /*
            if (addOrSubtract == -1)
                playThisFileWithLastParData(new_selectedFile);
            else
                playThisFile(m_selectedFile);
             */
       //     bPreventIncreaseArrayIndex.set(false);
            return;
        }

        System.out.println("newInd =" +newInd);
        SmilData sd = hashMapHRefs.get(mp3PlayFile.getAbsolutePath());
        SmilData_Par par = null;
        List<SmilData_Par> pars = null;
        if (sd != null)
        {
            pars = sd.getSmilDataParsItems();
            if (newInd > -1 && newInd < pars.size())
            {
                par = pars.get(newInd);
                if (par == null)
                {
                    System.out.println("par == null!");
                }
            }
            else
            {
                selectLinkFileAndStartTime(null, mp3PlayFile, addOrSubtract);
                return;
            }
        }

        File new_selectedFile = null;
        if (sd == null || pars == null || newInd < 0) {
            System.out.println("sd == null || pars == null || newInd < 0!");
            new_selectedFile = getNewSelectedFileAfterCurrentFile(mp3PlayFile, addOrSubtract);
            if (new_selectedFile == null)
            {
     //           bPreventIncreaseArrayIndex.set(false);
                System.out.println("new_selectedFile == null!");
                return;
            }
            if (addOrSubtract == 1 && iIndArrDirFiles < arrAudioFiles.length)
                iIndArrDirFiles++;
            else
            if (iIndArrDirFiles > 0)
                iIndArrDirFiles--;
            else
                return;

            m_selectedFile = new_selectedFile;
            selectLinkFileAndStartTime(null, mp3PlayFile, addOrSubtract);
            /*
            if (addOrSubtract == -1)
                playThisFileWithLastParData(new_selectedFile);
            else
                playThisFile(m_selectedFile);
             */
    //        bPreventIncreaseArrayIndex.set(false);
            return;
        }

        String smilPath = sd.getSmilFilePath(); // .replace(".mp3",".smil").replaceAll("\\\\","/");
        List<SmilData_Par> parItems = sd.getSmilDataParsItems();
        if (parItems == null || parItems.size() == 0 || parItems.size() <= newInd)
        {
            new_selectedFile = getNewSelectedFileAfterCurrentFile(mp3PlayFile, addOrSubtract);
            if (new_selectedFile == null) {
//               bPreventIncreaseArrayIndex.set(false);
                return;
            }
            if (addOrSubtract == 1 && iIndArrDirFiles < arrAudioFiles.length)
                iIndArrDirFiles++;
            else
            if (iIndArrDirFiles > 0)
                iIndArrDirFiles--;
            m_selectedFile = new_selectedFile;
            if (addOrSubtract == -1)
                playThisFileWithLastParData(new_selectedFile);
            else
                playThisFile(m_selectedFile);
//            bPreventIncreaseArrayIndex.set(false);
            return;
        }

        // File newmp
 //       bPreventIncreaseArrayIndex.set(false);
        double beginClip = par.getClip_beginSeconds();
        int indArr = getIndArrOfListMp3Files(m_selectedFile);
        if (new_selectedFile == null)
            new_selectedFile = m_selectedFile;
        mediaControlPane.setIndArrDirFiles(indArr);
        mediaControlPane.playThisFile(new_selectedFile, beginClip, indArr, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
        int indSelectedFile = indArr; // mediaControl.possibleNextTrack();
        if (indSelectedFile > -1 && bWebViewLoaded)
        {
            scrollIntoWebView(indSelectedFile);
        }
    }

    private void playThisFileWithLastParData(File new_selectedFile)
    {
        if (new_selectedFile == null)
            return;
        String smilPath = new_selectedFile.getAbsolutePath().replace(".mp3",".smil").replaceAll("\\\\","/");
        SmilData sd = hashMapSmilData.get(smilPath);
        if (sd == null)
            return;
        List<SmilData_Par> list = sd.getSmilDataParsItems();
        if (list == null || list.size() == 0)
            return;
        int max = list.size();
        SmilData_Par sdpar = list.get(max-1);
        if (sdpar == null)
            return;
        double beginClip = sdpar.getClip_beginSeconds();
        int indArr = getIndArrOfListMp3Files(new_selectedFile);
        mediaControlPane.setIndArrDirFiles(indArr);
        mediaControlPane.playThisFile(new_selectedFile, beginClip, indArr, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
    }

    private File getNewSelectedFileAfterCurrentFile(File mp3PlayFile, int addOrSubtract)
    {
        File ret = null;
        if (mp3PlayFile == null)
            return null;
        if (addOrSubtract != 1 && addOrSubtract != -1)
            return null;
        int indArr = getIndArrOfListMp3Files(mp3PlayFile);
        if (indArr == -1)
            return null;
        if (addOrSubtract == 1)
            indArr++;
        else
        if (addOrSubtract == -1)
            indArr--;
        if (indArr < 0)
            return null;
        int max = listMp3Files.size();
        if (indArr >= max)
            return null;

        ret = listMp3Files.get(indArr);
        return ret;
    }

    @FXML
    protected void pressedAudioDirButton() {
        if (dirChooser != null && dirChooser.exists())
            directoryChooser.setInitialDirectory(dirChooser);
        selectedDirectory = directoryChooser.showDialog(this.primaryStage);
        if (selectedDirectory != null && selectedDirectory.exists()) {
            user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY;
            isDaisyNcc = false;
            dirChooser = selectedDirectory;
            bWebViewLoaded = false;
            labelSeleectedFile.setText(selectedDirectory.getAbsolutePath());
           // webView.getEngine().load(null);
            Platform.runLater(new Runnable() {
                public void run() {
                    webView.getEngine().setJavaScriptEnabled(false);
                    webView.getEngine().load(null);
                    webView.getEngine().setJavaScriptEnabled(true);
                    try {
                        Thread.sleep(250);
                    }catch (Exception e){
                    }
                }
            });
            pressedPlayButton();
        }
    }

    @FXML
    protected void pressedDaisyFileButton()
    {
        pressedAudioFileButtonFrom(false);
    }

    @FXML
    protected void pressedAudioFileButton() {
        pressedAudioFileButtonFrom(true);
    }

    private void pressedAudioFileButtonFrom(boolean bCalledFromAudioButtonPressed) {
        if (bCalledFromAudioButtonPressed) {
            fileChooser.getExtensionFilters().clear();
            try {
                Thread.sleep(500);
            }catch (Exception e){
            }
            FileChooser.ExtensionFilter[] extFilters = getFileExtenstionFilters();
            for (FileChooser.ExtensionFilter ef : extFilters)
                fileChooser.getExtensionFilters().add(ef);
        }
        else
        {
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Daisy html files (*.html)", "*.html"));
        }

        if (fChooser != null)
            fileChooser.setInitialDirectory(fChooser);
        File selectedFile = fileChooser.showOpenDialog(this.primaryStage);
        loadFile(selectedFile, null);
    }

    private void loadFile(File selectedFile, BookMark bookMark){
        if (selectedFile != null && selectedFile.exists()) {

            cleanDaisyDataVariables();
            Platform.runLater(new Runnable() {
                public void run() {
                    /*
                    webView.getEngine().setJavaScriptEnabled(false);
                    webView.getEngine().load(null);
                    webView.getEngine().setJavaScriptEnabled(true);
                     */
                    mediaControlPane.stop();
                    try {
                        Thread.sleep(250);
                    }catch (Exception e){
                    }
                }
            });
            if(selectedFile.getName().endsWith(".html") || selectedFile.getName().endsWith(".htm"))
            {
                isDaisyNcc = true;
                user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML;
                mediaControlPane.setTimeHop(60000);
            }
            else
            {
                if (isDaisyNcc) // earlier selected file was daisy
                    Platform.runLater(new Runnable() {
                        public void run() {
                            webView.getEngine().setJavaScriptEnabled(false);
                            webView.getEngine().load(null);
                            webView.getEngine().setJavaScriptEnabled(true);
                        }
                    });
                try {
                    Thread.sleep(250);
                }catch (Exception e){
                }
                isDaisyNcc = false;
                user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_FILE;
            }
            fChooser = selectedFile.getParentFile();
            labelSeleectedFile.setText(selectedFile.getAbsolutePath());
            selectedDirectory = null;
            // if (webView.getEngine().getLocation() != null)
            //   webView.getEngine().load(null);
            autoBookMarkPressedPlayButton(bookMark);
        }
    }

    @FXML
    protected void pressedPlayButton() {
        autoBookMarkPressedPlayButton(null);
    }

    private void autoBookMarkPressedPlayButton(BookMark autoBookMark) {
        if (labelSeleectedFile.getText().trim().length()>0) {
            File selectedFile = new File(labelSeleectedFile.getText());
            if (selectedFile != null && selectedFile.exists()) {
                if (selectedFile.isDirectory()) {
                    bWebViewLoaded = false;
                    playDirectory(selectedFile, autoBookMark);
                    updateSplitPaneDividerPosition();
                }
                else {
                    bWebViewLoaded = false;
                    playFile(selectedFile, autoBookMark);
                    updateSplitPaneDividerPosition();
                }
            }
        }
    }

    private void cleanDaisyDataVariables()
    {
        iSelectedSmilParLevel = 0; // user selected smil par item level; 0 = all!
        par_level_direction = PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION;
        iMaxSmilParLevelNormalPage = 0;
        hashMapSmils = new HashMap<String,String>();
        listSmils = new ArrayList<String>();
        hashMapHRefs = new HashMap<String, SmilData>();
        hashMapSmilData = new HashMap<String,SmilData>();
        listMp3Files = new ArrayList<File>();
        hashMapMp3Files = new HashMap<String,File>();
        arrAudioFiles = null;
        iIndArrDirFiles = -1;
        bPreventIncreaseArrayIndex = new AtomicBoolean(false);
        speechLanaguage = "fi";
        daisyIndexFile = null;
        dCurrentPosition = -1.0;
        hashMapLevelHeaderWords = new HashMap<String,Integer>();
        hashMapSmilIndexLinks = new HashMap<String,SmilIndexLink>();
        allSmilData_ParItems = new ArrayList<>();
        metadataSmilDataPar = new SmilData_ParMeta();
        metaGroups = new HashMap<String, SmilData_ParGroup>();
        currentMetaGroup = null;
        currentNormalPage = null;
        currentHeader = null;
        bCreateMetaLevelsAbovePages = false;
        iMaxMetaLevel = 0;
        logicalRoot = new SmilData_Par(SmilData_Par.cnstLogicalRoot);
        lastParentSmilData_Pars = new HashMap<>();
        describeMetaGroups = new DescribeMetaGroups();
    }

    private void playThisFile(File selectedFile) {
        mediaControlPane.playFile(selectedFile);
        /*
        WebEngine webEngine = webView.getEngine();
        if (selectedFile.getName().endsWith(".html") || selectedFile.getName().endsWith(".htm")) {
            mediaControl.playFile(selectedFile);
        } else {
            buttonNextTrack.setDisable(true);
            buttonPreviousTrack.setDisable(true);
            buttonPlay.setDisable(false);
            mediaControl.playFile(selectedFile);
            updateSplitPaneDividerPosition();
        }
         */
    }

    private String getSpeechLanguage(File selectedFile)
    {
        String ret = null;
        Path path = Paths.get(selectedFile.getAbsolutePath());

        List<String> listContent = null;
        String strXml = null;
        try {
            try {
                listContent = Files.readAllLines(path);
            }catch (IOException ioe2){
                System.out.println("'" +ioe2.getMessage() +"'");
                if (ioe2.getMessage().contains("Input length = 1"))
                    listContent = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
                else
                    throw ioe2;
            }
            if (listContent != null && listContent.size() > 0)
            {
                StringBuffer sb = new StringBuffer();
                sb.append(listContent);
                strXml = sb.toString();
                int ind = strXml.indexOf("<html");
                if (ind > -1)
                {
                    String strHtml = strXml.substring(ind);
                    if (strHtml != null && strHtml.trim().length() > 0)
                    {
                        int indEnd = strHtml.indexOf(">");
                        if (indEnd > -1) {
                            strHtml = strHtml.substring(0, indEnd);
                            if (strHtml != null && strHtml.trim().length() > 0) {
                                Pattern pattern = Pattern.compile("xml:lang=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
                                Matcher matcher = pattern.matcher(strHtml);
                                String strHtml2 = strHtml.replace("xml:lang=", "");
                                String lang2 = null, lang1 = null;
                                if (matcher.find())
                                    lang1 = matcher.group(1);
                                pattern = Pattern.compile("lang=\"(.*?)\"", Pattern.CASE_INSENSITIVE);
                                matcher = pattern.matcher(strHtml2);
                                if (matcher.find())
                                    lang2 = matcher.group(1);
                                if (lang1 != null)
                                    ret = lang1;
                                else
                                if (lang2 != null)
                                    ret = lang2;
                            }
                        }
                    }
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
        return ret;
    }

    private void playFile(File selectedFile, BookMark autoBookMark) {
        bPreventIncreaseArrayIndex.set(false);
        WebEngine webEngine = webView.getEngine();
        bWebViewLoaded = false;
        iIndArrDirFiles = -1;
        arrAudioFiles = null;

        deleteAllEarlierData();
        mediaControlPane.stop();

        if ( selectedFile != null && (!selectedFile.getName().endsWith(".html")
            && !selectedFile.getName().endsWith(".htm")))
        {
       //     webEngine.load(null);
            daisyIndexFile = null;
        }
        m_selectedFile = selectedFile;

        if (selectedFile.getName().endsWith(".html") || selectedFile.getName().endsWith(".htm"))
        {
            isDaisyNcc = true;
            mediaControlPane.setTimeHop(60000);
            buttonPrevLevelLink.setDisable(false);
            buttonNextLevelLink.setDisable(false);
            speechLanaguage = getSpeechLanguage(selectedFile);
            readMediaContentData(selectedFile);
            bWebViewLoaded = true;
    //       user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML;
            if (autoBookMark == null)
                webEngine.load(selectedFile.toURI().toString());
            daisyIndexFile = selectedFile;
           // mediaControl.playFile(null);
            buttonPrevLink.setDisable(false);
            buttonNextLink.setDisable(false);
            buttonLowerLevel.setDisable(false);
            buttonUpperLevel.setDisable(false);
            buttonPlay.setDisable(false);

            if (autoBookMark != null && autoBookMark.getDaisyBookIndexPath() != null) {
                m_selectedFile = new File(autoBookMark.getPlayFilePath());
                double iBeginClip = autoBookMark.getPosition();
                dCurrentPosition = iBeginClip;
                iIndArrDirFiles = Integer.parseInt(autoBookMark.getIndArrDirFiles()) ;
                arrAudioFiles = selectedFile.getParentFile().listFiles(audiofilter);
                buttonNextTrack.setDisable(false);
                buttonPreviousTrack.setDisable(false);
                Platform.runLater(new Runnable() {
                    public void run() {
                        webEngine.load(daisyIndexFile.toURI().toString());
                        // mediaControlPane.playThisFile(m_selectedFile, iBeginClip, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
                    }
                });
            }
        }
        else {
            buttonPrevLink.setDisable(true);
            buttonNextLink.setDisable(true);
            buttonNextTrack.setDisable(true);
            buttonPreviousTrack.setDisable(true);
            buttonPlay.setDisable(false);
            mediaControlPane.playFile(selectedFile);
          //  user_selected_to_open = USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_FILE;
            updateSplitPaneDividerPosition();
        }
    }

    private void readMediaContentData(File selectedFile)
    {
        if (selectedFile == null)
            return;
        if (!selectedFile.exists())
            return;
    }

    private void updateSplitPaneDividerPosition()
    {
        boolean bMetaData = mediaControlPane.isMetaData();
       // splitPane.setDividerPositions(0.2);
        callUpdateSplitPaneDividerPosition(bMetaData, null);
        // splitPane.setDividerPositions(1);
    }

    public boolean isVideoFile(File f)
    {
        if (f == null)
            return false;
        if (!f.exists())
            return false;
        if (f.getName().endsWith(".mp4"))
            return true;
        return false;
    }

    public void callUpdateSplitPaneDividerPosition(boolean bValue, String metadata)
    {
        boolean bMetaData = mediaControlPane.isMetaData();
/*        mediaScrollPane.setMinWidth(0);
        mediaScrollPane.setMinHeight(0);
 */
        if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY) {
            splitPane.setDividerPositions(1);
            return;
        }
        else
        if(user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML) {
            splitPane.setDividerPositions(0.3);
            return;
        }
        else
        if(user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_FILE) {
            splitPane.setDividerPositions(0.6);
            return;
        }
        /*
        if (isVideoFile(m_selectedFile) ||!(m_selectedFile.getName().endsWith(".html")
                || m_selectedFile.getName().endsWith(".htm")) && !isDaisyNcc )
        {
            if (user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.DAISY_HTML
                || user_selected_to_open == USER_SELECTED_OPEN_DIR_OR_FILE.AUDIO_DIRECTORY)
                splitPane.setDividerPositions(1);
            else {
                if (bMetaData)
                    splitPane.setDividerPositions(0.4);
                else
                    splitPane.setDividerPositions(0.2);
            }

            ?*
            mediaScrollPane.setMinWidth(mediaScrollPane.getPrefWidth());
            mediaScrollPane.setMinHeight(mediaScrollPane.getPrefHeight());
            mediaScrollPane.setMinHeight(0);
             *?
            return;
        }
        */
        if (bMetaData)
            splitPane.setDividerPositions(0.4);
        else
            splitPane.setDividerPositions(0.2);
    }

    private void playDirectory(File selectedFile, BookMark autoBookMark)
    {
        WebEngine webEngine = webView.getEngine();
        // webEngine.load(null);
        webEngine.loadContent(null);
        deleteAllEarlierData();

        boolean bValue = bPreventIncreaseArrayIndex.weakCompareAndSet(false, true);
        if (!bValue)
            return;
        hashMapSmils.clear();
        hashMapHRefs.clear();
        hashMapSmilData.clear();
        hashMapMp3Files.clear();
        listSmils.clear();

        mediaControlPane.stop();
        buttonNextTrack.setDisable(false);
        buttonPreviousTrack.setDisable(false);
        buttonPlay.setDisable(false);
        // mediaControl.playDirectory(selectedFile);
        arrAudioFiles = selectedFile.listFiles(audiofilter);
        iIndArrDirFiles = 0;
        if (arrAudioFiles == null || arrAudioFiles.length == 0)
        {
            labelMsg.setText("No audio files at the directory.");
            bPreventIncreaseArrayIndex.set(false);
            return;
        }
        double iBeginClip = 0.0;
        if (autoBookMark == null)
            m_selectedFile = arrAudioFiles[0];
        else {
            String strAudioPath = autoBookMark.getPlayFilePath();
            if (strAudioPath != null) {
                m_selectedFile = new File(strAudioPath);
                iIndArrDirFiles = Integer.parseInt(autoBookMark.getIndArrDirFiles());
                iBeginClip = autoBookMark.getPosition();
            }
        }
        bPreventIncreaseArrayIndex.set(false);
        if (m_selectedFile != null) {
            mediaControlPane.playThisFile(m_selectedFile, iBeginClip, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
        }
        /*
        if (!selectedFile.exists() || !selectedFile.isDirectory())
            return;
        File [] files = selectedFile.listFiles();
        for(File f : files)
        {
            mediaControl.playFile(f);
        }
         */
    }

    public NewSelectedFile getPossibleNextPlayfile()
    {
        NewSelectedFile ret = null;
        System.out.println("bPreventIncreaseArrayIndex=" +bPreventIncreaseArrayIndex.toString());
        System.out.println("arrAudioFiles=" +arrAudioFiles);
        if (arrAudioFiles != null)
            System.out.println("arrAudioFiles.length=" +arrAudioFiles.length);
        System.out.println("iIndArrDirFiles=" +iIndArrDirFiles);
        System.out.println("");

        System.out.println("");
        if (bPreventIncreaseArrayIndex.get() || arrAudioFiles == null || arrAudioFiles.length == 0)
            return null;
        if (iIndArrDirFiles < 0 || iIndArrDirFiles >= arrAudioFiles.length)
            return null;
        boolean bValue = bPreventIncreaseArrayIndex.weakCompareAndSet(false, true);
        if (!bValue)
            return null;
        int iNewInd = iIndArrDirFiles;
        iNewInd++;
        System.out.println("iNewInd=" +iNewInd);
        if (iNewInd < 0 || iNewInd >= arrAudioFiles.length)
        {
            bPreventIncreaseArrayIndex.set(false);
            return null;
        }
        iIndArrDirFiles = iNewInd;
        m_selectedFile = arrAudioFiles[iIndArrDirFiles];
        System.out.println("new m_selectedFile=" +m_selectedFile.getAbsolutePath());
        ret = new NewSelectedFile();
        ret.iSize = arrAudioFiles.length;
        ret.iCounter = iIndArrDirFiles;
        ret.newSelectedfile = m_selectedFile;
        bPreventIncreaseArrayIndex.set(false);
       // dddd
        return ret;
    }

    private File getPossibleEarlierFile()
    {
        if (bPreventIncreaseArrayIndex.get() || arrAudioFiles == null || arrAudioFiles.length == 0)
            return null;
        if (iIndArrDirFiles < 0 || iIndArrDirFiles >= arrAudioFiles.length)
            return null;
        boolean bValue = bPreventIncreaseArrayIndex.weakCompareAndSet(false, true);
        if (!bValue)
            return null;
        int iNewInd = iIndArrDirFiles;
        iNewInd--;
        System.out.println("iNewInd=" +iNewInd);
        if (iNewInd < 0 || iNewInd >= arrAudioFiles.length)
        {
            bPreventIncreaseArrayIndex.set(false);
            return null;
        }
        iIndArrDirFiles = iNewInd;
        m_selectedFile = arrAudioFiles[iIndArrDirFiles];
        return m_selectedFile;
    }

    @FXML
    protected void pressedButtonNextTrack() {
        if (arrAudioFiles == null || arrAudioFiles.length == 0)
            return;
        boolean bValue = bPreventIncreaseArrayIndex.compareAndSet(false, true);
        if (!bValue)
            return;

        if (iIndArrDirFiles < 0 || iIndArrDirFiles >= arrAudioFiles.length) {
            bPreventIncreaseArrayIndex.set(false);
            return;
        }
        int iNewInd = iIndArrDirFiles;
        iNewInd++;
        if (iNewInd < 0 || iNewInd >= arrAudioFiles.length) {
            bPreventIncreaseArrayIndex.set(false);
            return;
        }
        iIndArrDirFiles = iNewInd;
        m_selectedFile = arrAudioFiles[iIndArrDirFiles];
        bPreventIncreaseArrayIndex.set(false);
        int indSelectedFile = iIndArrDirFiles; // mediaControl.possibleNextTrack();
        if (indSelectedFile > -1 && bWebViewLoaded)
        {
            scrollIntoWebView(indSelectedFile);
        }
        updateSplitPaneDividerPosition();
        mediaControlPane.playThisFile(m_selectedFile, -1.0, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
    }

    private void scrollIntoWebViewLink(String hrefid)
    {
        //   String value = getValueFromSmilHref(href);
        execJs = "document.getElementById(" +'"' + hrefid +'"' +").scrollIntoView();";
        if (hrefid != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    WebEngine engine = webView.getEngine();
                    if (engine != null)
                        engine.executeScript(execJs);
                }
            });
        }
    }

    private void scrollIntoWebView(int indSelectedFile)
    {
        if (indSelectedFile < 0)
            return;
        if (listMp3Files == null || listMp3Files.size() ==  0)
            return;
        int max = listMp3Files.size();
        File [] arrFiles = new File[max];
        arrFiles = listMp3Files.toArray(arrFiles);
        int ind = -1;
        File selectedFile = null;
        for(File f: arrFiles)
        {
            ind++;
            if (ind == indSelectedFile)
            {
                selectedFile = f;
                break;
            }
        }
        if (selectedFile != null)
        {
            String strFileName = selectedFile.getAbsolutePath();
            SmilData sd = hashMapHRefs.get(strFileName);
            String href = sd.getHref();
            String key = getKeyFromSmilHref(href);
         //   String value = getValueFromSmilHref(href);
            String docExec = "document.getElementById(\"" + key +"\")";
            execJs = "if (document != undefined && document != null && " +docExec +" != undefined && " +docExec +" != null) document.getElementById(" +'"' + key +'"' +").scrollIntoView();";
            // System.out.println("execJs=" +execJs);
            if (key != null) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        WebEngine engine = webView.getEngine();
                        if (engine != null)
                            engine.executeScript(execJs);
                    }
                });
            }
            /*
            Document document = engine.getDocument();
            if (document != null)
            {
                Element anchor = document.getElementById(key);
                if (anchor != null)
            }
             */
        }
    }

    @FXML
    protected void pressedButtonPreviousTrack() {
        if (arrAudioFiles == null || arrAudioFiles.length == 0)
            return;
        if (iIndArrDirFiles < 0 || iIndArrDirFiles >= arrAudioFiles.length)
            return;
        boolean bValue = bPreventIncreaseArrayIndex.compareAndSet(false, true);
        if (!bValue)
             return;
        int iNewInd = iIndArrDirFiles;
        iNewInd--;
        if (iNewInd < 0 || iNewInd >= arrAudioFiles.length)
        {
            bPreventIncreaseArrayIndex.set(false);
            return;
        }

        iIndArrDirFiles = iNewInd;
        m_selectedFile = arrAudioFiles[iIndArrDirFiles];
        if (m_selectedFile == null || !m_selectedFile.exists()) {
            bPreventIncreaseArrayIndex.set(false);
            return;
        }
        bPreventIncreaseArrayIndex.set(false);
        int indSelectedFile = iIndArrDirFiles; // mediaControl.possibleNextTrack();
        if (indSelectedFile > -1 && bWebViewLoaded)
        {
            scrollIntoWebView(indSelectedFile);
        }
        updateSplitPaneDividerPosition();
        mediaControlPane.playThisFile(m_selectedFile, -1.0, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
        bPreventIncreaseArrayIndex.set(false);
    }

    public void addAnchorTagHref(String href, String id, String text, String attrClass,
                                 String parentNodeElementName, int iCounter,
                                 String parentId)
    {
        if (!bWebViewLoaded && (m_selectedFile == null || (!m_selectedFile.getName().endsWith(".html") && !m_selectedFile.getName().endsWith(".htm"))))
            return;
      // System.out.println("addAnchorTagHref href=" +href +" id=" +id);
        String key = getKeyFromSmilHref(href);
        String value = getValueFromSmilHref(href);
        SmilData sd;
        SmilIndexLink link;
        if (key != null && key.trim().length() > 0 && value != null && value.trim().length() > 0  ) {
            link = hashMapSmilIndexLinks.get(key);
            if (link == null)
            {
                link = new SmilIndexLink();
                link.setId(key);
                link.setIndexhref(href);
                link.setStrText(text);
                link.setParnentElelmentName(parentNodeElementName);
                link.setParentId(parentId);
                link.setParentClass(attrClass);
                link.setParentId(parentId);
                hashMapSmilIndexLinks.put(key, link);
            }
            hashMapSmils.put(key, value);
            sd = hashMapSmilData.get(value);
            if (sd == null) {
                listSmils.add(value);
                sd = new SmilData(null, value, null, attrClass, parentNodeElementName);
                sd.setIndHtmlLink(iCounter);
                sd.setSmilFilePath(value);
                sd.setHref(href);
                hashMapSmilData.put(value, sd);
            }
            hashMapTexts.put(key, text);
        }
    }

    public List<SmilData_Par> getUpdatedSmilDataParsItems( List<SmilData_Par> parlist)
    {
        if (parlist == null)
            return null;
        SmilIndexLink indexLink;
        List<SmilData_Par> newparlist = new ArrayList<>();
        for(SmilData_Par par : parlist)
        {
            indexLink = hashMapSmilIndexLinks.get(par.getText_id());
            if (indexLink != null) {
                par.setClassName(indexLink.getParentClassName());
                if (indexLink.getParentClassName() != null
                    && indexLink.getParentClassName().equals("page-normal"))
                    currentNormalPage = par;
            }
            if (par.getSmilParLevel()>0)
                currentHeader = par;
            if (currentNormalPage != null &&  (par.getClassName() == null || par.getClassName() != null
                && !par.getClassName().contains("page"))) {
               // System.out.println("currentNormalPage.getText()=" +currentNormalPage.getText());
                par.setUnderPage(Integer.parseInt(currentNormalPage.getText()));
                par.setUnderPageLevel(currentNormalPage.getSmilParLevel());
                par.setUnderPagePar_id(currentNormalPage.getPar_id());
            }
            if (currentHeader != null)
                par.setUnderHeaderPar_id(currentHeader.getPar_id());
            newparlist.add(par);
        }
        return newparlist;
    }

    public void allAnchorTagHrefCalledInHtmlDoc(String strMaxPages, String strCds,
                                                String strDepth, String strTitle,
                                                String strCreator, String strRevisionDate)
    {
      //  System.out.println("allAnchorTagHrefCalledInHtmlDoc ->");
        SmilData sd = null;
        String text = null;
        int i = 0;
        File f = null;
        String mp3FPath = null;
        ReadSmillDataFromFile pair;

        metadataSmilDataPar.setStrCds(strCds);
        metadataSmilDataPar.setStrCreator(strCreator);
        metadataSmilDataPar.setStrDepth(strDepth);
        metadataSmilDataPar.setStrMaxPages(strMaxPages);
        metadataSmilDataPar.setStrRevisionDate(strRevisionDate);
        metadataSmilDataPar.setStrTitle(strTitle);

        allSmilData_ParItems.clear();
        currentNormalPage = null;

        for (String key : listSmils)
        {
            sd = hashMapSmilData.get(key);
            if (sd == null) {
                System.out.println("sd == null");
                continue;
            }
            pair = readSmilData(key);
            if (pair == null)
            {
                System.out.println("stop");
            }
            sd.setPair(pair);
            sd.setIndexOfSmilFile(i);
            if (sd.getSmilDataParsItems()!=null) {
                allSmilData_ParItems.addAll(sd.getSmilDataParsItems());;
            }
            i++;

            mp3FPath = sd.getMp3filename();
            f = hashMapMp3Files.get(mp3FPath);
            if (f == null)
                f = new File(mp3FPath);
            hashMapMp3Files.put(mp3FPath, f);
            hashMapHRefs.put(f.getAbsolutePath(), sd);
            listMp3Files.add(f);

            // sd = getSmilData(null, key);
            hashMapSmilData.put(key, sd);
        }

        /*
        System.out.println("iMaxSmilParLevelNormalPage=" +iMaxSmilParLevelNormalPage);
        System.out.println("iMaxSmilParLevel=" +iMaxSmilParLevel);
        */

        /*
        System.out.println("hashMapSmils size=" +hashMapSmils.keySet().size());
        System.out.println("hashMapSmilData size=" +hashMapSmilData.keySet().size());
        System.out.println("hashMapSmils size=" +hashMapSmils.keySet().size());
        System.out.println("hashMapHRefs size=" +hashMapHRefs.keySet().size());
        */
        allSmilData_ParItems = getUpdatedSmilDataParsItems(allSmilData_ParItems);
     //   ddd

       if (!bCreateMetaLevelsAbovePages && isDaisyNcc)
          createMetaLevelsAbovePages();

        int max = listMp3Files.size();
        if (max > 0)
        {
            arrAudioFiles = new File[max];
            buttonNextTrack.setDisable(false);
            buttonPreviousTrack.setDisable(false);
            arrAudioFiles = listMp3Files.toArray(arrAudioFiles);
            // mediaControl.playTheseMp3Files(arrFiles);
            if (m_selectedFile != null && (m_selectedFile.getName().endsWith(".html") || m_selectedFile.getName().endsWith(".htm")))
            { // user has open ncc.html file:
                iIndArrDirFiles = 0;
                m_selectedFile = arrAudioFiles[0];
                bPreventIncreaseArrayIndex.set(false);
                dCurrentPosition = -1.0;
            }
            else
            { // loaded in initializing:
                m_selectedFile = arrAudioFiles[iIndArrDirFiles];
                bPreventIncreaseArrayIndex.set(false);
            }
            mediaControlPane.stop();
            mediaControlPane.setIndArrDirFiles(iIndArrDirFiles);
            mediaControlPane.playThisFile(m_selectedFile, dCurrentPosition, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, "");
            int indSelectedFile = iIndArrDirFiles; // mediaControl.possibleNextTrack();
            if (indSelectedFile > -1 && bWebViewLoaded)
            {
                scrollIntoWebView(indSelectedFile);
            }
        }
    }

    private void setParentIdsOfSmilData_ParItems()
    {
        //ffff
        if (lastParentSmilData_Pars.size() == 0)
            lastParentSmilData_Pars.put(SmilData_Par.cnstLogicalRoot, logicalRoot);

        SmilData_Par currentMeta = null;
        String strClassName = null;
        for (SmilData_Par par : allSmilData_ParItems)
        {
            if (par.getClassName() == null) { // => <h3 id="econ_0026"><a href="econ0010.smil#ec100004">What is economics?</a></h3>
                if (par.getSmilParLevel() > 1 && lastParentSmilData_Pars.get("chapter") != null)
                    par.setParentId(lastParentSmilData_Pars.get("chapter").getPar_id());
                else // => parent is most likely the logical root:
                    par.setParentId(logicalRoot.getPar_id());
            }
            else {
                strClassName = par.getClassName();
                if (strClassName != null)
                {
                    if (strClassName.equals("title")) {
                        par.setParentId(logicalRoot.getPar_id());
                        if (lastParentSmilData_Pars.get("tile") == null)
                            lastParentSmilData_Pars.put("title", par);
                    }
                    else
                    {
                        if (strClassName.equals("part")) { // => part -> section -> chapter -> page-normal
                            par.setParentId(logicalRoot.getPar_id());
                            lastParentSmilData_Pars.put("part", par);
                        }
                        else
                        {
                            if (strClassName.equals("section")) {
                                currentMeta = lastParentSmilData_Pars.get("part");
                                if (currentMeta == null)
                                    par.setParentId(logicalRoot.getPar_id());
                                else
                                    par.setParentId(currentMeta.getPar_id());
                                lastParentSmilData_Pars.put("section", par);
                            }
                            else
                            if (strClassName.equals("chapter")) {
                                currentMeta = lastParentSmilData_Pars.get("section");
                                if (currentMeta == null) {
                                    if (lastParentSmilData_Pars.get("part") == null)
                                        par.setParentId(logicalRoot.getPar_id());
                                    else
                                    {
                                        currentMeta = lastParentSmilData_Pars.get("part");
                                        par.setParentId(currentMeta.getPar_id());
                                    }
                                }
                                else
                                    par.setParentId(currentMeta.getPar_id());
                                lastParentSmilData_Pars.put("chapter", par);
                            }
                            else
                            {
                                par.setParentId(logicalRoot.getPar_id());
                            }
                        }
                    }
                }
            }
        }
    }

    private void createMetaLevelsAbovePages()
    {
        // TODO: !! metaGroups == NULL: metaGroups = metadataSmilDataPar.getMetagroups();
        SmilData_ParGroup allPageGroup = new SmilData_ParGroup();
        allPageGroup.setSameGroupNames(false);
//        SmilData_ParGroup specialPageGroup = new SmilData_ParGroup();
        for(SmilData_Par par : allSmilData_ParItems)
        {
            if (par.getClassName() != null && par.getClassName().contains("page"))
                 allPageGroup.addSmilDataPar(par);
        }
        allPageGroup.setStrGroupName(cnstAllPages);
        if (metaGroups != null)
            metaGroups.put(allPageGroup.getStrGroupName(), allPageGroup);

        SmilData_ParGroup sentenceGroup = new SmilData_ParGroup();
        sentenceGroup.setStrGroupName(cnstSentence);
        if (metaGroups != null)
            metaGroups.put(cnstSentence, sentenceGroup);

        SmilData_ParGroup timeshiftGroup = new SmilData_ParGroup();
        sentenceGroup.setStrGroupName(cnstTimeShift);
        metaGroups.put(cnstTimeShift, timeshiftGroup);

        SmilData_ParGroup parItemsAbovePageGroup = new SmilData_ParGroup();
        parItemsAbovePageGroup.setSameGroupNames(false);
        List<Integer> pagelevels = allPageGroup.getListConstainLevels();
        for(SmilData_Par par : allSmilData_ParItems)
        {
            if (par.getClassName() != null && !par.getClassName().contains("page")
                    && par.getSmilParLevel() > 0 /* && isLowerThasnSomeOfLevels(pagelevels, par.getSmilParLevel()) */
                    && ((metadataSmilDataPar.getiMaxPage() > 0 && metadataSmilDataPar.getiMaxPage() <= par.getSmilParLevel() )
                    || iMaxSmilParLevelNormalPage > par.getSmilParLevel())) {
                SmilData_ParGroup g = metaGroups.get(par.getSmilParLevel());
                if (g == null)
                  g = new SmilData_ParGroup();
                g.addSmilDataPar(par);
                metaGroups.put("" +par.getSmilParLevel(), g);
                parItemsAbovePageGroup.addSmilDataPar(par);
            }
        }
        parItemsAbovePageGroup.setStrGroupName(cnstParItemsAbovePages);
        metaGroups.put(parItemsAbovePageGroup.getStrGroupName(), parItemsAbovePageGroup);

        SmilData_ParGroup parItemsLowerPageGroup = new SmilData_ParGroup();
        parItemsLowerPageGroup.setSameGroupNames(false);
        for(SmilData_Par par : allSmilData_ParItems)
        {
            if (par.getClassName() != null && !par.getClassName().contains("page")
                    && par.getSmilParLevel() > 0 && isGreaterThasnSomeOfLevels(pagelevels, par.getSmilParLevel()))
                parItemsLowerPageGroup.addSmilDataPar(par);
        }
        parItemsLowerPageGroup.setStrGroupName(cnstParItemsLowerPages);
        metaGroups.put(parItemsLowerPageGroup.getStrGroupName(), parItemsLowerPageGroup);

        setParentIdsOfSmilData_ParItems();

        SmilData_ParGroup partGroup = metaGroups.get("part");

        SmilData_ParGroup sectionGroup = metaGroups.get("section");
        if (partGroup == null && sectionGroup != null)
        {
            if (describeMetaGroups.hmNumberMetaGroup.get("1") == null) {
                describeMetaGroups.hmNumberMetaGroup.put("1", "section");
                describeMetaGroups.iNumberMetaGroups++;
            }
            addIntoMetaGroup("1", sectionGroup, true);
            SmilData_ParGroup chapterGroup = metaGroups.get("chapter");
            if (chapterGroup != null) {
                if (describeMetaGroups.hmNumberMetaGroup.get("2") == null)
                {
                    describeMetaGroups.hmNumberMetaGroup.put("2", "chapter");
                    describeMetaGroups.iNumberMetaGroups++;
                }
                addIntoMetaGroup("2", chapterGroup, true);
            }
        }
        else
        {
            if (partGroup == null && sectionGroup == null) {
                SmilData_ParGroup chapterGroup = metaGroups.get("chapter");
                if (chapterGroup != null) {
                    if (describeMetaGroups.hmNumberMetaGroup.get("1") == null)
                    {
                        describeMetaGroups.hmNumberMetaGroup.put("1", "chapter");
                        describeMetaGroups.iNumberMetaGroups++;
                    }
                    addIntoMetaGroup("1", chapterGroup, true);
                }
            }
            else {
                if (partGroup != null) {
                    if (describeMetaGroups.hmNumberMetaGroup.get("1") == null)
                    {
                        describeMetaGroups.hmNumberMetaGroup.put("1", "part");
                        describeMetaGroups.iNumberMetaGroups++;
                    }
                    addIntoMetaGroup("1", partGroup, true);
                    if (sectionGroup != null) {
                        if (describeMetaGroups.hmNumberMetaGroup.get("2") == null)
                        {
                            describeMetaGroups.hmNumberMetaGroup.put("2", "section");
                            describeMetaGroups.iNumberMetaGroups++;
                        }
                        addIntoMetaGroup("2", sectionGroup, true);
                        SmilData_ParGroup chapterGroup = metaGroups.get("chapter");
                        if (chapterGroup != null) {
                            if (describeMetaGroups.hmNumberMetaGroup.get("3") == null)
                            {
                                describeMetaGroups.hmNumberMetaGroup.put("3", "chapter");
                                describeMetaGroups.iNumberMetaGroups++;
                            }
                            addIntoMetaGroup("3", chapterGroup, true);
                        }
                    }
                    else
                    {
                        SmilData_ParGroup chapterGroup = metaGroups.get("chapter");
                        if (chapterGroup != null) {
                            if (describeMetaGroups.hmNumberMetaGroup.get("2") == null)
                            {
                                describeMetaGroups.hmNumberMetaGroup.put("2", "chapter");
                                describeMetaGroups.iNumberMetaGroups++;
                            }
                            addIntoMetaGroup("2", chapterGroup, true);
                        }
                    }
                }
            }
        }

        checkIfMetaGroupItemsExists("1");
        checkIfMetaGroupItemsExists("2");
        checkIfMetaGroupItemsExists("3");
        checkIfMetaGroupItemsExists("4");
        iMaxMetaLevel++; // add a 1 value for all-pages metagroup!
        describeMetaGroups.iNumberOfPage = iMaxMetaLevel;
        iMaxMetaLevel++; // add a 1 value for sentence metagroup!
        describeMetaGroups.iNumberOfSentence = iMaxMetaLevel;
        iMaxMetaLevel++; // add a 1 value for timeshift metagroup!
        describeMetaGroups.iNumberOfTimeShift = iMaxMetaLevel;
        bCreateMetaLevelsAbovePages = true;
    }

    private boolean checkIfMetaGroupItemsExists(String groupName)
    {
        boolean ret = false;
        if (groupName == null || groupName.trim().length()==0)
            return false;
        SmilData_ParGroup chapterGroup = metaGroups.get(groupName);
        if (chapterGroup == null)
            return false;
        if(chapterGroup.getListItems().isEmpty())
            return false;
       iMaxMetaLevel++;
       ret = true;
       return ret;
    }

    private void addIntoMetaGroup(String groupName, SmilData_ParGroup groupToAdd, boolean bClearItems)
            throws NullPointerException
    {
        if (groupName == null || groupName.trim().length()==0)
            throw new NullPointerException("groupName is null or empty!");
        if (groupToAdd == null || groupName.trim().length()==0)
            throw new NullPointerException("groupToAdd is null!");
        if (groupToAdd.getListItems().isEmpty())
            throw new NullPointerException("groupToAdd. getListItems is empty!");

        if (groupToAdd != null) {
            SmilData_ParGroup aGroup = metaGroups.get(groupName);
            if (aGroup == null) {
                aGroup = new SmilData_ParGroup();
                aGroup.setStrGroupName(groupName);
                aGroup.setSameGroupNames(true);
            }
            if (bClearItems)
                aGroup.getListItems().clear();
            for (SmilData_Par par : groupToAdd.getListItems())
                aGroup.addSmilDataPar(par);
            metaGroups.put(groupName, aGroup);
        }
    }

    private boolean isLowerThasnSomeOfLevels(List<Integer> pagelevels, int iSmilParLevel)
    {
        boolean ret = false;
        if (pagelevels != null && pagelevels.size() > 0 && iSmilParLevel > 0)
        {
            boolean founded = false;
            for(int iCurrentLevel : pagelevels)
            {
                if (iCurrentLevel > iSmilParLevel)
                {
                    founded = true;
                    ret = founded;
                    break;
                }
            }
        }
        return ret;
    }

    private boolean isGreaterThasnSomeOfLevels(List<Integer> pagelevels, int iSmilParLevel)
    {
        boolean ret = false;
        if (pagelevels != null && pagelevels.size() > 0 && iSmilParLevel > 0)
        {
            boolean founded = false;
            for(int iCurrentLevel : pagelevels)
            {
                if (iCurrentLevel < iSmilParLevel)
                {
                    founded = true;
                    ret = founded;
                    break;
                }
            }
        }
        return ret;
    }

    private SmilData getSmilData(String key, String fname)
    {
        String path = null;
        ReadSmillDataFromFile pair = readSmilData(fname);
        SmilData ret = new SmilData(path, fname, pair,null, null);
        return ret;
    }

    /**
     * This method is reading a smildata file. A reason why this method is here, because it uses
     * hashMapTexts hasmap to get right corresponding link text from html doc and set the text
     * into current SmilData_Par instance.
     *
     * @param m_smilfilename A smil file path
     * @return ReadSmillDataFromFile a data class set its parameters into SmilData instance.
     */
    private ReadSmillDataFromFile readSmilData(String m_smilfilename) {
        // Path path = Paths.get(m_smilfilename);
        boolean bSmilFileNoExists = false;
        Properties props = new Properties();
        List<SmilData_Par> listSmilDataParItems = new ArrayList<>();
        String endsync = null, dur = null, par_id = null,text_src = null, text_id = null;
        String audio_src = null, audio_id = null, clip_begin = null, clip_end = null;
        HashMap<String, SmilData_Par> hashMap = new HashMap<String, SmilData_Par>();
        String m_mp3filename = null;

        File smilFile = new File(m_smilfilename);
        if (!smilFile.exists()) {
            bSmilFileNoExists = true;
            return null;
        }

       // SmilData_ParGroup metaGroup;
        metaGroups = metadataSmilDataPar.getMetagroups();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            db.setEntityResolver(new EntityResolver()
            {
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException
                {
                    return new InputSource(new StringReader(""));
                }
            });

            // Document doc = dBuilder.parse(new InputSource(new StringReader(strXml)));
            Document doc = db.parse(smilFile);
            doc.getDocumentElement().normalize();
        //   System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("meta");

            String name = null;
            String content = null;

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
               // System.out.println("\nCurrent Element: " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;
                    name = elem.getAttribute("name");
                    content = elem.getAttribute("content");
                    if (name != null && content != null)
                        props.put(name, content);
                }
            }

            nList = doc.getElementsByTagName("seq");

            NodeList nListPar = null, nListSeq = null, nListAudio = null, nListText = null;
            Node nNode3 = null, nNode2 = null, nNode = null, nNode2b = null, nNode4 = null;
            Element elem = null, elem2 = null, elem2b = null, elem4 = null;
            SmilData_Par sp = null;
            int iSdParInd = 0;
            SmilIndexLink indexLink;
            List<SmilData_Par_Audio> listSmilDataParAudioItems = new ArrayList<>();
            SmilData_Par_Audio audio;
            String sp_audio_src = "", sp_audio_id = "", sp_clip_begin = "", sp_clip_end = "";

            for (int i = 0; i < nList.getLength(); i++) {
                nNode = nList.item(i);
              //  System.out.println("\nCurrent Element: " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    elem = (Element) nNode;
                    dur = elem.getAttribute("dur");
                    nListPar = elem.getElementsByTagName("par");
                    for (int j = 0; j < nListPar.getLength(); j++) {
                        nNode2 = nListPar.item(j);
                      //  System.out.println("\nCurrent Element: " + nNode2.getNodeName());
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            elem2 = (Element) nNode2;
                            endsync = elem2.getAttribute("endsync");
                            par_id = elem2.getAttribute("id");

                            // handle 1 text block:
                            nListText = elem2.getElementsByTagName("text");
                            for (int jb = 0; jb < nListText.getLength(); jb++) {
                                nNode2b = nListText.item(jb);
                         //       System.out.println("\nCurrent Element: " + nNode2b.getNodeName());
                                if (nNode2b.getNodeType() == Node.ELEMENT_NODE) {
                                    elem2b = (Element) nNode2b;
                                    text_src = elem2b.getAttribute("src");
                                    text_id = elem2b.getAttribute("id");
                                    break;
                                }
                            }

                            int max_audios = 0;

                            listSmilDataParAudioItems = new ArrayList<>();

                            // handle 1 seq block:
                            nListSeq = elem2.getElementsByTagName("seq");
                            for (int k = 0; k < nListSeq.getLength(); k++) {
                                nNode3 = nListSeq.item(k);
                            ///    System.out.println("\nCurrent Element: " + nNode3.getNodeName());
                                if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
                                    Element elem3 = (Element) nNode3;
                                    // handle 1-n items of audio blocks:
                                    nListAudio = elem3.getElementsByTagName("audio");
                                    max_audios = nListAudio.getLength();
                                    for (int l = 0; l < max_audios; l++) {
                                        nNode4 = nListAudio.item(l);
                                    //    System.out.println("\nCurrent Element: " + nNode4.getNodeName());
                                        if (nNode4.getNodeType() == Node.ELEMENT_NODE) {
                                            elem4 = (Element) nNode4;
                                            audio_src = elem4.getAttribute("src");
                                            audio_id = elem4.getAttribute("id");
                                            clip_begin = elem4.getAttribute("clip-begin");
                                            if (l == 0) {
                                                sp_audio_src = audio_src;
                                                sp_audio_id = audio_id;
                                                sp_clip_begin = clip_begin;
                                            }
                                            clip_end = elem4.getAttribute("clip-end");
                                            if (l == (max_audios-1))
                                                sp_clip_end = clip_end;
                                            audio = new SmilData_Par_Audio();
                                            audio.setAudio_id(audio_id);
                                            audio.setAudio_src(audio_src);
                                            audio.setClip_begin(clip_begin);
                                            audio.setClip_end(clip_end);
                                            listSmilDataParAudioItems.add(audio);
                                        }
                                    }
                                }
                            }
                        }

                        sp = new SmilData_Par();
                        iSdParInd = j;
                        sp.setInd(iSdParInd);
                        sp.setSeq_dur(dur);
                        sp.setListSmilDataParAudioItems(listSmilDataParAudioItems);
                        String text = hashMapTexts.get(text_id);
                        sp.setText(text);
                        sp.setAudio_id(sp_audio_id);
                        sp.setAudio_src(sp_audio_src);
                        sp.setClip_begin(sp_clip_begin);
                        sp.setEndsync(endsync);
                        sp.setClip_end(sp_clip_end);
                        if (m_mp3filename == null && audio_src != null) {
                            File fPath = smilFile.getParentFile();
                            m_mp3filename = fPath.getAbsolutePath() +File.separator + audio_src;
                        }
                        sp.setPar_id(par_id);
                        sp.setText_id(text_id);
                        sp.setText_src(text_src);
                        indexLink = hashMapSmilIndexLinks.get(sp.getText_id());
                        if (indexLink != null)
                            sp.setClassName(indexLink.getParentClassName());
                        listSmilDataParItems.add(sp);
                        hashMap.put(text_id, sp);
                    }
                }
            }

            /*
            Node node1 = elem.getElementsByTagName("firstname").item(0);
                    String fname = node1.getTextContent();

                    Node node2 = elem.getElementsByTagName("lastname").item(0);
                    String lname = node2.getTextContent();

                    Node node3 = elem.getElementsByTagName("occupation").item(0);
                    String occup = node3.getTextContent();

                    System.out.printf("User id: %s%n", uid);
                    System.out.printf("First name: %s%n", fname);
                    System.out.printf("Last name: %s%n", lname);
                    System.out.printf("Occupation: %s%n", occup);
                }
            }
            */
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (org.xml.sax.SAXException saxE) {
            saxE.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        boolean bMp3FileNoExists = false;
        File m_mp3File = new File(m_mp3filename);
        if (!m_mp3File.exists())
            bMp3FileNoExists = true;
        ReadSmillDataFromFile pair = new ReadSmillDataFromFile();
        pair.hashMap = hashMap;
        pair.mp3FileName = m_mp3File.getAbsolutePath();
        pair.bSmilFileNoExists = bSmilFileNoExists;
        pair.bMp3FileNoExists = bMp3FileNoExists;
        pair.list = listSmilDataParItems;
        pair.props = props;
        return pair;
    }

    public String getKeyFromSmilHref(String href)
    {
        String ret = null;
        if (href != null && href.trim().length()>0)
        {
            int ind = href.indexOf('#');
            if (ind > -1)
            {
                ret = href.substring(ind +1);
            }
        }
        return ret;
    }

    public String getValueFromSmilHref(String href)
    {
        String ret = null;
        if (href != null && href.trim().length()>0)
        {
            int ind = href.indexOf('#');
            if (ind > -1)
            {
                ret = href.substring(0, ind);
                if (ret.contains("file:")) {
                    ret = ret.replace("file://", "");
                    if (File.separatorChar == '\\' && ret.startsWith("/"))
                        ret = ret.substring(1);
                    try {
                        ret = URLDecoder.decode(ret, "utf-8");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }

    public String getStrDoubleValue(String textid)
    {
        if (textid == null || textid.trim().length() == 0)
            return "-1.0";
        int ind = textid.indexOf("=");
        if (ind > -1)
        {
            return textid.substring(ind +1).replace("s","");
        }
        return textid.replace("s","");
    }

    private String getEspeakLanguage(String speechLanaguage)
    {
        String ret = "" +speechLanaguage +"-" +speechLanaguage;
        return ret;
    }

    private void possibleSpeekText(String text)
    {
        if (text == null || text.trim().length() == 0)
            return;

        if (radioButtonOwnTTS.isSelected())
        {
            Platform.runLater(new Runnable() {
                public void run() {
                    labelLinkMsg.setText(text);
                    //labelLinkMsg.setAccessibleText(text);
                    labelLinkMsg.requestFocus();
                    /*
                    try {
                        Thread.sleep(2000);
                        webView.requestFocus();
                    }catch (Exception e){
                    }<
                     */
                }
            });
            return;
        }

        if ((radioButtonESpeak.isSelected() || radioButtonFreeTts.isSelected())
            && mediaControlPane.isPlaying())
           // mediaControlPane.pauseOrPlay();
            mediaControlPane.pause();

        if (radioButtonESpeak.isSelected())
        {
         //   radioButtonFreeTts.setDisable(false);
            String strPathEnv = System.getenv("PATH");
            try {
                System.out.println("strPathEnv=" +strPathEnv);
                com.harium.hci.espeak.Voice manVoice = new com.harium.hci.espeak.Voice();
                if (speechLanaguage == null || speechLanaguage.trim().length() == 0)
                    manVoice.setName("fi-fi");
                else
                    manVoice.setName(getEspeakLanguage(speechLanaguage));
                manVoice.setAmplitude(100);
                manVoice.setPitch(20);
                manVoice.setSpeed(150);
                // manVoice.setVariant(true, 3);
                com.harium.hci.espeak.Espeak man = new com.harium.hci.espeak.Espeak(manVoice);
                man.speak(text);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    ;
                }
            }catch (Exception espeackEx){
                espeackEx.printStackTrace();
                labelMsg.setText("Espeak error: " +espeackEx.getMessage() );
            }
        }
        else
        if (radioButtonFreeTts.isSelected())
        {
            radioButtonESpeak.setSelected(false);
            try {
                System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
                VoiceManager vm = VoiceManager.getInstance();
                Voice voice = vm.getVoice("kevin16");
                voice.allocate();
                voice.speak(text);
            } catch (Exception freeTTSEx){
                freeTTSEx.printStackTrace();
                labelMsg.setText(freeTTSEx.getMessage());
            }
        }
    }
    public void handleFocusIn(String href, String id, String text)
    {
        System.out.println("handleFocusIn href=" +href +" id=" +id +" text=" +text);
        possibleSpeekText(text);
        /*
        Audio audio = Audio.getInstance();
        InputStream sound = null;
        try {
            sound = audio.getAudio(text, Language.FINNISH);
        } catch (IOException ex) {
            // Logger.getLogger(JavaFX_TextToSpeech.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            labelMsg.setText(ex.getMessage());
        }
        try {
            audio.play(sound);
        } catch (JavaLayerException ex) {
            // Logger.getLogger(JavaFX_TextToSpeech.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            labelMsg.setText(ex.getMessage());
        }
         */
        /*
        voiceReadingThread = new Thread() {
            public void run() {
            }
        };
         */
        // voiceReadingThread.start();
        /*
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelMsg.setText(text);
                labelMsg.setAccessibleHelp(text);
                labelMsg.requestFocus();
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                webView.requestFocus();
            }
        q})        ;
         */
    }

    public void handleLinkClick(String href, String id, String text)
    {
    //    System.out.println("-2- href=" +href +" text=" +text);
        if (m_selectedFile == null /* || (!m_selectedFile.getName().endsWith(".html") && !m_selectedFile.getName().endsWith(".htm")) */)
            return;
        if (bPreventIncreaseArrayIndex.get())
            return;
        bPreventIncreaseArrayIndex.set(true);
        // System.out.println("handleLinkClick href=" +href);
        String key = getKeyFromSmilHref(href);
        String fname = getValueFromSmilHref(href);
        SmilData sm = hashMapSmilData.get(fname);
      //  System.out.println("sm=" +sm.toString());
        m_selectedFile = sm.getMp3File();
        double beginClip = -1.0; // undef value
        SmilData_Par sdpar = sm.getSmilData_ParOfKey(key);
        if (sdpar != null)
        {
            String strbeginClip = sdpar.getClip_begin();
            if (strbeginClip != null) {
                try {
                    beginClip = Double.parseDouble(getStrDoubleValue(strbeginClip));
                    String mp3FileName = sdpar.getAudio_src();
                    if (mp3FileName != null && m_selectedFile != null)
                    {
                        if (!mp3FileName.equals(m_selectedFile.getName()))
                            m_selectedFile = new File(m_selectedFile.getParentFile().getAbsolutePath() +File.separator +mp3FileName);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        int indArr = getIndArrOfListMp3Files(m_selectedFile);
        iIndArrDirFiles = indArr;
       // mediaControl.setIndArrDirFiles(indArr);
        bPreventIncreaseArrayIndex.set(false);
        if (iIndArrDirFiles > -1 && bWebViewLoaded)
        {
            scrollIntoWebViewLink(key);
            labelMsg.setText(text);
         //  labelMsg.setAccessibleHelp(text);
        }

        mediaControlPane.playThisFile(m_selectedFile, beginClip, iIndArrDirFiles, arrAudioFiles != null ? arrAudioFiles.length : 0, text);
    }

    private int getIndArrOfListMp3Files(File selectedFile)
    {
        int ret = 0;
        if (selectedFile != null && listMp3Files != null && listMp3Files.size() > 0)
        {
            int max = listMp3Files.size();
            File [] arrFiles = new File[max];
            arrFiles = listMp3Files.toArray(arrFiles);
            int ind = -1;
            boolean bFound = false;
            for(File f : arrFiles)
            {
                ind++;
                if (f.getAbsolutePath().equals(selectedFile.getAbsolutePath()))
                {
                    bFound = true;
                    break;
                }
            }
            if (!bFound)
                ind = 0;
            ret = ind;
        }

        return ret;
    }

    public void handleKeyEvent(KeyEvent event)
    {
        KeyCode keyCode = event.getCode();
        switch (keyCode) {
            case PLUS:    mediaControlPane.setPlayLouderVolume(); break;
            case MINUS:  mediaControlPane.setPlayLowerVolume(); break;
            case P:  mediaControlPane.pauseOrPlay(); break;
            case DIGIT8:
                mediaControlPane.play10sMiinus(); break;
            case DIGIT9:
                mediaControlPane.play10sPlus(); break;
            case DIGIT5:
                if (!buttonPreviousTrack.isDisabled())
                    pressedButtonPreviousTrack(); break;
            case DIGIT6:
                if (!buttonNextTrack.isDisabled())
                    pressedButtonNextTrack(); break;
            case DIGIT2:
                if (!buttonNextLink.isDisabled())
                    pressedButtonPrevLink(); break;
            case DIGIT3:
                if (!buttonNextLink.isDisabled())
                    pressedButtonNextLink(); break;
            case Q:
                if (!buttonUpperLevel.isDisabled())
                    pressedButtonUpperLevel(); break;
            case W:
                if (!buttonLowerLevel.isDisabled())
                    pressedButtonLowerLevel(); break;
            case R:
                if (!buttonPrevLevelLink.isDisabled())
                    pressedButtonPrevLevelLink(); break;
            case T:
                if (!buttonNextLevelLink.isDisabled())
                    pressedButtonNextLevelLink();
                break;
            case F1:
                handleF1HelpPressed(event);
                break;
//            case PAGE_UP:
//                pressedButtonUpperLevel();
            case DIGIT1:
                pressedButtonLowerLevel();
                break;
            case I:
                if (event.isAltDown())
                {
                    if (isFocusNotInWebView) {
                        webView.requestFocus();
                        isFocusNotInWebView = false;
                    } else {
                        buttonPlay.requestFocus();
                        isFocusNotInWebView = true;
                    }
                }
                    break;
                /*
            case C:
                if (event.isAltDown())
                {
                    if (stageHelp.isShowing())
                        stageHelp.close();
                }
                break;
                */
               // case RIGHT: goEast  = true; break;
               // case SHIFT: running = true; break;
            }
    }

    @FXML
    private void selectedFiSpecificHelp(ActionEvent event)
    {
        Object nodeObj = event.getSource();
        if (nodeObj instanceof MenuItem) {
            MenuItem source = (MenuItem)nodeObj;
            String id = source.getId();
            if (id != null){
                helpController.scrollInto(id);
                openHelpCtrl();
            }
        }
    }

    private void handleF1HelpPressed(KeyEvent event)
    {
        Object obj = event.getSource();
        Scene scene = (Scene) obj;
        Object nodeObj = scene.focusOwnerProperty().get();
        if (nodeObj instanceof Control) {
            Control source = (Control)nodeObj;
            String id = source.getId();
            if (id != null){
                helpController.scrollInto(id);
               // buttonHelp.setDisable(true);
                openHelpCtrl();
              //  buttonHelp.setDisable(false);
            }
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        labelSeleectedFile.setText("Welcome to JavaFX Application!");
    }

    public SmilData_Par getLeveledParItem(SmilData_Par d_par, SmilData parent)
    {
        // TDOO: implementation!
        boolean bValueNotSet = true;
        SmilIndexLink indexLink = hashMapSmilIndexLinks.get(d_par.getText_id());
        if (indexLink != null) {
            String strAttrClass = indexLink.getParentClassName();
            String strParentNodeElementName = indexLink.getParnentElelmentName();
            if (strParentNodeElementName != null
                    && hashMapLevelHeaderWords.containsKey(strParentNodeElementName)) {
                Integer level = hashMapLevelHeaderWords.get(strParentNodeElementName);
                if (level != null) {
                    d_par.setSmilParLevel(level.intValue());
                    bValueNotSet = false;
                }
            }

            if (bValueNotSet) {
                strParentNodeElementName = parent.getParentNodeElementName();
                if (strParentNodeElementName != null
                        && hashMapLevelHeaderWords.containsKey(strParentNodeElementName)) {
                    Integer level = hashMapLevelHeaderWords.get(strParentNodeElementName);
                    if (level != null) {
                        int newvalue = level.intValue()+1;
                        if (newvalue > iMaxSmilParLevel)
                            iMaxSmilParLevel = newvalue;
                        if (newvalue > iMaxSmilParLevelNormalPage && strAttrClass != null
                            && strAttrClass.equals("page-normal"))
                            iMaxSmilParLevelNormalPage = newvalue;
                        d_par.setSmilParLevel(newvalue);
                        bValueNotSet = false;
                    }
                }
                if (bValueNotSet)
                {
                    System.out.println("No par item level name and either parent element name!");
                }
            }

            if (bValueNotSet && strAttrClass != null) {
                Integer level = hashMapLevelWords.get(strAttrClass);
                if (level != null) {
                    int newvalue = level.intValue();
                    if (newvalue > iMaxSmilParLevel)
                        iMaxSmilParLevel = newvalue;
                    d_par.setSmilParLevel(newvalue);
                }
            }
        }

        // allSmilData_ParItems.add(sp);
        SmilData_ParGroup metaGroup = metaGroups.get(d_par.getClassName());
        if (d_par.getClassName() != null ) {
            if (metaGroup == null)
                metaGroup = new SmilData_ParGroup();
            metaGroup.addSmilDataPar(d_par);
            metaGroups.put(d_par.getClassName(), metaGroup);
        }
        else
            System.out.println("a SmilDataPar has no classname: " +d_par.getPar_id());
        return d_par;
    }
}