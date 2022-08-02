package com.metait.javafxplayer.daisy2;

import java.util.HashMap;

public class SmilData_ParMeta {
    private HashMap<String, SmilData_ParGroup> metagroups = new HashMap<>();
    private int iMetaDepth = 0;
    private int iMaxPage = 0;
    private int iCurrentLevel = 0;
    private String strMaxPages = null;
    private String strCds = null;
    private String strDepth = null;
    private String strTitle = null;
    private String strCreator = null;
    private String strRevisionDate = null;

    public String getStrMaxPages() {
        return strMaxPages;
    }

    public void setStrMaxPages(String strMaxPages) {
        this.strMaxPages = strMaxPages;
        try {
            if (strMaxPages != null && strMaxPages.trim().length() > 0)
                iMaxPage = Integer.parseInt(strMaxPages);
        }catch (Exception e){
        }
    }

    public String getStrCds() {
        return strCds;
    }

    public void setStrCds(String strCds) {
        this.strCds = strCds;
    }

    public String getStrDepth() {
        return strDepth;
    }

    public void setStrDepth(String strDepth) {
        this.strDepth = strDepth;
        try {
            if (strDepth != null && strDepth.trim().length() > 0)
                iMetaDepth = Integer.parseInt(strDepth);
        }catch (Exception e){
        }
    }

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrCreator() {
        return strCreator;
    }

    public void setStrCreator(String strCreator) {
        this.strCreator = strCreator;
    }

    public String getStrRevisionDate() {
        return strRevisionDate;
    }

    public void setStrRevisionDate(String strRevisionDate) {
        this.strRevisionDate = strRevisionDate;
    }

    public HashMap<String, SmilData_ParGroup> getMetagroups() {
        return metagroups;
    }

    public void setMetagroups(HashMap<String, SmilData_ParGroup> metagroups) {
        this.metagroups = metagroups;
    }

    public int getiMetaDepth() {
        return iMetaDepth;
    }

    public void setiMetaDepth(int iMetaDepth) {
        this.iMetaDepth = iMetaDepth;
    }

    public int getiMaxPage() {
        return iMaxPage;
    }

    public void setiMaxPage(int iMaxPage) {
        this.iMaxPage = iMaxPage;
    }

    public int getiCurrentLevel() {
        return iCurrentLevel;
    }

    public void setiCurrentLevel(int iCurrentLevel) {
        this.iCurrentLevel = iCurrentLevel;
    }

}
