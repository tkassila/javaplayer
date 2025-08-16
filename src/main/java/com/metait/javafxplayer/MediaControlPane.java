package com.metait.javafxplayer;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.AccessibleRole;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;

import java.io.File;
import java.util.HashMap;

public class MediaControlPane extends BorderPane {

    private MediaPlayer mp;
    private MediaView mediaView;
    private boolean bRepeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private /* HBox */ FlowPane mediaBar;
    private Media buzzer;
    private final Button playButton = new Button(">");
    private final Button prev10sButton = new Button("< 10s");
    private final Button next10sButton = new Button("10s >");
    private Label centerLabel = new Label("Open a file!");
    private File m_selectedFile = null;
    private File currentPlayFile = null;
    private File m_selectedDir = null;
   // private File [] arrDirFiles = null;
    // private ExtensionsFilter m_audiofilter = null;
    private int iIndArrDirFiles = 0;
    private int iArrDirFiles = 0;
    private Label spacer = null;
    private boolean bMetaData = false;
    private IFileContainer m_parent = null;
    private final double iHeigth;
    private final double iWidth;
    private double volume = 0.5;
    // private Label labelCounter = new Label();
    private volatile Duration currentTime = null;
    private volatile int iSecondsIncurrentTime = 0;
    private volatile HashMap<Integer,String> hashMapSeconds = new HashMap<>();
  //  private List<BookMarkCollection> m_bookMarkCollections = new ArrayList<>();
    private EventHandler playButtonEventHandler = null;
    private EventHandler prev10sButtonEventHandler = null;
    private EventHandler next10sButtonEventHandler = null;
    private Label labelClicked = new Label();

    private String strVolume_ui;
    private String strTime_u;
    private String strMediaPane_1;
    private String strMediaPane_2;
    private String strMediaPane_3;
    private String strMediaPane_4;
    private String strMediaPane_5;
    private String strMediaPane_6;
    private String strMediaPane_7;
    private String strMediaPane_8;
    private ICallParentOnEvery10Secs sec10Listener = null;
    public int getTimeHop() {
        return iTimeHop;
    }

    public void add10SecListener(ICallParentOnEvery10Secs par_sec10Listener)
    {
        sec10Listener = par_sec10Listener;
    }

    public void setTimeHop(int iTimeHop) {
        this.iTimeHop = iTimeHop;
        prev10sButton.setText("< " +(iTimeHop / 1000) +"s");
        next10sButton.setText("" +(iTimeHop / 1000) +"s >");
    }

    private int iTimeHop = 10000;

    public double getStopAndCurrentTime()
    {
        if (currentTime == null)
            return 0.0;
        if (currentTime.isUnknown())
                return 0.0;
        if (mp.getStatus() == MediaPlayer.Status.UNKNOWN || mp.getStatus() == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return -1.0;
        }

    //    if (mp.getStatus() == MediaPlayer.Status.PLAYING)
      //      mp.stop();
        return currentTime.toMillis();
    }

    public double getCurrentTime()
    {
        if (currentTime == null)
            return 0.0;
        if (currentTime.isUnknown())
            return 0.0;
        if (mp.getStatus() == MediaPlayer.Status.UNKNOWN || mp.getStatus() == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return -1.0;
        }

        //    if (mp.getStatus() == MediaPlayer.Status.PLAYING)
        //      mp.stop();
        return currentTime.toMillis();
    }

    public double getDuration() {
        if (duration == null)
            return 0.0;
        if (duration.isUnknown())
            return 0.0;
        return duration.toMillis();
    }

    public File getPlayFile() { return m_selectedFile; }
    // public void setExtensionFilter(ExtensionsFilter p_audiofilter) { m_audiofilter = p_audiofilter; }
    public boolean isMetaData() { return bMetaData; }

    private void disAbleUserControls(boolean bValue)
    {
        next10sButton.setDisable(bValue);
        prev10sButton.setDisable(bValue);
        playButton.setDisable(bValue);
        volumeSlider.setDisable(bValue);
        timeSlider.setDisable(bValue);
    }

