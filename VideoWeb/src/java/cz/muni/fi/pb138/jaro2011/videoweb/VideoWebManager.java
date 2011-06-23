package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.File;
import java.util.Collection;
import org.w3c.dom.Node;

/**
 *
 * @author Honza
 */
public interface VideoWebManager {

    public void addDvd(Dvd dvd);
    
    public void editDvd(Dvd dvd);
    
    public void deleteDvd(Dvd dvd);
    
    public Node getDvdByName(String name);
    
    public Node getDvdByType(Type type);
    
    public Node getAllDvds();
    
    public void importDvdsFromODF(File file);
    
}
