package com.metait.javafxplayer.daisy2;

public class SmilData_Par_Audio {
    private String audio_src = null;
    private String audio_id = null;
    private String clip_begin = null;
    private String clip_end = null;
    private int ind = -1;
    private String par_id;

    public void setInd(int value) { ind = value; }
    public int getInd() { return ind; }
    public double etDur() { return Double.parseDouble(clip_end)  - Double.parseDouble(clip_begin); }

    public String getPar_id() {
        return par_id;
    }
    public void setPar_id(String par_id) {
        this.par_id = par_id;
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
}
