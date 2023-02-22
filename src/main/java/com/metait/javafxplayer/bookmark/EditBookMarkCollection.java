package com.metait.javafxplayer.bookmark;

import java.io.File;

public class EditBookMarkCollection
{
    private BookMark [] bookMarks = null;
    private String strName = "";
    private String strPlayFilePath = "";
    private String strSelectedDirPath = "";

    private File fAudioFile = null;
    private File daisyFile = null;
    private File fDirFile = null;

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

    public File getDirFile() {
        return fDirFile;
    }

    public File getAudioFile() {
        return fAudioFile;
    }

    public String toString() {
        return "bookmark strName=" +strName +" strPlayFilePath=" + strPlayFilePath +"\n"
                +" strSelectedDirPath=" + strSelectedDirPath +"\n";
    }

    public BookMark[] getBookMarks() {
        return bookMarks;
    }

    public void setBookMarks(BookMark[] bookMarks) {
        this.bookMarks = bookMarks;
    }
}
