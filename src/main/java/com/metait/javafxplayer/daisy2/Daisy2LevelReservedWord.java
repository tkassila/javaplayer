package com.metait.javafxplayer.daisy2;

import java.util.ArrayList;
import java.util.List;

public class Daisy2LevelReservedWord {
    private final String idWord;
    private List<SmilData_Par> smilData_pars = new ArrayList<>();
    public Daisy2LevelReservedWord(String p_idWord)
    {
        idWord = p_idWord;
    }

    public void add(SmilData_Par par)
    {
        smilData_pars.add(par);
    }
    public void remove(SmilData_Par par)
    {
        smilData_pars.remove(par);
    }
    public void clear() { smilData_pars.clear(); }
}
