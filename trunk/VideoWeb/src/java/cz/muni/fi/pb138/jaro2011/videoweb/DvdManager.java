package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.OutputStream;

/**
 *
 * @author Honza
 */
public interface DvdManager {
    
    public void createDvd(Dvd dvd);
    
    public void updateDvd(Dvd dvd);
    
    public void deleteDvd(String name);
    
    public void deleteDvd(long id);
    
    public OutputStream getDvdById(long id);
    
    public OutputStream getAllDvds();
    
    public OutputStream getDvdByType(Type type);
    
    public OutputStream getDvdByName(String name);
    
    
}
