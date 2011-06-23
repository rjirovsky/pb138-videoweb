/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.w3c.dom.Document;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author Honza
 */
public class VideoWebManagerImpl implements VideoWebManager {
     
    private DvdManagerImpl dvdManager;
    
    /**
     * Constructor of VideoWebManagerImpl class 
     */
     public VideoWebManagerImpl()  {
        try {
            dvdManager = new DvdManagerImpl();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLDBException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    @Override
    /**
     * Adds new DVD
     * 
     * @param dvd DVD to add 
     */
    public void addDvd(Dvd dvd) {
        dvdManager.createDvd(dvd);
    }

    @Override
    /**
     * Edits a DVD
     * 
     * @param dvd DVD to edit
     */
    public void editDvd(Dvd dvd) {
        dvdManager.updateDvd(dvd);
    }

    @Override
    /**
     * Deletes a DVD
     * 
     * @param dvd DVD to delete 
     */
    public void deleteDvd(Dvd dvd) {
        dvdManager.deleteDvd(dvd.getId());
    }

    @Override
    /**
     * Find DVDs with certain movie
     * 
     * @param name Name of movie to find
     * @return Found DVDs
     */
    public Document getDvdByName(String name) {
        return dvdManager.getDvdByName(name);
    }

    @Override
    /**
     * Finds DVDs with certain type
     * 
     * @param type Type of DVDs to find
     * @return Found DVDs
     */
    public Document getDvdByType(Type type) {
        return dvdManager.getDvdByType(type);
    }

    @Override
    /**
     * Returns all DVDs
     * 
     * @return All DVDs
     */
    public Document getAllDvds() {
        return dvdManager.getAllDvds();
    }

    @Override
    /**
     * Imports DVDs from ODF spreadsheet 
     * 
     * @param name Name of spreadsheet to load 
     */
    public void importDvdsFromODF(String name) {
        
        try {
            OdfDocument odfDoc = OdfDocument.loadDocument(name);                // loads whole document
            List<OdfTable> OdfTables = new ArrayList(odfDoc.getTableList());
            
            for (int k = 0; k < OdfTables.size(); k++) {        // loading of single sheet
                OdfTable table = OdfTables.get(k);

                for (int i = 1; i < table.getRowCount(); i++) {   // reading of data from single row (single DVD)
                    Dvd dvd = new Dvd();
                    List<Track> list = new ArrayList<Track>();
                    dvd.setName(table.getCellByPosition(0, i).getDisplayText());    // getting a name of DVD
                    
                    // getting a type of DVD
                    if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("originál")) {  
                        dvd.setType(Type.ORIGINAL);
                    }
                    if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("časopis")) {
                        dvd.setType(Type.MAGAZINE);
                    }
                    if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("domácí")) {
                        dvd.setType(Type.HOME);
                    }
                    if (table.getCellByPosition(1, i).getDisplayText().equalsIgnoreCase("kopie")) {
                        dvd.setType(Type.COPY);
                    }                    

                    for (int j = 2; j < table.getColumnCount(); j = j + 2) {    // getting title and leading actor from single row (single DVD)
                        Track track = new Track();
                        if (!table.getCellByPosition(j, i).getStringValue().isEmpty()) {    // check if cell is not empty
                            track.setName(table.getCellByPosition(j, i).getStringValue());
                 
                            if (!table.getCellByPosition(j + 1, i).getStringValue().isEmpty()) {        // check if actor's cell is not empty
                                track.setLeadActor(table.getCellByPosition(j + 1, i).getStringValue());
                            }
                            list.add(track);
                        }
                    }
                    dvd.setTrackList(list);

                    /*System.out.println("");       //used for testing
                    System.out.println("");

                    System.out.println(dvd.getName());
                    System.out.println(dvd.getType());
                    System.out.println(dvd.getTrackList().size());
                    System.out.println(dvd.getTrackList().get(0).getName());                    
                    System.out.println(dvd.getTrackList().get(1).getName());
                    */
                    this.addDvd(dvd);
                    
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }           
    
}
