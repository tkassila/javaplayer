package com.metait.javafxplayer.help;

import com.metait.javafxplayer.PlayerController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;
// import org.w3c.dom.html.HTMLAnchorElement;

// import java.awt.*;


public class HelpLinkRedirectListener implements ChangeListener<Worker.State>, EventListener
{
    private static final String CLICK_EVENT = "click";
    private static final String FOCUS_EVENT = "focusin";
    private static final String ANCHOR_TAG = "a";

    private final WebView webView;
    private HelpController m_helpController;
    private boolean loadAddListener = false;
    public void setLoadAddListener(boolean value)
    {
        loadAddListener = value;
    }
    public boolean getLoadAddListener() {
        return loadAddListener;
    }

    public HelpLinkRedirectListener(WebView webView, HelpController helpController)
    {
        this.webView = webView;
        this.m_helpController = helpController;
    }


    private void showNodeContent(Node n, int depth) {
        if (loadAddListener)
            return;

        for (int i=0; i<depth; i++) {
            System.out.print(" ");
        }
        System.out.println(n.getNodeName()+":"+n.getNodeValue());
        NodeList children = n.getChildNodes();
        if (children == null || children.getLength() == 0)
            return;
        // webView.getEngine().
      //  webViewHelp.
        for (int i=0; i<1; i++) {
            showNodeContent(children.item(i), depth+1);
        }
        m_helpController.setLoadAddListener(true);
        /*
        for (int i=0; i<children.getLength(); i++) {
            showNodeContent(children.item(i), depth+1);
        }
         */
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue)
    {
        if (Worker.State.SUCCEEDED.equals(newValue))
        {
            Document document = webView.getEngine().getDocument();
            // showNodeContent(document, 4);
            m_helpController.indexChildNodes(document.getElementsByTagName("BODY"));
            NodeList anchors = document.getElementsByTagName(ANCHOR_TAG);
            for (int i = 0; i < anchors.getLength(); i++)
            {
                Node node = anchors.item(i);
                EventTarget eventTarget = (EventTarget) node;
                HTMLAnchorElement anchorElement = (HTMLAnchorElement)node;
                String href = anchorElement.getHref();
                String id = anchorElement.getId();
                String text = anchorElement.getTextContent();
                /*
                if (id == null) {
                    anchorElement.setId(m_helpController.getKeyFromSmilHref(href));
                    id = anchorElement.getId();
                }
                m_helpController.addAnchorTagHref(href, id, text, i);
                 */
                eventTarget.addEventListener(CLICK_EVENT, this, false);
                eventTarget.addEventListener(FOCUS_EVENT, this, false);
            }
            m_helpController.allAnchorTagHrefCalledInHtmlDoc();
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
 //       System.out.println("-1- href=" +href);
        if (eventType != null && eventType.equals("focusin"))
        {
            m_helpController.handleFocusIn(href, id, text);
            return;
        }
        m_helpController.handleLinkClick(href, id, text);
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
