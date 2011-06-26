package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.File;
import org.w3c.dom.Node;

/**
 *
 * @author Honza
 */
public interface VideoWebManager {

    public void addDvd(Dvd dvd);
    
    public void editDvd(Dvd dvd);
    
    public void deleteDvd(long dvdId);
    
    public String getDvdByTitle(String title);
    
    public Dvd getDvdById(long dvdId);
    
    public String getDvdByType(Type type);
    
    public String getAllDvds();
    
    public void importDvdsFromODF(File file);
    
    public int getDvdCount();
    
}
