package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import org.exist.xmldb.XQueryService;
import org.w3c.dom.Document;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

/**
 *
 * @author Honza
 */
public class DvdManagerImpl implements DvdManager {

    private static String dbURI = "xmldb:exist://localhost:8080/exist/xmlrpc/db";

    public DvdManagerImpl() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        //database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);
    }

    @Override
    public void createDvd(Dvd dvd) {

        if (dvd == null) {
            throw new IllegalArgumentException("DVD cannot be null!");
        }

        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");

            String xmlDvd = dvdToXml(dvd);
            CompiledExpression compiled = xqs.compile("update insert" + xmlDvd
                    + "into //dvd-library");
            ResourceSet result = xqs.execute(compiled);

            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
            }

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
        }

    }

    @Override
    public void updateDvd(Dvd dvd) {
        if (dvd == null) {
            throw new IllegalArgumentException("DVD cannot be null!");
        }

        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");


            CompiledExpression compiled = xqs.compile("update replace //dvd[@id = '" + dvd.getId() + "'] with " + dvdToXml(dvd));
            ResourceSet result = xqs.execute(compiled);

            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
            }

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
        }
    }

    @Override
    public void deleteDvd(String name) {

        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");


            CompiledExpression compiled = xqs.compile("update delete //dvd[name = '" + name + "']");
            ResourceSet result = xqs.execute(compiled);
            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
            }

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při mazání dvd!", ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
        }
    }

    @Override
    public void deleteDvd(long id) {

        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");


            CompiledExpression compiled = xqs.compile("update delete //dvd[@id = '" + id + "']");
            ResourceSet result = xqs.execute(compiled);
            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
            }

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při mazání dvd!", ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
        }
    }

    @Override
    public Document getDvdById(long id) {

        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[@id = " + id + "]");

            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
                return null;
            }

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Záznam nebyl nalezen.", ex);

        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }

    }

    @Override
    public Document getAllDvds() {
        Collection col = null;
        Document allDvds = null;

        try {
            col = DatabaseManager.getCollection(dbURI);
            col.setProperty(OutputKeys.INDENT, "no");
            XMLResource xmlRes = (XMLResource) col.getResource("dvd.xml");      // ziska cely xml dokument

            allDvds = (Document) xmlRes.getContentAsDOM();                      // prevede na DOM Node (Document)
            //System.out.println("XML získáno z DB.");

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dat z DB!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
            return allDvds;
        }
    }

    @Override
    public Document getDvdByType(Type type) {
        
        
    if (type == null) {
            throw new IllegalArgumentException("Type cannot be null!");
        }

        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[type = '" + type.toString() + "']");

            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Žádné dvd nebylo nalezeno v DB!");
                return null;
            }

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dvd dle typu!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }
    }

    @Override
    public Document getDvdByName(String name) {
        
        if (name == null){
            throw new IllegalArgumentException("DVD cannot be null!");
        }
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[name = '" + name + "']");

            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
                return null;
            }

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();


        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dvd dle názvu!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }
    }

    @Override
    public Document getDvdByTitle(String title) {
        if (title == null){
            throw new IllegalArgumentException("DVD cannot be null!");
        }
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[titles/title/name = '" + title + "']");
            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
                return null;
            }

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();
        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dvd dle titulu!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }

    }

    private String dvdToXml(Dvd dvd) {
        if (dvd == null) return "";
        
        String xmlDvd;
        xmlDvd = "<dvd id=\"" + dvd.getId() + "\">"
                + "<name>" + dvd.getName() + "</name>"
                + "<type>" + dvd.getType() + "</type>"
                + "<titles>";

        List<Track> titles = dvd.getTrackList();
        for (int i = 0; i < titles.size(); i++) {
            Track title = titles.get(i);
            xmlDvd += "<title>"
                     + "<name>" + title.getName() + "</name>";
            if (title.getLeadActor() != null){
                    xmlDvd +="<representative>" + title.getLeadActor() + "</representative>";
            }
            xmlDvd +="</title>";
        }
        xmlDvd +="</titles></dvd>";

        return xmlDvd;
    }
}
