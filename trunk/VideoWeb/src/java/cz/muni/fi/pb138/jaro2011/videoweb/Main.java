/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author radek
 */
public class Main {

    private static DvdManagerImpl dm;

    public static void main(String[] args) {
        try {
            dm = new DvdManagerImpl();

            //printDocument(dm.getAllDvds());

            //printDocument(dm.getDvdById(2));

            //printDocument(dm.getDvdByType("originál"));

            //printDocument(dm.getDvdByName("Simpsonovi"));
            
            //printDocument(dm.getDvdByTitle("Potápění"));
            
            

            System.out.println("Konec programu.");

        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLDBException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void printDocument(Document doc) {

        if (doc == null) {
            System.out.println("Prázdný dokument");
        } else {
            Element dvds = doc.getDocumentElement();
            NodeList dvdList = dvds.getElementsByTagName("dvd");

            for (int i = 0; i < dvdList.getLength(); i++) {        // prochazi dvd
                Element dvd = (Element) dvdList.item(i);         // vezme i-te dvd

                Element name = (Element) dvd.getElementsByTagName("name").item(0);
                System.out.println("Název dvd: " + name.getTextContent());

                Long id = Long.parseLong(dvd.getAttribute("id"));
                System.out.println("ID: " + id);

                Element type = (Element) dvd.getElementsByTagName("type").item(0);
                System.out.println("Typ dvd: " + type.getTextContent());

                Element titles = (Element) dvd.getElementsByTagName("titles").item(0);
                NodeList titleList = titles.getElementsByTagName("title");

                for (int j = 0; j < titleList.getLength(); j++) {
                    Element title = (Element) titleList.item(j);

                    Element titleName = (Element) title.getElementsByTagName("name").item(0);
                    System.out.println("Titul: " + titleName.getTextContent());

                    Element titleRepresentative = (Element) title.getElementsByTagName("representative").item(0);
                    System.out.println("Hl. představitel: " + titleName.getTextContent());

                }

            }
        }
    }

    private static void addDvd() throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        
        //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc  = docBuilder.newDocument();
            
            DocumentFragment df = doc.createDocumentFragment();

            ////////////////////////
            //Creating the XML tree

            //create the root element and add it to the document
            Element dvdLib = doc.createElement("dvd-library");
            df.appendChild(dvdLib);
            
            //create the root element and add it to the document
            Element dvd = doc.createElement("dvd");
            dvd.setAttribute("id", "111");
            dvdLib.appendChild(dvd);

            //create child element, add an attribute, and add to root
            Element name = doc.createElement("name");
            name.setTextContent("Top Gear");
            dvd.appendChild(name);
            
            Element type = doc.createElement("type");
            type.setTextContent("magazín");
            dvd.appendChild(type);

            Element titles = doc.createElement("titles");
            dvd.appendChild(titles);
            
            //first title
            Element title = doc.createElement("title");
            titles.appendChild(title);
            
            Element titleName =  doc.createElement("name");
            titleName.setTextContent("Top Gear Uncovered");
            title.appendChild(titleName);
            
            Element titleRep =  doc.createElement("representative");
            titleRep.setTextContent("Richard Hammond");
            title.appendChild(titleRep);
            
            // second title
            Element title1 = doc.createElement("title");
            titles.appendChild(title1);
            
            Element titleName1 =  doc.createElement("name");
            titleName1.setTextContent("The Italian Job");
            title1.appendChild(titleName1);
            
            Element titleRep1 =  doc.createElement("representative");
            titleRep1.setTextContent("Jeremy Clarkson");
            title1.appendChild(titleRep1);
            
            
            
            //printDocument(dm.createDvd(df));
            
    }
}
