/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author Jakub Kutil
 */
public class VideoWebManagerImpl implements VideoWebManager {

    private DvdManagerImpl dvdManager;
    private static final String xsltFile = "http://localhost:8084/VideoWeb/transform.xsl";

    /**
     * Constructor of VideoWebManagerImpl class 
     */
    public VideoWebManagerImpl() throws RuntimeException {
        try {
            dvdManager = new DvdManagerImpl();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
            throw new RuntimeException(ex.getMessage());
        } catch (InstantiationException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
            throw new RuntimeException(ex.getMessage());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
            throw new RuntimeException(ex.getMessage());
        } catch (XMLDBException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }
   
    /**
     * Adds new DVD
     * 
     * @param dvd DVD to add 
     */
    @Override
    public void addDvd(Dvd dvd) {
        dvdManager.createDvd(dvd);
    }

    /**
     * Edits a DVD
     * 
     * @param dvd DVD to edit
     */
    @Override
    public void editDvd(Dvd dvd) {
        dvdManager.updateDvd(dvd);
    }

    /**
     * Deletes a DVD
     * 
     * @param dvd DVD to delete 
     */
    @Override
    public void deleteDvd(long dvdId) {
        dvdManager.deleteDvd(dvdId);
    }

    /**
     * Find DVDs with certain movie
     * 
     * @param title Name of movie to find
     * @return Found DVDs
     */
    @Override
    public String getDvdByTitle(String title) {
        Document doc = dvdManager.getDvdByTitle(title);
        DOMSource domSource = new DOMSource(doc);
        String xmlFile = transformToString(domSource);
        return xmlFile;
    }

    /**
     * Gets DVD by id
     * 
     * @param dvdId Id of DVD
     * @return DVD with requested id
     */
    @Override
    public Dvd getDvdById(long dvdId) { 
        Dvd dvd = new Dvd();
        Document doc = dvdManager.getDvdById(dvdId);
        dvd = parseDvd(doc);
        return dvd;
    }
    
    /**
     * Parses document with DVDs
     * 
     * @param dvdXML document with DVDs
     * @return DVD parsed DVD
     */
    private Dvd parseDvd(Document dvdXML) {
        Dvd myDvd = new Dvd();
        myDvd.setName(dvdXML.getElementsByTagName("name").item(0).getTextContent());
        myDvd.setId(Long.parseLong(dvdXML.getElementsByTagName("dvd").item(0).getAttributes().getNamedItem("id").getTextContent()));
        NodeList titles = dvdXML.getElementsByTagName("title");
        List<Track> trackList = new ArrayList<Track>();
        Track track;
        for (int i = 0; i < titles.getLength(); i++) {
            track = new Track();
            track.setName(titles.item(i).getChildNodes().item(1).getTextContent());
            if (titles.item(i).getChildNodes().item(3) != null) {
                track.setLeadActor(titles.item(i).getChildNodes().item(3).getTextContent());
            }

            trackList.add(track);
        }
        myDvd.setTrackList(trackList);
        return myDvd;
    }
    
    /**
     * Finds DVDs with certain type
     * 
     * @param type Type of DVDs to find
     * @return Found DVDs
     */
    @Override
    public String getDvdByType(Type type) {
        Document doc = dvdManager.getDvdByType(type);
        DOMSource domSource = new DOMSource(doc);
        String xmlFile = transformToString(domSource);
        return xmlFile;
    }

    /**
     * Returns all DVDs
     * 
     * @return All DVDs
     */
    @Override
    public String getAllDvds() {        
        Document doc = dvdManager.getAllDvds();
        DOMSource domSource = new DOMSource(doc);
        String xmlFile = transformToString(domSource);
        return xmlFile; 
    }
    /**
     * Transforms DOMSource to string
     * 
     * @param domSource DOMSource to transform
     * @return transformed string
     */
    private String transformToString(DOMSource domSource) {
        InputStream is = null;
        try {
            URL url = new URL(xsltFile);
            is = url.openStream();
            final StreamSource styleSource = new StreamSource(is);
            TransformerFactory tf = TransformerFactory.newInstance();
            Templates templates = tf.newTemplates(styleSource);
            Transformer transformer = templates.newTransformer();
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            transformer.transform(domSource, sr);
            return sw.toString();
        } catch (TransformerException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            return "Error with XML DB has been reached.";
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            return "Error with XML DB has been reached.";
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                return "Error with XML DB has been reached.";
            }
        }
    }
    
    /**
     * Returns the sum of dvds.
     * 
     * @return sum of dvds.
     */
    @Override
    public int getDvdCount() {
        return dvdManager.getDvdCount();
    }

    /**
     * Imports DVDs from ODF spreadsheet 
     * 
     * @param file File to load 
     */
    @Override
    public void importDvdsFromODF(File file) throws RuntimeException {
        
        try {
            OdfDocument odfDoc = OdfDocument.loadDocument(file);                // loads whole document
            List<OdfTable> OdfTables = new ArrayList(odfDoc.getTableList());

            for (int k = 0; k < OdfTables.size(); k++) {        // loading of single sheet
                OdfTable table = OdfTables.get(k);

                for (int i = 1; i < table.getRowCount(); i++) { // reading of data from single row (single DVD)
                    int type = 0;
                    if (!table.getCellByPosition(0, i).getDisplayText().isEmpty()) {    // testing if name cell is not empty
                        Dvd dvd = new Dvd();
                        List<Track> list = new ArrayList<Track>();
                        dvd.setName(table.getCellByPosition(0, i).getDisplayText());    // getting a name of DVD

                        // getting a type of DVD
                        if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("originál")) {
                            dvd.setType(Type.ORIGINAL);
                            type = 1;
                        }
                        if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("časopis")) {
                            dvd.setType(Type.MAGAZINE);
                            type = 1;
                        }
                        if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("domácí")) {
                            dvd.setType(Type.HOME);
                            type = 1;
                        }
                        if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("kopie")) {
                            dvd.setType(Type.COPY);
                            type = 1;
                        }

                        if (type == 1) {
                            for (int j = 2; j < table.getColumnCount(); j = j + 2) {    // getting title and leading actor from single row (single DVD)
                                Track track = new Track();
                                if (!table.getCellByPosition(j, i).getDisplayText().isEmpty()) {    // check if cell is not empty
                                    track.setName(table.getCellByPosition(j, i).getDisplayText());

                                    if (!table.getCellByPosition(j + 1, i).getDisplayText().isEmpty()) {        // check if actor's cell is not empty
                                        track.setLeadActor(table.getCellByPosition(j + 1, i).getDisplayText());
                                    }
                                    list.add(track);
                                }
                            }
                            dvd.setTrackList(list);
                            this.addDvd(dvd);
                            }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při importu z ODF spreadsheetu", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }    
}
