package com.metait.javafxplayer.bookmark;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookMarkCollection
{
    public enum BOOKMARK_TYPE {
        DAISY, AUDIO_DIR, AUDIO_FILE
    }

    public BookMarkCollection(BOOKMARK_TYPE p_type)
    {
        type = p_type;
    }

    public BOOKMARK_TYPE getType() { return type; }
    private BOOKMARK_TYPE type = null;
    private BookMark [] bookMarks = null;
    private String strName = "";
    private String strPlayFilePath = "";
    private String strSelectedDirPath = "";

    private File fFile = null;
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
        return fFile;
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
    public boolean addBookMark(BookMark p_bm)
    {
        boolean ret = false;
        if (p_bm == null)
            return false;
        if (bookMarks == null) {
            bookMarks = new BookMark[1];
            bookMarks[0] = p_bm;
        }
        else
        {
            List<BookMark> bmList = new ArrayList<BookMark>(bookMarks.length);
            for(BookMark bm : bookMarks)
            {
                if (bm == null)
                    continue;
                bmList.add(bm);
            }
            boolean fAdded = false;
            if (!bmList.contains(p_bm)) {
                bmList.add(p_bm);
                fAdded = true;
            }
            if (fAdded) {
                bookMarks = new BookMark[bmList.size()];
                bookMarks = bmList.toArray(bookMarks);
            }
            ret = fAdded;
        }
        return ret;
    }
}
