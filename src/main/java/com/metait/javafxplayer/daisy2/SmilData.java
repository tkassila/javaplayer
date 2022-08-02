package com.metait.javafxplayer.daisy2;

import com.metait.javafxplayer.IParLevelSetter;
import com.metait.javafxplayer.daisy2.ReadSmillDataFromFile;
import com.metait.javafxplayer.daisy2.SmilData_Par;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.util.Properties;

public class SmilData {

    public static IParLevelSetter m_setter;

    private String m_path = null;
    private String m_smilfilename = null;
    private String m_mp3filename = null;
    private File m_mp3File;
    private boolean bMp3FileNoExists = false;
    private  boolean bSmilFileNoExists = false;
    private Properties props = new Properties();

    private String dur = null;
    /*
    private String endsync = null;
    private String par_id = null;
    */
    private int indSmilFile = -1;
    private int indHtmlLink = -1;
    private String smilFilePath = null;
    private String href = null;
    private String attrClass = null;
    private String parentNodeElementName;

    public String getParentNodeElementName() { return parentNodeElementName;}
    public void setParentNodeElementName(String value) { parentNodeElementName = value;}

    public String getAttrClass() { return attrClass;}
    public void setAttrClass(String value) { attrClass = value;}

    public int getIndHtmlLink() { return indHtmlLink;}
    public void setIndHtmlLink(int value) { indHtmlLink = value;}

    public String getHref() { return href;}
    public void setHref(String value) { href = value;}

    public String getSmilFilePath() { return smilFilePath;}
    public void setSmilFilePath(String value) { smilFilePath = value;}

    public int getIndexOfSmilFile() { return indSmilFile; }
    public void setIndexOfSmilFile(int value) { indSmilFile = value; }

    public boolean isbSmilFileNoExists() {
        return bSmilFileNoExists;
    }
    public Properties geDctProperties() {
        return props;
    }
    public String getDuration() {
        return dur;
    }

    /*
    private String text_src = null;
    private String text_id = null;
    private String audio_src = null;
    private String audio_id = null;
    private String clip_begin = null;
    private String clip_end = null;
     */
    private List<SmilData_Par> listSmilDataParItems = new ArrayList<>();
    private HashMap<String, SmilData_Par> hashMap = new HashMap<>();

    public List<SmilData_Par> getSmilDataParsItems() { return listSmilDataParItems; }
    public String [] getSmilDataParKeys() { return hashMap.keySet().toArray(new String[0]); }
    public SmilData_Par getSmilData_ParOfKey(String par_id) { return hashMap.get(par_id); }

    public String toString() {
        return "m_path=" +m_path +" m_smilfilename=" +m_smilfilename +" m_mp3File="
                +(m_mp3File != null ? m_mp3File.getAbsolutePath() : "") +" bMp3FileNoExists=" +bMp3FileNoExists;
    }

    public SmilData(String path, String smilfilename, ReadSmillDataFromFile pair,
                    String p_attrClass, String p_parentNodeElementName)
    {
        m_path = path;
        if (m_path == null)
            m_path = new String("");

        m_smilfilename = smilfilename;
        attrClass = p_attrClass;
        parentNodeElementName = p_parentNodeElementName;
        if (pair != null)
            setPair(pair);
       // readSmilData();
    }

    public String getMp3filename() { return m_mp3filename; }

    public void setPair(ReadSmillDataFromFile pair)
    {
        bMp3FileNoExists = pair.bMp3FileNoExists;
        bSmilFileNoExists = pair.bSmilFileNoExists;
        props = pair.props;
        hashMap = pair.hashMap;
        m_mp3filename = pair.mp3FileName;
        m_mp3File = new File(m_mp3filename);
        bMp3FileNoExists = !m_mp3File.exists();
        listSmilDataParItems = pair.list;
        if (listSmilDataParItems != null && listSmilDataParItems.size()>0)
            dur = listSmilDataParItems.get(0).getSeq_dur();
        setSmilParLevelsOfListSmilDataParItems();
    }

    public void setSmilParLevelsOfListSmilDataParItems()
    {
        if (listSmilDataParItems == null || listSmilDataParItems.size()==0
            || attrClass == null || parentNodeElementName == null
            || attrClass.trim().length()==0 || parentNodeElementName.trim().length()==0)
            return;
        int i = 0;
        List<SmilData_Par> newlist = new ArrayList<SmilData_Par>();
        for(SmilData_Par d_par : listSmilDataParItems)
        {
            if (d_par == null)
                continue;
            d_par = m_setter.getLeveledParItem(d_par, this);
            newlist.add(d_par);
        }
       // newlist = m_setter.getUpdatedSmilDataParsItems(newlist);
        listSmilDataParItems = newlist;
    }

