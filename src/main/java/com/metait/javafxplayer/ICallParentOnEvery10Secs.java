package com.metait.javafxplayer;

import com.metait.javafxplayer.daisy2.SmilData;
import com.metait.javafxplayer.daisy2.SmilData_Par;

public interface ICallParentOnEvery10Secs {
    public void callParentOnEvery10Secs(double currentTime, int iElapsedSecs, String strMediaSource);
}