    public MediaControlPane(IFileContainer p_parent, String p_volume_ui, String p_strTime_ui,
                            String p_strMediaPane_1, String p_strMediaPane_2, String p_strMediaPane_3,
                            String p_strMediaPane_4, String p_strMediaPane_5, String p_strMediaPane_6,
                            String p_strMediaPane_7, String p_strMediaPane_8) {

        strTime_u = p_strTime_ui;
        strVolume_ui = p_volume_ui;
        strMediaPane_1 = p_strMediaPane_1;
        strMediaPane_2 = p_strMediaPane_2;
        strMediaPane_3 = p_strMediaPane_3;
        strMediaPane_4 = p_strMediaPane_4;
        strMediaPane_5 = p_strMediaPane_5;
        strMediaPane_6 = p_strMediaPane_6;
        strMediaPane_7 = p_strMediaPane_7;
        strMediaPane_8 = p_strMediaPane_8;
        m_parent = p_parent;
        setStyle("-fx-background-color: #bfc2c7;");
        mediaView = new MediaView();
        Pane mvPane = new Pane() {
        };
        centerLabel.setPadding(new Insets(5, 10, 5, 10));
        centerLabel.setAccessibleHelp(strMediaPane_1);
        centerLabel.setAccessibleRole(AccessibleRole.TEXT);
        centerLabel.setFocusTraversable(true);
        mediaView.setAccessibleHelp(strMediaPane_2);
        mediaView.setAccessibleRole(AccessibleRole.NODE);

        playButton.setAccessibleRole(AccessibleRole.BUTTON);
        prev10sButton.setAccessibleRole(AccessibleRole.BUTTON);
        next10sButton.setAccessibleRole(AccessibleRole.BUTTON);
        centerLabel.setAccessibleRole(AccessibleRole.TEXT);

        prev10sButton.setAccessibleHelp(strMediaPane_3);
        next10sButton.setAccessibleRoleDescription(prev10sButton.getAccessibleHelp());
        next10sButton.setAccessibleHelp(strMediaPane_4);
        next10sButton.setAccessibleRoleDescription(next10sButton.getAccessibleHelp());

        playButton.setAccessibleHelp(strMediaPane_5);
        playButton.setAccessibleRoleDescription(playButton.getAccessibleHelp());

        // prev10sButton.setAccessibleRole(AccessibleRole.NODE);

        mvPane.getChildren().add(mediaView);
        mvPane.getChildren().add(centerLabel);
        mvPane.getChildren().add(labelClicked);
        mvPane.setStyle("-fx-background-color: black;");
        mvPane.setStyle("-fx-foreground-color: yellow;");
        setCenter(mvPane);
       // centerLabel.setEditable(false);

        mediaBar = new /* HBox */ FlowPane();
        /*
        mediaBar.setVgap(5);
        mediaBar.setHgap(3);
         */
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);

