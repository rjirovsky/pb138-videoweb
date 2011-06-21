/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author radek
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            DvdManagerImpl dm = new DvdManagerImpl();
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLDBException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
