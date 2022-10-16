package com.metait.javafxplayer.bookmark;

import java.io.File;
import java.util.Date;

public class BookMark {
    public static final String cnstBookMark_name = "name";
    public static final String cnstBookMark_dirpath = "dirpath";
    public static final String cnstBookMark_playpath = "playpath";
    public static final String cnstBookMark_playdirindex = "playdirindex";
    public static final String cnstBookMark_position = "position";
    public static final String cnstBookMark_type = "type";

    private String strName = "";
    private String strPlayFilePath = "";
    private String strSelectedDirPath = "";
    private double dBookMarkPosition = 0.0;
    private Date bookMarkDate = null;
    private String strDaisyBookIndexPath = "";
    private String strIndArrDirFiles = "";

    private String strType = "";
    private File fAudioFile = null;
    private File daisyFile = null;
    private File fDirFile = null;

    public String getName() {
        return strName;
    }

    public void setName(String strName) {
        this.strName = strName;
    }

    public String getPlayfilepath() {
        return strPlayFilePath;
    }

    public void setPlayfilepath(String strPlayFilePath) {
        this.strPlayFilePath = strPlayFilePath;
    }

    public String getDirpath() {
        return strSelectedDirPath;
    }

    public void setDirpath(String strDirPath) {
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
        return "bookmark strName=" +strName +" strPlayFilePath=" + strPlayFilePath +"\n"
                +" strSelectedDirPath=" + strSelectedDirPath +"\n"
                +" strDaisyBookIndexPath=" +strDaisyBookIndexPath +"\n"
                +" strIndArrDirFiles=" +strIndArrDirFiles +"\n"
                +" dBookMarkPosition=" +dBookMarkPosition +"\n"
                +" dBookMarkType=" +strType +"\n"
                +" bookMarkDate=" +bookMarkDate;
    }

    public String getDaisybookindexpath() {
        return strDaisyBookIndexPath;
    }

    public void setDaisybookindexpath(String strDaisyBookIndexPath) {
        this.strDaisyBookIndexPath = strDaisyBookIndexPath;
    }

    public String getIndarrdirfiles() {
        return strIndArrDirFiles;
    }

    public void setIndarrdirfiles(String strIndArrDirFiles) {
        this.strIndArrDirFiles = strIndArrDirFiles;
    }
}
