package cz.muni.fi.pb138.jaro2011.videoweb;

import org.w3c.dom.Node;

/**
 *
 * @author Honza
 */
public interface DvdManager {
    
    public void createDvd(Dvd dvd);
    
    public void updateDvd(Dvd dvd);
    
    public void deleteDvd(String name);
    
    public void deleteDvd(long id);
    
    public Node getDvdById(long id);
    
    public Node getAllDvds();
    
    public Node getDvdByType(Type type);
    
    public Node getDvdByName(String name);
    
    public Node getDvdByTitle(String title);
    
    
}
