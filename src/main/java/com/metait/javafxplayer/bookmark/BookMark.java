package com.metait.javafxplayer.bookmark;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookMark {
    public static final String cnstBookMark_name = "name";
    public static final String cnstBookMark_dirpath = "dirpath";
    public static final String cnstBookMark_playpath = "playpath";
    public static final String cnstBookMark_playdirindex = "playdirindex";
    public static final String cnstBookMark_position = "position";
    public static final String cnstBookMark_type = "type";
    public static final String cnstBookMark_description = "description";

    private String strName = "";
    private String strPlayFilePath = "";
    private String strSelectedDirPath = "";
    private double dBookMarkPosition = 0.0;
    private Date bookMarkDate = null;
    private String strDaisyBookIndexPath = "";
    private String strIndArrDirFiles = "";

    public String getStrDescription() {
        return strDescription;
    }

    public void setStrDescription(String strDescription) {
        this.strDescription = strDescription;
    }

    private String strDescription = "";

    private String strType = "";
    private File fAudioFile = null;
    private File daisyFile = null;
    private File fDirFile = null;

    public BookMark() {
        bookMarkDate = Calendar.getInstance().getTime();
    }
    public String getName() {
        return strName;
    }

    public void setName(String strName) {
        this.strName = strName;
    }

    public String getPlayFilePath() {
        return strPlayFilePath;
    }

    public void setPlayFilePath(String strPlayFilePath) {
        this.strPlayFilePath = strPlayFilePath;
    }

    public String getDirPath() {
        return strSelectedDirPath;
    }

    public void setDirPath(String strDirPath) {
        this.strSelectedDirPath = strDirPath;
    }
    public String getType() {
        return strType;
    }
    public void setType(String strType) {
        this.strType = strType;
    }

    public File getDirfile() {
        return fDirFile;
    }

    public File getAudiofile() {
        return fAudioFile;
    }

    public double getPosition() {
        return dBookMarkPosition;
    }

    public void setPosition(double dBookMarkPosition) {
        this.dBookMarkPosition = dBookMarkPosition;
    }

    public Date getBookMarkDate() {
        return bookMarkDate;
    }

    public void setBookMarkDate(Date bookMarkDate) {
        this.bookMarkDate = bookMarkDate;
    }

    public String toString() {
     /*        return "bookmark strName=" +strName +" strPlayFilePath=" + strPlayFilePath +"\n"
                +" strSelectedDirPath=" + strSelectedDirPath +"\n"
                +" strDaisyBookIndexPath=" +strDaisyBookIndexPath +"\n"
                +" strIndArrDirFiles=" +strIndArrDirFiles +"\n"
                +" dBookMarkPosition=" +dBookMarkPosition +"\n"
                +" dBookMarkType=" +strType +"\n"
                +" bookMarkDate=" +bookMarkDate;
      */
        return strName +" " +getDateString() +" " +strDescription +" " +dBookMarkPosition;
    }

    private String getDateString()
    {
        // DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String strDate= formatter.format(bookMarkDate);
        return strDate;
    }

    public String getDaisyBookIndexPath() {
        return strDaisyBookIndexPath;
    }

    public void setDaisyBookIndexPath(String strDaisyBookIndexPath) {
        this.strDaisyBookIndexPath = strDaisyBookIndexPath;
    }

    public String getIndArrDirFiles() {
        return strIndArrDirFiles;
    }

    public void setIndArrDirFiles(String strIndArrDirFiles) {
        this.strIndArrDirFiles = strIndArrDirFiles;
    }
}
