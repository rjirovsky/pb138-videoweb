package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Honza
 */
public class Dvd {
    private long id;
    private String name;
    private Type type;
    private List<Track> trackList;
    
    public Dvd(){
        trackList = new ArrayList<Track>();
    }
    
    public long getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public Type getType(){
        return type;
    }
    
    public List<Track> getTrackList(){
        return trackList;
    }
    public void setId(long id){
        this.id = id;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setType(Type type){
        this.type = type;
    }
    
    public void setTrackList(List<Track> trackList){
        this.trackList = trackList;
    }
    
}