        prev10sButtonEventHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                setBackwardAfterMilliSecs(iTimeHop);
                /*
                MediaPlayer.Status status = mp.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                mp.pause();
                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    } else {
                        Duration currTime = mp.getCurrentTime();
                        mp.seek(currTime.subtract(Duration.millis(10000)));
                    }
                    mp.play();
                } else {
                    mp.pause();
                    if (status == MediaPlayer.Status.PAUSED
                            || status == MediaPlayer.Status.READY
                            || status == MediaPlayer.Status.STOPPED) {
                        // rewind the movie if we're sitting at the end
                        if (atEndOfMedia) {
                            mp.seek(mp.getStartTime());
                            atEndOfMedia = false;
                        } else {
                            Duration currTime = mp.getCurrentTime();
                            mp.seek(currTime.subtract(Duration.millis(10000)));
                        }
                        mp.play();
                    }
                }
                 */
            }
        };

        prev10sButton.setOnAction(prev10sButtonEventHandler);

        next10sButtonEventHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
               setForwardAfterMilliSecs(iTimeHop);
              /*
                MediaPlayer.Status status = mp.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    else
                    {
                        Duration currTime = mp.getCurrentTime();
                        mp.seek(currTime.add(Duration.millis(10000)));
                    }
                    mp.play();
                } else {
                    mp.pause();
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    else
                    {
                        Duration currTime = mp.getCurrentTime();
                        mp.seek(currTime.add(Duration.millis(10000)));
                    }
                    mp.play();
                }
                 */
            }
        };

        next10sButton.setOnAction(next10sButtonEventHandler);

        playButtonEventHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MediaPlayer.Status status = mp.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    mp.play();
                } else {
                    mp.pause();
                }
            }
        };

        playButton.setOnAction(playButtonEventHandler);

        mediaBar.getChildren().add(playButton);
        mediaBar.getChildren().add(prev10sButton);
        mediaBar.getChildren().add(next10sButton);

        // Add spacer
        spacer = new Label("  ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label(strTime_u +": ");
        timeLabel.setFocusTraversable(true);
        timeLabel.setLabelFor(timeSlider);
        timeLabel.setAccessibleRole(AccessibleRole.TEXT);
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        timeLabel.setLabelFor(timeSlider);
        timeSlider.setAccessibleHelp(strMediaPane_6);

        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setAccessibleRole(AccessibleRole.TEXT);
        playTime.setFocusTraversable(true);
        playTime.setAccessibleHelp(strMediaPane_7);
        timeLabel.setLabelFor(playTime);
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // mediaBar.getChildren().add(labelCounter);
        // Add the volume label
        Label volumeLabel = new Label(strVolume_ui +": ");
        volumeLabel.setFocusTraversable(true);
        volumeLabel.setLabelFor(volumeSlider);

        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setAccessibleHelp(strMediaPane_8);
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    volume = volumeSlider.getValue() / 100.0;
                    mp.setVolume(volume);
                }
            }
        });
        mediaBar.getChildren().add(volumeSlider);

        playButton.defaultButtonProperty().bind(playButton.focusedProperty());
        prev10sButton.defaultButtonProperty().bind(prev10sButton.focusedProperty());
        next10sButton.defaultButtonProperty().bind(next10sButton.focusedProperty());

        playButton.setId("playbutton");
        prev10sButton.setId("prev10sButton");
        next10sButton.setId("next10sButton");
        timeSlider.setId("timeSlider");
        volumeSlider.setId("volumeSlider");
        playTime.setId("playTime");
        centerLabel.setId("centerLabel");

        setBottom(mediaBar);

        disAbleUserControls(true);

        iHeigth = getHeight();
        iWidth = getWidth();
    }

    public void setForwardAfterMilliSecs(int msecs)
    {
        setMilliSecsForPlay(msecs, PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION);
    }

    public void setBackwardAfterMilliSecs(int msecs)
    {
        setMilliSecsForPlay(msecs, PAR_LEVEL_DIRECTION.UPWARD_PAR_LEVEL_DIRECTION);
    }

    enum PAR_LEVEL_DIRECTION { UPWARD_PAR_LEVEL_DIRECTION, DOWNWARD_PAR_LEVEL_DIRECTION  };

    private void setMilliSecsForPlay(int msecs, PAR_LEVEL_DIRECTION direction)
    {
       MediaPlayer.Status status = mp.getStatus();

        if (msecs < 1 || msecs >= this.duration.toMillis())
            return;
        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return;
        }

        if (status == MediaPlayer.Status.PAUSED
                || status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.STOPPED) {
            // rewind the movie if we're sitting at the end
            if (atEndOfMedia) {
                mp.seek(mp.getStartTime());
                atEndOfMedia = false;
            }
            else
            {
                Duration currTime = mp.getCurrentTime();
                System.out.println("currTime=" +currTime.toMillis());
                Duration newPos;
                if (direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION)
                    newPos = currTime.add(Duration.millis(msecs));
                else {
                    newPos = currTime.subtract(Duration.millis(msecs));
                }
                mp.seek(newPos);
                mp.setStartTime(newPos);
            }
            mp.play();
        } else {
            mp.pause();
            if (atEndOfMedia) {
                mp.seek(mp.getStartTime());
                atEndOfMedia = false;
            }
            else
            {
                Duration currTime = mp.getCurrentTime();
                System.out.println("currTime=" +currTime.toMillis());
                Duration newPos = null;
                if (direction == PAR_LEVEL_DIRECTION.DOWNWARD_PAR_LEVEL_DIRECTION)
                    newPos = currTime.add(Duration.millis(msecs));
                else
                    newPos = currTime.subtract(Duration.millis(msecs));
                mp.seek(newPos);
                mp.setStartTime(newPos);
            }
            mp.play();
        }
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mp.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mp.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    /*
    private void playTheseMp3Files(File [] arrMp3Files)
    {
        if (arrMp3Files == null || arrMp3Files.length == 0)
            return;
        if(mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING){
            mp.stop();
        }

        // arrDirFiles = arrMp3Files;
        // iIndArrDirFiles = 0;
      //  currentPlayFile = arrDirFiles[iIndArrDirFiles];
        if (currentPlayFile != null)
            playThisFile(currentPlayFile, -1.0, iIndArrDirFiles, a.length);
    }
     */

    /*
    private void playDirectory(File selectedFile)
    {
        if (!selectedFile.isDirectory())
            return;
        if(mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING){
            mp.stop();
        }
        m_selectedDir = selectedFile;
        // arrDirFiles = selectedFile.listFiles(m_audiofilter);
      //  if (arrDirFiles == null || arrDirFiles.length == 0)
        //    return;
       // iIndArrDirFiles = 0;
      //  currentPlayFile = arrDirFiles[iIndArrDirFiles];
        if (currentPlayFile != null)
            playThisFile(currentPlayFile, -1.0);
    }
    */

    public void playFile(File selectedFile) {
        iIndArrDirFiles = 0;
        iArrDirFiles = 1;
        playThisFile(selectedFile, -1.0, 0, 1, "");
    }

    /*
    private void updateLabelCounter()
    {
        Platform.runLater(new Runnable() {
            public void run() {
                labelCounter.setText(" " +(iIndArrDirFiles+1) +" ");
            }
        });
    }
     */

    /*
    @Override
    public void bindViewSizeToParent() {
        mediaView.fitHeightProperty().bind(container.heightProperty());
        mediaView.fitWidthProperty().bind(container.widthProperty());
    }
     */

    public void stop() {
        if(mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING){
            mp.stop();
        }
    }

    public void playThisFile(File selectedFile, double beginClip,
                             int p_iIndArrDirFiles, int iArraySize, String clickedText) {
        if(mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING) {
            mp.stop();
            try {
                Thread.sleep(500);
            } catch (Exception e){
            }
        }
        else
        {
            labelClicked.setText(clickedText);
        }
        setHeight(iHeigth);
        setWidth(iWidth);

        m_selectedFile = selectedFile;
        iIndArrDirFiles = p_iIndArrDirFiles;
        iArrDirFiles = iArraySize;
        stopRequested = false;
        playButton.setText("   " +">" +"   ");

        if (selectedFile == null)
            return;

        String strResource = selectedFile.getAbsolutePath(); // getClass().getResource(selectedFile.getAbsolutePath()).toExternalForm();
        // strResource = selectedFile.toURI().toString();
        try {
            strResource = selectedFile.toURI().toString();
            buzzer = new Media(strResource);
            mp = new MediaPlayer(buzzer);
            mp.setAutoPlay(true);

            // updateLabelCounter();
            //Get the juicy metadata... when it arrives. It's not immediate :\
            ObservableMap<String, Object> meta = buzzer.getMetadata();
            //So we have to register a listener for that map
				/* The awful MapChangeListener.Change bit is to make chg non-ambiguous
				because it could be an InvalidationListener obj instead. */
            bMetaData = false;
            meta.addListener((MapChangeListener.Change<? extends String, ? extends Object> chg) -> {
                StringBuilder labelTxt = new StringBuilder();

                //No guarantees on useful order.
                for(String key : meta.keySet()) {
                    if (key != null && !key.contains("raw metadata")) {
                        bMetaData = true;
                        labelTxt.append(key);
                        labelTxt.append(": ");
                        labelTxt.append(meta.get(key));
                        labelTxt.append(System.lineSeparator());
                    }
                }

                String strCounter = "" +(iIndArrDirFiles+1) +"/" +iArrDirFiles +"" +System.lineSeparator();
              //  Text wd = new Text(strCounter +labelTxt.toString());
              //  wd.setFont(Font.font ("Verdana", FontWeight.BOLD, 12));
                String strvalue = strCounter +labelTxt.toString();
                centerLabel.setText(strvalue);
                centerLabel.setFont(Font.font("Amble CN", FontWeight.BOLD, 12));
                m_parent.callUpdateSplitPaneDividerPosition(true, strvalue);
            });

            if (!bMetaData)
            {
                String strCounter = "" +(iIndArrDirFiles+1) +"/" +iArrDirFiles +""
                        +System.lineSeparator();
                centerLabel.setText(strCounter +" "+selectedFile.getName() +System.lineSeparator());
                centerLabel.setFont(Font.font("Amble CN", FontWeight.BOLD, 12));
            }

            /*
            String title = (String)mp.getMetadata().get("title");
            String album = (String)mp.getMetadata().get("album");
            String artist = (String)mp.getMetadata().get("artist");
             */
            initMpAfterNewMp();
           // System.out.println(mp.getStatus().toString());
            mediaView.setMediaPlayer(mp);

            //change width and height to fit video
            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();
            width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
            mediaView.setPreserveRatio(!m_parent.isVideoFile(selectedFile));

            if (beginClip > 0.0)
            {
                // mp.setAutoPlay(false);
                mp.setStartTime(Duration.millis((beginClip*1000)));
            }
            if (volume > 0.0)
            {
                mp.setVolume(volume);
                volumeSlider.setValue(volume);
            }
            if(mp.isAutoPlay() && mp.getStatus() != MediaPlayer.Status.PLAYING){
//                if (beginClip > 0.0)
                mp.play();
            }
            disAbleUserControls(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setIndArrDirFiles(int ind)
    {
        if (ind < 0)
            return;
    //   if (arrDirFiles == null || arrDirFiles.length < 1 || arrDirFiles.length < ind)
      //      return;
      //  iIndArrDirFiles = ind;
    }

    private void possibleNextTrack()
    {
        NewSelectedFile returndata = m_parent.getPossibleNextPlayfile();
        if (returndata == null)
            return;
        currentPlayFile = returndata.newSelectedfile;
        iIndArrDirFiles = returndata.iCounter;
        iArrDirFiles = returndata.iSize;
        if (currentPlayFile != null)
            playThisFile(currentPlayFile, -1.0, iIndArrDirFiles, iArrDirFiles, "");
        /*
        if (arrDirFiles == null || arrDirFiles.length == 0 || arrDirFiles.length <= iIndArrDirFiles)
            return -1;
        int iIndArrDirFiles2 = iIndArrDirFiles +1;
        if (arrDirFiles.length <= iIndArrDirFiles2)
            return -1;
        iIndArrDirFiles = iIndArrDirFiles2;
        currentPlayFile = arrDirFiles[iIndArrDirFiles];
        if (currentPlayFile == null)
            return -1;
       // labelCounter.setText(" " +(iIndArrDirFiles+1) +" ");
         */
      //  playThisFile(currentPlayFile, -1.0);
        // iIndArrDirFiles;
    }

    public int possiblePreviousTrack()
    {
        if (m_selectedFile == null)
            return -1;
        /*
        if (arrDirFiles == null || arrDirFiles.length == 0 || arrDirFiles.length <= (iIndArrDirFiles-1))
            return -1;
        if (iIndArrDirFiles == 0)
            return -1;
        int iIndArrDirFiles2 = iIndArrDirFiles -1;
        if (0 > iIndArrDirFiles2)
            return -1;
        if (arrDirFiles.length <= iIndArrDirFiles2)
            return -1;
        iIndArrDirFiles = iIndArrDirFiles2;
        currentPlayFile = arrDirFiles[iIndArrDirFiles];
        if (currentPlayFile == null)
            return -1;
        // labelCounter.setText(" " +(iIndArrDirFiles+1) +" ");
        playThisFile(currentPlayFile, -1.0);
        return iIndArrDirFiles;
         */
        return -1;
    }

    private void initMpAfterNewMp()
    {
        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                //of stopping the double commit.
                currentTime = newValue;
                iSecondsIncurrentTime = new Double(currentTime.toSeconds()).intValue();
              //  System.out.println("" +iSeconsdIncurrentTime);
                if (iSecondsIncurrentTime % 10 == 0 && hashMapSeconds.get(new Integer(iSecondsIncurrentTime)) == null)
                {
                    hashMapSeconds.clear();
                    hashMapSeconds.put(iSecondsIncurrentTime, new Integer(iSecondsIncurrentTime).toString());
                    // System.out.println("-> " + iSecondsIncurrentTime);
                    sec10Listener.callParentOnEvery10Secs(currentTime.toMillis(), iSecondsIncurrentTime,
                       mp.getMedia().getSource());
                }
            }
        });

        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                    playButton.setText("   " +"||" +"   ");
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run() {
          //     System.out.println("onPaused");
                playButton.setText("   " +">" +"   ");
            }
        });

        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setCycleCount(bRepeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!bRepeat) {
                    playButton.setText("   " +">" +"  ");
                    stopRequested = true;
                    atEndOfMedia = true;
                    possibleNextTrack();
                }
            }
        });
    }

    public void setPlayLouderVolume()
    {
        double bVolume = volumeSlider.getValue();
        if (bVolume >= 100.0)
            return; // is now max volume
  //      if (volumeSlider.isValueChanging()) {
        double bVolume2 = ((10.0 * bVolume) / 100.0) +bVolume;
        if (bVolume2 >= 100.0)
            bVolume2 = ((5.0 * bVolume) / 100.0) +bVolume;
        if (bVolume2 >= 100.0)
            return; // is now max volume
        volume = bVolume2 / 100.0;
       mp.setVolume(volume);
    }

    public void setPlayLowerVolume()
    {
        double bVolume = volumeSlider.getValue();
        if (bVolume <= 0.0)
            return; // is now min volume
        double bVolume2 =  bVolume - ((10.0 * bVolume) / 100.0);
        if (bVolume2 <= 0.0)
            bVolume2 = bVolume - ((5.0 * bVolume) / 100.0);
        if (bVolume2 <= 0.0)
            return; // is now max volume
        volume = bVolume2 / 100.0;
        mp.setVolume(volume);
    }

    public void play10sMiinus()
    {
        if (prev10sButtonEventHandler != null)
            prev10sButtonEventHandler.handle(new ActionEvent());
    }

    public void play10sPlus()
    {
        if (next10sButtonEventHandler != null)
        next10sButtonEventHandler.handle(new ActionEvent());
    }

    public void pauseOrPlay()
    {
        playButtonEventHandler.handle(new ActionEvent());
    }


    public boolean isPlaying()
    {
        boolean ret = false;
        if (mp != null)
        {
            Status playerStatus = mp.getStatus();
            if (playerStatus == null || playerStatus == Status.UNKNOWN
               || playerStatus == Status.READY || playerStatus == Status.PAUSED
               || playerStatus == Status.STOPPED || playerStatus == Status.DISPOSED
                    || playerStatus == Status.STALLED)
                return false;
          //  if (playerStatus == Status.PLAYING)
                return true;
        }
        return ret;
    }

    public void pause()
    {
        MediaPlayer.Status status = mp.getStatus();

        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return;
        }

        if (!(status == MediaPlayer.Status.PAUSED
                || status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.STOPPED)) {
            // rewind the movie if we're sitting at the end
            if (atEndOfMedia) {
                mp.seek(mp.getStartTime());
                atEndOfMedia = false;
            }
            else
                mp.pause();
        }
    }
}