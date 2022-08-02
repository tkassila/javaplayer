package com.metait.javafxplayer.help;

import org.w3c.dom.Node;

public class TreeItemString {
    private String visaulString = "";
    private Node node = null;

    public TreeItemString(String p_visaulString, Node p_node)
    {
        super();
        setVisaulString(p_visaulString);
        setNode(p_node);
    }

    public String toString() { return visaulString; }

    public String getVisaulString() {
        return visaulString;
    }

    public void setVisaulString(String visaulString) {
        this.visaulString = visaulString;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
