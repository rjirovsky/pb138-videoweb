/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.File;
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
 * @author Jakub Kutil
 */
public class VideoWebManagerImpl implements VideoWebManager {

    private DvdManagerImpl dvdManager;

    /**
     * Constructor of VideoWebManagerImpl class 
     */
    public VideoWebManagerImpl() {
        try {
            dvdManager = new DvdManagerImpl();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
        } catch (XMLDBException ex) {
            Logger.getLogger(VideoWebManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při vytváření dvdManageru", ex);
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
    public void deleteDvd(Dvd dvd) {
        dvdManager.deleteDvd(dvd.getId());
    }

    /**
     * Find DVDs with certain movie
     * 
     * @param name Name of movie to find
     * @return Found DVDs
     */
    @Override
    public Document getDvdByName(String name) {
        return dvdManager.getDvdByName(name);
    }

    /**
     * Finds DVDs with certain type
     * 
     * @param type Type of DVDs to find
     * @return Found DVDs
     */
    @Override
    public Document getDvdByType(Type type) {
        return dvdManager.getDvdByType(type);
    }

    /**
     * Returns all DVDs
     * 
     * @return All DVDs
     */
    @Override
    public Document getAllDvds() {
        return dvdManager.getAllDvds();
    }

    /**
     * Imports DVDs from ODF spreadsheet 
     * 
     * @param name Name of spreadsheet to load 
     */
    @Override
    public void importDvdsFromODF(File file) {
        
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
        }
    }    
}
