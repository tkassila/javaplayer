package com.metait.javafxplayer.daisy2;

public class SmilIndexLink {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setParentId(String idParent) {
        this.idParent = idParent;
    }

    public String getIndexhref() {
        return indexhref;
    }

    public void setIndexhref(String indexhref) {
        this.indexhref = indexhref;
    }

    public String getStrText() {
        return strText;
    }

    public void setStrText(String strText) {
        this.strText = strText;
    }

    public String getParnentElelmentName() {
        return parnentElelmentName;
    }

    public void setParnentElelmentName(String parnentElelmentName) {
        this.parnentElelmentName = parnentElelmentName;
    }

    private String id;

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getParentClassName() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    private String idParent;
    private String indexhref = null;
    private String strText = null;
    private String parnentElelmentName = null;
    private String parentClass = null;
}
