package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.Collection;

/**
 *
 * @author Honza
 */
public interface DvdManager {
    
    public void createDvd(Dvd dvd);
    
    public void updateDvd(Dvd dvd);
    
    public void deleteDvd(Dvd dvd);
    
    public Dvd getDvdById(long id);
    
    public Collection<Dvd> getAllDvds();
    
    public Collection<Dvd> getDvdByType(Type type);
    
    public Collection<Dvd> getDvdByName(String name);
    
    
}
