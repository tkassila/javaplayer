package com.metait.javafxplayer.daisy2;

import java.util.ArrayList;
import java.util.List;

public class SmilData_ParGroup {
    private String strGroupName = null;
    private boolean bSameGroupNames = true;

    public boolean isbSameGroupNames() {
        return bSameGroupNames;
    }

    public void setSameGroupNames(boolean bSameGroupNames) {
        this.bSameGroupNames = bSameGroupNames;
    }

    public String getStrGroupName() {
        return strGroupName;
    }

    public void setStrGroupName(String strGroupName) {
        this.strGroupName = strGroupName;
    }

    public List<Integer> getListConstainLevels() {
        return listConstainLevels;
    }

    public void setListConstainLevels(List<Integer> listConstainLevels) {
        this.listConstainLevels = listConstainLevels;
    }

    public List<SmilData_Par> getListItems() {
        return listItems;
    }

    public void setListItems(List<SmilData_Par> listItems) {
        this.listItems = listItems;
    }

    private List<Integer> listConstainLevels = new ArrayList<Integer>();
    private List<SmilData_Par> listItems = new ArrayList<>();
    public void addSmilDataPar(SmilData_Par par)
    {
       if (this.strGroupName == null && par.getClassName() != null)
           this.strGroupName = par.getClassName();
       else
           if (this.strGroupName != null && bSameGroupNames && !this.strGroupName.equals(par.getClassName()))
               return;
       int iLevel = par.getSmilParLevel();
       if (iLevel > -1 && !listConstainLevels.contains(new Integer(iLevel)))
       {
           listConstainLevels.add(new Integer(iLevel));
       }
       listItems.add(par);
    }

}
