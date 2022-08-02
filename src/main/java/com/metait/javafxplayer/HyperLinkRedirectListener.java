package com.metait.javafxplayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;
// import org.w3c.dom.html.HTMLAnchorElement;

// import java.awt.*;


public class HyperLinkRedirectListener implements ChangeListener<Worker.State>, EventListener
{
    private static final String CLICK_EVENT = "click";
    private static final String FOCUS_EVENT = "focusin";
    private static final String ANCHOR_TAG = "a";

    private final WebView webView;
    private PlayerController m_playerController;

    public HyperLinkRedirectListener(WebView webView, PlayerController playerController)
    {
        this.webView = webView;
        this.m_playerController = playerController;
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue)
    {
        if (Worker.State.SUCCEEDED.equals(newValue))
        {
            Document document = webView.getEngine().getDocument();
            NodeList metaitems = document.getElementsByTagName("meta");
            Node node, nodeName, nodeContent, nodeAttr;
            String strDepth = null;
            String strTitle = null;
            String strCreator = null;
            String strMaxPages = null;
            String strRevisionDate = null;
            String strCds = null, strNodeName = null, strAttrName = null, strAttrValue = null;
            NamedNodeMap attributes;
            if (metaitems != null && metaitems.getLength()>0) {
                for(int i = 0; i < metaitems.getLength(); i++) {
                    node = metaitems.item(i);
                    strNodeName = node.getNodeName();
                    attributes = node.getAttributes();
                    for (int k = 0; k < attributes.getLength(); k++) {
                        nodeAttr = attributes.item(k);
                        if (nodeAttr == null) {
                            System.out.println("null");
                            continue;
                        }

                        strAttrName = nodeAttr.getNodeName();
                        strAttrValue = nodeAttr.getNodeValue();
                        /*
                        System.out.println("strAttrName=" +strAttrName);
                        System.out.println("strAttrValue=" +strAttrValue);
                        System.out.println("ncc:depth=" +(attributes.getNamedItem("ncc:depth") != null ? attributes.getNamedItem("ncc:depth").getNodeValue() : ""));
                        System.out.println("content=" +(attributes.getNamedItem("content") != null ? attributes.getNamedItem("content").getNodeValue() : ""));
                        /*
                    nodeName = attributes.getNamedItem("ncc:depth");
                    nodeContent = attributes.getNamedItem("content");
                    */
                        if (strAttrValue != null && strAttrValue.equals("ncc:depth")
                                && attributes.getNamedItem("content") != null) {
                            strDepth = attributes.getNamedItem("content").getNodeValue();
                        }
                        else
                        if (strAttrValue != null && strAttrValue.equals("dc:creator")
                                && attributes.getNamedItem("content") != null) {
                            strCreator = attributes.getNamedItem("content").getNodeValue();
                        }
                        else
                        if (strAttrValue != null && strAttrValue.equals("ncc:maxPageNormal")
                                && attributes.getNamedItem("content") != null) {
                            strMaxPages = attributes.getNamedItem("content").getNodeValue();
                        }
                        else
                        if (strAttrValue != null && strAttrValue.equals("dc:title")
                                && attributes.getNamedItem("content") != null) {
                            strTitle = attributes.getNamedItem("content").getNodeValue();
                        }
                        else
                        if (strAttrValue != null && strAttrValue.equals("ncc:revisionDate")
                                && attributes.getNamedItem("content") != null) {
                            strRevisionDate = attributes.getNamedItem("content").getNodeValue();
                        }
                        else
                        if (strAttrValue != null && strAttrValue.equals("ncc:setInfo")
                                && attributes.getNamedItem("content") != null) {
                            strCds = attributes.getNamedItem("content").getNodeValue();
                        }
                    }
                }
            }

            NodeList anchors = document.getElementsByTagName(ANCHOR_TAG);
            EventTarget eventTarget;
            HTMLAnchorElement anchorElement;
            Node parentNode;
            for (int i = 0; i < anchors.getLength(); i++)
            {
                node = anchors.item(i);
                eventTarget = (EventTarget) node;
                anchorElement = (HTMLAnchorElement)node;
                String href = anchorElement.getHref();
                String id = anchorElement.getId();
                String text = anchorElement.getTextContent();
                parentNode = anchorElement.getParentNode();
                String attrClass = null;
                String parentNodeElementName = null;
                String parentId = null;
                if (parentNode != null) {
                    attributes = parentNode.getAttributes();
                    parentNodeElementName = parentNode.getNodeName();
                    if (attributes.getNamedItem("class") != null) {
                        attrClass = attributes.getNamedItem("class").getNodeValue();
                        parentId = attributes.getNamedItem("id").getNodeValue();
                    }
                }
                /*
                if (attributes != null)
                {
                    Node nodeAttrClass = attributes.getNamedItem("class");
                    if (nodeAttrClass != null)
                        attrClass = nodeAttrClass.getNodeValue();
                }
                 */
                if (id == null) {
                    anchorElement.setId(m_playerController.getKeyFromSmilHref(href));
                    id = anchorElement.getId();
                }
                m_playerController.addAnchorTagHref(href, id, text, attrClass, parentNodeElementName, i, parentId);
                eventTarget.addEventListener(CLICK_EVENT, this, false);
                eventTarget.addEventListener(FOCUS_EVENT, this, false);
            }
            m_playerController.allAnchorTagHrefCalledInHtmlDoc(strMaxPages, strCds, strDepth,
                    strTitle, strCreator, strRevisionDate);
        }
    }

    @Override
    public void handleEvent(Event event)
    {
        // EventTarget anchorElement = event.getCurrentTarget();
        String eventType = event.getType();
        HTMLAnchorElement anchorElement = (HTMLAnchorElement) event.getCurrentTarget();
        // System.out.println("anchorElement=" +anchorElement.getClass().getName());
        String href = anchorElement.getHref();
        String text = anchorElement.getTextContent();
        String id = anchorElement.getId();
   //     System.out.println("-1- href=" +href);
        if (eventType != null && eventType.equals("focusin"))
        {
            m_playerController.handleFocusIn(href, id, text);
            return;
        }
        m_playerController.handleLinkClick(href, id, text);

        /*
        if (Desktop.isDesktopSupported())
        {
            openLinkInSystemBrowser(href);
        } else
        {
            // LOGGER.warn("OS does not support desktop operations like browsing. Cannot open link '{}'.", href);
        }
        */
        event.preventDefault();
    }

    private void openLinkInSystemBrowser(String url)
    {
        // LOGGER.debug("Opening link '{}' in default system browser.", url);

        /*
        try
        {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (Throwable e)
        {
            // LOGGER.error("Error on opening link '{}' in system browser.", url);
        }
         */
    }
}
