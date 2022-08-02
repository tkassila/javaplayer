package com.metait.javafxplayer.daisy2;

import java.util.ArrayList;
import java.util.List;

public class SmilData_Par {
    private String parentId = null;
    private String endsync = null;
    private String par_id = null;
    private String text_src = null;
    private String text_id = null;
    private String audio_src = null;
    private String audio_id = null;
    private String clip_begin = null;
    private String clip_end = null;
    private String text = null;
    private String seq_dur = null;
    private int ind = -1;
    private int iSmilParLevel = 0;
    private String className = null;
    private int iUnderPage = 0;
    private int iUnderPageLevel = 0;
    private String underPagePar_id = null;
    private String underHeaderPar_id = null;

    public String getUnderHeaderPar_id() {
        return underHeaderPar_id;
    }

    public static String cnstLogicalRoot = "tLogicalRoot";

    public SmilData_Par(){}
    public SmilData_Par(String id){
        super();
        setPar_id(id);
    }
    public void setUnderHeaderPar_id(String underHeaderPar_id) {
        this.underHeaderPar_id = underHeaderPar_id;
    }

    public String getUnderPagePar_id() {
        return underPagePar_id;
    }

    public void setUnderPagePar_id(String underPagePar_id) {
        this.underPagePar_id = underPagePar_id;
    }

    public int getiUnderPage() {
        return iUnderPage;
    }

    public void setUnderPage(int iUnderPage) {
        this.iUnderPage = iUnderPage;
    }

    public int getUnderPageLevel() {
        return iUnderPageLevel;
    }

    public void setUnderPageLevel(int iUnderPageLevel) {
        this.iUnderPageLevel = iUnderPageLevel;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private List<SmilData_Par_Audio> listSmilDataParAudioItems = new ArrayList<>();

    public List<SmilData_Par_Audio> getListSmilDataParAudioItems() {
        return listSmilDataParAudioItems;
    }

    public void setListSmilDataParAudioItems(List<SmilData_Par_Audio> listSmilDataParAudioItems) {
        this.listSmilDataParAudioItems = listSmilDataParAudioItems;
    }

    public void setSmilParLevel(int value) { iSmilParLevel = value; }
    public int getSmilParLevel() { return iSmilParLevel; }

    public void setInd(int value) { ind = value; }
    public int getInd() { return ind; }
    public String getText() { return text; }
    public void setText(String value) { text = value; }

    public String getSeq_dur() { return seq_dur; }
    public void setSeq_dur(String value) {
        this.seq_dur = value;
    }

    public String getEndsync() {
        return endsync;
    }

    public void setEndsync(String endsync) {
        this.endsync = endsync;
    }

    public String getPar_id() {
        return par_id;
    }

    public void setPar_id(String par_id) {
        this.par_id = par_id;
    }

    public String getText_src() {
        return text_src;
    }

    public void setText_src(String text_src) {
        this.text_src = text_src;
    }

    public String getText_id() {
        return text_id;
    }

    public void setText_id(String text_id) {
        this.text_id = text_id;
    }

    public String getAudio_src() {
        return audio_src;
    }

    public void setAudio_src(String audio_src) {
        this.audio_src = audio_src;
    }

    public String getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(String audio_id) {
        this.audio_id = audio_id;
    }

    public String getClip_begin() {
        return clip_begin;
    }
    public double getClip_beginSeconds()
    {
        return getDoubleFromStringValue(clip_begin);
    }

    public void setClip_begin(String clip_begin) {
        this.clip_begin = clip_begin;
    }

    public String getClip_end() {
        return clip_end;
    }
    public double getclip_endSeconds()
    {
        return getDoubleFromStringValue(clip_end);
    }

    private double getDoubleFromStringValue(String value)
    {
        double ret = -1.0;
        if (value == null || value.trim().length() == 0)
            return -1.0;
        String strvalue = new String(value);
        int ind = strvalue.indexOf('=');
        if (ind > -1)
        {
            strvalue = strvalue.substring(ind +1);
        }
        strvalue = strvalue.replace("s","");
        ret = Double.parseDouble(strvalue);
        return ret;
    }

    public void setClip_end(String clip_end) {
        this.clip_end = clip_end;
    }

    public boolean durationIsBetweenBeginAndEndTimes(double currentDuration)
    {
        boolean ret = false;
        if (currentDuration < 0.0)
            return false;
        double endClip = getclip_endSeconds();
        boolean smallerOrEqualOfEndClip = currentDuration <= endClip;
        boolean equalOrGreateOfZero = 0.0 <= currentDuration;
        return (equalOrGreateOfZero && smallerOrEqualOfEndClip);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