    /*
    private void readSmilData() {
       // Path path = Paths.get(m_smilfilename);
        File smilFile = new File(m_smilfilename);
        if (!smilFile.exists()) {
            bSmilFileNoExists = true;
            return;
        }

        ?*
        List<String> listContent = null;
        String strXml = null;
        try {
            listContent = Files.readAllLines(path);
            if (listContent != null && listContent.size() > 0)
            {
                StringBuffer sb = new StringBuffer();
                sb.append(listContent);
                strXml = sb.toString();
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            return;
        }
         *?

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // DocumentBuilder dBuilder = factory.newDocumentBuilder();
            ?*
            dbf.setFeature("http://xml.org/sax/features/namespaces", false);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            *?
            DocumentBuilder db = factory.newDocumentBuilder();
            db.setEntityResolver(new EntityResolver()
            {
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException
                {
                    return new InputSource(new StringReader(""));
                }
            });

            // Document doc = dBuilder.parse(new InputSource(new StringReader(strXml)));
            Document doc = db.parse(smilFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("meta");

            String name = null;
            String content = null;

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                System.out.println("\nCurrent Element: " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;
                    name = elem.getAttribute("name");
                    content = elem.getAttribute("content");
                    if (name != null && content != null)
                        props.put(name, content);
                }
            }

            nList = doc.getElementsByTagName("seq");

            NodeList nListPar = null, nListSeq = null, nListAudio = null, nListText = null;
            Node nNode3 = null, nNode2 = null, nNode = null, nNode2b = null, nNode4 = null;
            Element elem = null, elem2 = null, elem2b = null, elem4 = null;
            SmilData_Par sp = null;

            for (int i = 0; i < nList.getLength(); i++) {
                nNode = nList.item(i);
                System.out.println("\nCurrent Element: " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    elem = (Element) nNode;
                    dur = elem.getAttribute("dur");
                    nListPar = elem.getElementsByTagName("par");
                    for (int j = 0; j < nListPar.getLength(); j++) {
                        nNode2 = nListPar.item(j);
                        System.out.println("\nCurrent Element: " + nNode2.getNodeName());
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            elem2 = (Element) nNode2;
                            endsync = elem2.getAttribute("endsync");
                            par_id = elem2.getAttribute("id");

                            // handle 1 text block:
                            nListText = elem2.getElementsByTagName("text");
                            for (int jb = 0; jb < nListText.getLength(); jb++) {
                                nNode2b = nListText.item(jb);
                                System.out.println("\nCurrent Element: " + nNode2b.getNodeName());
                                if (nNode2b.getNodeType() == Node.ELEMENT_NODE) {
                                    elem2b = (Element) nNode2b;
                                    text_src = elem2b.getAttribute("src");
                                    text_id = elem2b.getAttribute("id");
                                    break;
                                }
                            }

                            int max_audios = 0;
                            // handle 1 seq block:
                            nListSeq = elem2.getElementsByTagName("seq");
                            for (int k = 0; k < nListSeq.getLength(); k++) {
                                nNode3 = nListSeq.item(k);
                                System.out.println("\nCurrent Element: " + nNode3.getNodeName());
                                if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
                                    Element elem3 = (Element) nNode3;
                                    // handle 1-n items of audio blocks:
                                    nListAudio = elem3.getElementsByTagName("audio");
                                    max_audios = nListAudio.getLength();
                                    for (int l = 0; l < max_audios; l++) {
                                        nNode4 = nListAudio.item(l);
                                        System.out.println("\nCurrent Element: " + nNode4.getNodeName());
                                        if (nNode4.getNodeType() == Node.ELEMENT_NODE) {
                                            elem4 = (Element) nNode4;
                                            if (l == 0) {
                                                audio_src = elem4.getAttribute("src");
                                                audio_id = elem4.getAttribute("id");
                                                clip_begin = elem4.getAttribute("clip-begin");
                                            }
                                            if (l == (max_audios-1))
                                                clip_end = elem4.getAttribute("clip-end");
                                        }
                                    }
                                }
                            }
                        }

                        sp = new SmilData_Par();
                        sp.setAudio_id(audio_id);
                        sp.setAudio_src(audio_src);
                        sp.setClip_begin(clip_begin);
                        sp.setEndsync(endsync);
                        sp.setClip_end(clip_end);
                        sp.setPar_id(par_id);
                        sp.setText_id(text_id);
                        sp.setText_src(text_src);
                        listSmilDataParItems.add(sp);
                        hashMap.put(text_id, sp);
                    }
                }
            }

            ?*
            Node node1 = elem.getElementsByTagName("firstname").item(0);
                    String fname = node1.getTextContent();

                    Node node2 = elem.getElementsByTagName("lastname").item(0);
                    String lname = node2.getTextContent();

                    Node node3 = elem.getElementsByTagName("occupation").item(0);
                    String occup = node3.getTextContent();

                    System.out.printf("User id: %s%n", uid);
                    System.out.printf("First name: %s%n", fname);
                    System.out.printf("Last name: %s%n", lname);
                    System.out.printf("Occupation: %s%n", occup);
                }
            }
            *?
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (org.xml.sax.SAXException saxE) {
            saxE.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        m_mp3File = new File(m_path +m_smilfilename.replace(".smil",".mp3"));
        if (!m_mp3File.exists())
            bMp3FileNoExists = true;
    }
    */

    public String getPath() {
        return m_path;
    }

    public String getSmilfilename() {
        return m_smilfilename;
    }

    public File getMp3File() {
        if (m_mp3File == null)
        {
            m_mp3File = new File(m_path + m_smilfilename.replace(".smil", ".mp3"));
            if (!m_mp3File.exists())
                bMp3FileNoExists = true;
        }
        return m_mp3File;
    }

    public boolean isbMp3FileNoExists() {
        return bMp3FileNoExists;
    }
}
