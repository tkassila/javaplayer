package com.metait.javafxplayer;

import com.metait.javafxplayer.daisy2.SmilData;
import com.metait.javafxplayer.daisy2.SmilData_Par;

import java.util.List;

public interface IParLevelSetter {
    public SmilData_Par getLeveledParItem(SmilData_Par d_par, SmilData parent);
 //   public List<SmilData_Par> getUpdatedSmilDataParsItems(List<SmilData_Par> parlist);
}
