/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

/**
 *
 * @author Honza
 */
public class VideoWebManagerImpl implements VideoWebManager {

    @Override
    public boolean addDvd(Dvd dvd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean editDvd(Dvd dvd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteDvd(Dvd dvd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Dvd> getDvdByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Dvd> getDvdByType(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Dvd> getAllDvds() {
        throw new UnsupportedOperationException("Not supported yet.");
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
