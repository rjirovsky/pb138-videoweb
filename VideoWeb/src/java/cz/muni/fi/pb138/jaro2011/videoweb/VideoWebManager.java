package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.File;
import java.util.Collection;

/**
 *
 * @author Honza
 */
public interface VideoWebManager {

    public boolean addDvd(Dvd dvd);
    
    public boolean editDvd(Dvd dvd);
    
    public boolean deleteDvd(Dvd dvd);
    
    public Collection<Dvd> searchDvdByName(String name);
    
    public Collection<Dvd> searchDvdByType(Type type);
    
    public Collection<Dvd> searchAllDvds();
    
    public boolean importFilmsFromODF(File input);
    
}
