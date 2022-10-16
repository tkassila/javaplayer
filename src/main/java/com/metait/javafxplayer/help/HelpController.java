package com.metait.javafxplayer.help;

import com.metait.javafxplayer.HyperLinkRedirectListener;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import javafx.scene.control.TreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
// import org.w3c.dom.html.HTMLAnchorElement;

// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.XMLConstants;
//import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

public class HelpController {

    @FXML
    private Button buttonHelpClose;
    @FXML
    private WebView webViewHelp;
    @FXML
    private TreeView<TreeItemString> treeView;
    @FXML
    private Button buttonTreeView;

    private boolean bLoaded = false;
    private String execJs = "";
    private String execJs2 = "";
    private String execJs3 = "";
    private String htmlid = null;
    private HashMap<String,Integer> hashMapHtml = new HashMap<>();
    private boolean bTreeViewExpanded = false;
    private TreeItem<TreeItemString> rootItem = null;
    private String strHelp = null;
    private HelpLinkRedirectListener linkRedirectListener = null;
    private boolean loadAddListener = false;
    public void setLoadAddListener(boolean value)
    {
        loadAddListener = value;
    }
    public boolean getLoadAddListener() {
        return loadAddListener;
    }

    @FXML
    public void initialize() {

        Node rootIcon = null;
        TreeItemString root = new TreeItemString("Index", rootIcon);
        rootItem = new TreeItem<TreeItemString> (root);

        WebEngine webEngine = webViewHelp.getEngine();
        bLoaded = false;
        webEngine.setJavaScriptEnabled(true);
        webEngine.getLoadWorker().stateProperty().addListener((ov,oldState,newState)->{
            if(newState== Worker.State.SCHEDULED){
                System.out.println("state: scheduled");
            } else if(newState== Worker.State.RUNNING){
                System.out.println("state: running");
            } else if(newState== Worker.State.SUCCEEDED){
                System.out.println("state: succeeded");
                loadAddListener = true;
            }
        });
        linkRedirectListener = new HelpLinkRedirectListener(webViewHelp, this);
        webEngine.getLoadWorker().stateProperty().addListener(linkRedirectListener);

        strHelp = getHelpHtml("help.html");
        // webEngine.load(strHelp);
        webEngine.loadContent(strHelp);
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TreeItemString>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TreeItemString>> observable,
                                TreeItem<TreeItemString> oldValue, TreeItem<TreeItemString> newValue) {
                // newValue represents the selected itemTree
                treeItemChanded(oldValue, newValue);
            }
        });
        pressedButtonTreeView();
     //   System.out.println("handleLinkClick end => bLoaded=" +bLoaded);
    }

    private void treeItemChanded(TreeItem<TreeItemString> oldValue, TreeItem<TreeItemString> newValue)
    {
        if (newValue != null && newValue.getValue() != null && newValue.getValue().toString() != null
           && !newValue.getValue().toString().toLowerCase().equals("index"))
        {
            TreeItemString tis = newValue.getValue();
            if (tis == null)
                return;
            Node node = tis.getNode();
            NamedNodeMap atts = node.getAttributes();
            if (atts == null)
                return;
            Node idNode = atts.getNamedItem("id");
            if (idNode == null)
                return;
            String id = idNode.getNodeValue();
            // this works all ways: next two staetments are helping clean earlier selections
            WebEngine webEngine = webViewHelp.getEngine();
            if (linkRedirectListener != null) {
                webEngine.getLoadWorker().stateProperty().removeListener(linkRedirectListener);
                linkRedirectListener = null;
            }
            webEngine.loadContent(strHelp);
            webEngine.reload();

            if (id != null)
                scrollInto(id);
        }
    }

    private void addAttributeIdIntoAndChildren(Node node)
    {
        if (node == null)
            return;
    }

    private String getNewId( Element element)
    {
        String ret = null;
        if (element != null && (element.id() == null || element.id().trim().length()==0)) {
            String name = element.nodeName();
            // if (name == null)
            Integer iCounter = hashMapHtml.get(name);
            if (iCounter == null){
                iCounter = 0;
            }
            else {
                iCounter++;
            }
            hashMapHtml.put(name, iCounter);
            ret = name +"_" +iCounter;
        }
        return ret;
    }

    private String getHelpHtml(String resourcePath)
    {
        String ret = null;
        File f = new File(resourcePath.replace("file:/",""));
        // BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            InputStream in = getClass().getResourceAsStream(resourcePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null)
                sb.append(line +"\n");
            // StringReader reader = new StringReader()
            Document doc = Jsoup.parse(sb.toString(), "UTF-8");
            Element body = doc.body();
            Elements children = body.getAllElements();
            String id;
            for (Element e : children)
            {
                id = e.id();
                if (id == null || id.trim().length() == 0) {
                    id = getNewId(e);
                    if (id != null && id.trim().length()!=0)
                        e.attributes().add("id", id);
                }
            }
            ret = doc.html();
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
        /*
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
           // factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
          //  factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document input = factory.newDocumentBuilder().parse(new File(resourcePath.replace("file:/","")));
            NodeList bodyChildren = input.getElementsByTagName("BODY");
            Node body = bodyChildren.item(0);
            if (body == null)
                return "";
            addAttributeIdIntoAndChildren(body);
            ret = getStringFromDocument(input);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
         */
        return ret;
    }

    //method to convert Document to String
    /*
    public String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    */

    @FXML
    public void pressedButtonHelpClose()
    {
       // System.out.println("pressedButtonHelpClose");
        Stage stage = (Stage) buttonHelpClose.getScene().getWindow();
        // do what you have to do
        // setBodyDefaultBackground();
        stage.close();
    }

    public void allAnchorTagHrefCalledInHtmlDoc()
    {
        bLoaded = true;
     //   System.out.println("allAnchorTagHrefCalledInHtmlDoc called");
    }

    public void handleFocusIn(String href, String id, String text)
    {
      //  System.out.println("handleFocusIn href=" +href +" id=" +id +" text=" +text +" called");
    }

    private boolean isHeader(String nodeNmae)
    {
        boolean ret = false;
        if (nodeNmae != null && nodeNmae.trim().length()>0)
        {
            if (nodeNmae.length() > 1 && nodeNmae.startsWith("H"))
            {
                String strRest = nodeNmae.substring(1);
                if (strRest != null && Integer.parseInt(strRest)>0)
                    return true;
            }
        }
        return ret;
    }

    private int getHeaderNumber(String nodeNmae)
    {
        int ret = -1;
        if (nodeNmae != null && nodeNmae.trim().length()>0)
        {
            if (nodeNmae.length() > 1 && nodeNmae.startsWith("H"))
            {
                String strRest = nodeNmae.substring(1);
                if (strRest != null)
                    return Integer.parseInt(strRest);
            }
        }
        return ret;
    }

    private boolean isNotValidIndexNode(Node node)
    {
        boolean ret = true;
        if (node == null)
            return true;
        String nodeName = node.getNodeName();
        if ( /* node.getNodeType() == Node.COMMENT_NODE
                || node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE */
                node.getNodeType() != Node.ELEMENT_NODE
                || node.getNodeType() == Node.TEXT_NODE  )
            return true;
        if (nodeName == null || nodeName.trim().length()==0 || nodeName.equals("B")
                || nodeName.equals("BR") || nodeName.equals("TD") || nodeName.equals("TR")
                || nodeName.equals("U") || nodeName.equals("i") || nodeName.equals("BR")
         || nodeName.equals("THEAD")  || nodeName.equals("TBODY")  || nodeName.equals("THEAD")
                    || nodeName.equals("STRONG")  || nodeName.equals("EM")  || nodeName.equals("COLGROUP")
                || nodeName.equals("GROUP") || nodeName.equals("ABBR") || nodeName.equals("ACRONYM")
                || nodeName.equals("COL") || nodeName.equals("BIG") || nodeName.equals("BDO")
                || nodeName.equals("BDI") || nodeName.equals("CANVAS") || nodeName.equals("CAPTION")
                || nodeName.equals("CENTER")  || nodeName.equals("CITE")  || nodeName.equals("CODE")
                || nodeName.equals("DATA") || nodeName.equals("DFN") || nodeName.equals("FIELDSET")
                || nodeName.equals("FIELDSET") || nodeName.equals("FONT") || nodeName.equals("OBJECT")
                || nodeName.equals("PARAM") || nodeName.equals("FONT") || nodeName.equals("PRE")
                || nodeName.equals("PROGRESS") || nodeName.equals("Q") || nodeName.equals("RP")
                || nodeName.equals("RUBY") || nodeName.equals("RT") || nodeName.equals("S")
                || nodeName.equals("SAMP") || nodeName.equals("SCRIPT") || nodeName.equals("SECTION")
                || nodeName.equals("SMALL") || nodeName.equals("SOURCE") || nodeName.equals("SPAN")
                || nodeName.equals("STRIKE") || nodeName.equals("SUB") || nodeName.equals("SUP")
                || nodeName.equals("TIME") /* || nodeName.equals("TIME") */
                || nodeName.equals("#text"))
            return true;
        ret = false;
        return ret;
    }

    private TreeItem<TreeItemString> getTreeItem(String nodeValue, Node node)
    {
        String nodeName = node.getNodeName();
        String nodeValue2 = null;
        if (isNotValidIndexNode(node))
            return null;
        if (nodeValue ==null || nodeValue.trim().length()==0)
        {
            System.out.println("ddd");
        }
        if (nodeName.equals("UL"))
            nodeValue = "LIST";
        else
        if (nodeName.equals("OL"))
            nodeValue = "LIST";
        else
        if (nodeName.equals("BL"))
            nodeValue = "LIST";
        else
        if (nodeName.equals("DL"))
            nodeValue = "LIST";
        else
        if (nodeName.equals("SVG"))
            nodeValue = "IMAGE";
        else
        if (nodeName.equals("TABLE"))
            nodeValue = "TABLE";
        else
        if (nodeName.equals("FIGURE"))
            nodeValue = "IMAGE";
        else
        if (nodeName.equals("IMG"))
            nodeValue = "IMAGE";
        /*
        else
        if (nodeName.equals("A"))
            nodeValue = "LINK";
        */
        TreeItemString tiString = new TreeItemString(nodeValue, node);
        TreeItem<TreeItemString> ret = new TreeItem<TreeItemString>(tiString);
        TreeItemString tiString2 = null;
        TreeItem<TreeItemString> ret2 = null;
        NodeList children = node.getChildNodes();
        int max = children.getLength();
        Node n = null;
        boolean isHeaderNode = isHeader(nodeName);
        if (!isHeaderNode)
        for(int i = 0; i < max ; i++)
        {
            n = children.item(i);
            if (n == null)
                continue;
            nodeValue2 = n.getNodeValue();
            if (nodeValue2 == null)
                nodeValue2 = n.getTextContent();
            ret2 = getTreeItem(nodeValue2, n);
            if (ret2 != null)
            ret.getChildren().add(ret2);
        }
        return ret;
    }

    public void indexChildNodes(NodeList children)
    {
    //    System.out.println("indexChildNodes ");

        Node node = null;
        String nodeName = null, nodeValue = null;
        int iHeader = -1, iPrevHeader = -1;
        int iDepth = 0;
        Node body = children.item(0);
        NodeList bodyChildren = body.getChildNodes();
        int max = bodyChildren.getLength();

        rootItem.setExpanded(true);
        String id = null;
        TreeItem<TreeItemString> item = null;

        for (int i = 0; i < max ; i++ )
        {
            node = bodyChildren.item(i);
            nodeName = node.getNodeName();
            if (nodeName == null)
                continue;
            if (isHeader(nodeName))
            {
                iHeader = getHeaderNumber(nodeName);
                iDepth = iHeader;
                nodeValue = node.getNodeValue();
                if (nodeValue == null)
                    nodeValue = node.getTextContent();
                item = getTreeItem(nodeValue, node);
                if (item != null)
                    rootItem.getChildren().add(item);
              //  TreeItem<String> item = new TreeItem<String> (getSpace(iDepth) +nodeValue);
              //  rootItem.getChildren().add(item);
            }
            else
            {
                nodeValue = node.getNodeValue();
                if (nodeValue == null)
                    nodeValue = node.getTextContent();
                item = getTreeItem(nodeValue, node);
                if (item != null)
                    rootItem.getChildren().add(item);
            }
            EventTarget eventTarget = (EventTarget) node;
        //    HTMLAnchorElement anchorElement = (HTMLAnchorElement)node;
       //     System.out.println("node=" +node.toString());
        }
        treeView.setRoot(rootItem);
        rootItem.setExpanded(true);
    }

    private void showNodeContent(Node n, int depth) {
        for (int i=0; i<depth; i++) {
            System.out.print(" ");
        }
        System.out.println(n.getNodeName()+":"+n.getNodeValue());
        NodeList children = n.getChildNodes() ;
        for (int i=0; i<children.getLength(); i++) {
            showNodeContent(children.item(i), depth+1);
        }
    }

    public void handleLinkClick(String href, String id, String text)
    {
        System.out.println("handleLinkClick href=" +href +" id=" +id +" text=" +text +" called");
        WebEngine webEngine = webViewHelp.getEngine();
        bLoaded = false;
        webEngine.load(getClass().getResource(href).toString());
      //  System.out.println("handleLinkClick end => bLoaded=" +bLoaded);
    }

    public void scrollInto(String p_htmlid)
    {
        if (p_htmlid != null && p_htmlid.trim().length()>0) {

            execJs = "document.getElementById(" + '"' + p_htmlid + '"' + ").scrollIntoView();";
            execJs2 = getBackGroundOf(p_htmlid, "yellow");
            execJs3 = null;
            if (htmlid != null)
                // execJs3 = getBackGroundOf(htmlid, "white");
                execJs3 = "window.location.reload();\n "
                        + "function delay(time) {\n"
                        +"  return new Promise(resolve => setTimeout(resolve, time));\n"
                       +"}\n\n"
                + "function  setItemAndChildrenIntoWhite(item){\n"
                + "item.style.backgroundColor = \"white\";\n"
                +  "var c = item.children;\n"
                +   "var i;\n"
                 +  "for (i = 0; i < c.length; i++) {\n"
                 +  "setItemAndChildrenIntoWhite(c[i]);"
                  +  "}\n}"
                  + "setItemAndChildrenIntoWhite(document.body);\n"
                        +"  "; // delay(1000);
            /*    "  c[i].style.backgroundColor = \"white\";\n" + */

               /*
                execJs3 = "var c = document.body.children;\n" +
                        "var i;\n" +
                        "for (i = 0; i < c.length; i++) {\n" +
                        "  c[i].style.backgroundColor = \"white\";\n" +
                        "}\n"
                        + "function delay(time) {\n" +
                        "  return new Promise(resolve => setTimeout(resolve, time));\n" +
                        "}\n"
                        +"delay(1000);";
                */
            /*
            Platform.runLater(new Runnable() {
                public void run() {
                    WebEngine engine = webViewHelp.getEngine();
                    if (engine != null)
                        // engine.reload();
                        engine.loadContent(strHelp);
                    try {  // this needed here, because above js is fully done
                        Thread.sleep(1500); //  before below js will executed
                    }catch (Exception e){
                    }
                }
            });
             */
            Platform.runLater(new Runnable() {
                public void run() {
                    WebEngine engine = webViewHelp.getEngine();
                    if (engine != null) {

                        if (execJs3 != null) {
                            // engine.executeScript(execJs3);
                            engine.executeScript("document.body.style.backgroundColor = 'white';");
                            engine.executeScript(execJs3);
                            try {  // this needed here, because above js is fully done
                                Thread.sleep(600); //  before below js will executed
                            }catch (Exception e){
                            }
                        }

                        /*
                        engine.loadContent(strHelp);
                        try {  // this needed here, because above js is fully done
                            Thread.sleep(1500); //  before below js will executed
                        }catch (Exception e){
                        }
                         */
                        engine.executeScript(execJs);
                        engine.executeScript(execJs2);
                        htmlid = p_htmlid;
                    }
                }
            });
        }
    }

    private void setBodyDefaultBackground()
    {
        // let children = parentElement.childNodes;
        if (htmlid != null) {
            execJs2 = "for (const element of document.body.childNodes) { element.style.backgroundColor = \"white\"; }";
            Platform.runLater(new Runnable() {
                public void run() {
                    WebEngine engine = webViewHelp.getEngine();
                    if (engine != null) {
                        engine.executeScript("console.log('document.body.childNodes. size' +document.body.childNodes.length);");
                    }
                }
            });
        }
    }

    private String getBackGroundOf(String htmlid, String color)
    {
        if (htmlid == null || htmlid.trim().length()==0 || color == null || color.trim().length()==0)
            return "";
        return "document.getElementById(" +'"' + htmlid +'"' +").style.backgroundColor = '" +color +"';";
    }

    @FXML
    protected void pressedButtonTreeView()
    {
        if (bTreeViewExpanded) {
            hideTreeViewTreeItemsExceptTrrRoot();
            bTreeViewExpanded = false;
        }
        else
        {
            showTreeViewTreeItemsExceptTrrRoot();
            bTreeViewExpanded = true;
        }
        WebEngine webEngine = webViewHelp.getEngine();
    }

    private void hideTreeViewTreeItemsExceptTrrRoot()
    {
        setTreeViewExpandValue(false);
    }

    private void showTreeViewTreeItemsExceptTrrRoot()
    {
        setTreeViewExpandValue(true);
    }

    private void setTreeViewExpandValue(boolean bValue)
    {
        Platform.runLater(new Runnable() {
            public void run() {
                expandTreeItem(rootItem, bValue);
            }
        });
    }

    private void expandTreeItem(TreeItem<TreeItemString> item, boolean bValue)
    {
        if (item == null)
            return;
        item.setExpanded(bValue);
        for (TreeItem<TreeItemString> ti : item.getChildren()) {
            expandTreeItem(ti, bValue);
        }
    }
}