package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import org.exist.xmldb.XQueryService;
import org.w3c.dom.Document;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

/**
 * Manager for accessing eXist - native XML database via XML:DB API
 * 
 * @author Radek Jirovský
 */
public class DvdManagerImpl implements DvdManager {

    private static String dbURI = "xmldb:exist://localhost:8080/exist/xmlrpc/db";   //database location
    private Random id;

    /**
     * It sets drivers for database.
     * 
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws XMLDBException 
     */
    public DvdManagerImpl() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        //database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        id = new Random();
    }

    /**
     * Inserts new dvd to DB.
     * 
     * @param dvd   dvd to add (without ID)
     */
    @Override
    public void createDvd(Dvd dvd) {

        if (dvd == null) {
            throw new IllegalArgumentException("DVD cannot be null!");
        }
        if (dvd.getId() > 0) {
            throw new IllegalArgumentException("Id already set!");
        }

        // generating IDs
        long newId = id.nextInt(Integer.MAX_VALUE);
        while (getDvdById(newId) != null) {
            newId = id.nextInt(Integer.MAX_VALUE);
        }
        dvd.setId(newId);

        Collection col = null;
        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");

            String xmlDvd = dvdToXml(dvd);
            // XQuery insert
            CompiledExpression compiled = xqs.compile("update insert" + xmlDvd
                    + "into //dvd-library");
            ResourceSet result = xqs.execute(compiled);
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd bylo přidáno.");


        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při přidávání dvd!", ex);
            throw new RuntimeException("Chyba při přidávání dvd!", ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
        }

    }

    /**
     * Update existing dvd in DB.
     * 
     * @param dvd   dvd to update (with ID)
     */
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
            // XQuery replace
            CompiledExpression compiled = xqs.compile("update replace //dvd[@id = '" + dvd.getId() + "'] with " + dvdToXml(dvd));
            ResourceSet result = xqs.execute(compiled);

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při editaci dvd!", ex);
            throw new RuntimeException("Chyba při editaci dvd!", ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
        }
    }

    /**
     * Deletes dvd from DB by name.
     * 
     * @param name  string  dvd name
     */
    @Override
    public void deleteDvd(String name) {

        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");
            //XQuery delete
            CompiledExpression compiled = xqs.compile("update delete //dvd[name = '" + name + "']");
            ResourceSet result = xqs.execute(compiled);

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při mazání dvd!", ex);
            throw new RuntimeException("Chyba při mazání dvd!", ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
        }
    }

    /**
     * Deletes dvd from DB by ID.
     * 
     * @param id  long  dvd id
     */
    @Override
    public void deleteDvd(long id) {

        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");
            //XQuery delete
            CompiledExpression compiled = xqs.compile("update delete //dvd[@id = '" + id + "']");
            ResourceSet result = xqs.execute(compiled);

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při mazání dvd!", ex);
            throw new RuntimeException("Chyba při mazání dvd!", ex);
        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
        }
    }

    /**
     * It retrieves dvd with given id. 
     * It should be only one, but it is not nessesary.
     * 
     * @param id   id of dvd
     * @return returns Document with dvd by specified id
     */
    @Override
    public Document getDvdById(long id) {

        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            //XPath
            ResourceSet result = xpqs.query("//dvd[@id = " + id + "]");

            if (result.getSize() == 0) {
                Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Dvd nebylo nalezeno v DB!");
                return null;
            }

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Chyba při získávání všech dvd", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }
    }

    /**
     * Retrieves all dvds from DB
     * @return Document all dvds in xml
     */
    @Override
    public Document getAllDvds() {
        Collection col = null;
        Document allDvds = null;

        try {
            col = DatabaseManager.getCollection(dbURI);
            col.setProperty(OutputKeys.INDENT, "no");
            XMLResource xmlRes = (XMLResource) col.getResource("dvd.xml");      // ziska cely xml dokument

            allDvds = (Document) xmlRes.getContentAsDOM();                      // prevede na DOM Node (Document)

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dat z DB!", ex);
            throw new RuntimeException("Chyba při získávání dat z DB!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
            return allDvds;
        }
    }

    /**
     * It retrieves dvds with given type. 
     * 
     * @param type   type of dvd (Enum)
     * @return returns Document with all dvds with specified type
     */
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
            //XPath
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
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }
    }

    /**
     * It retrieves dvd with given name. 
     * 
     * @param name   name of dvd
     * @return returns Document with all dvds with specified name
     */
    @Override
    public Document getDvdByName(String name) {

        if (name == null) {
            throw new IllegalArgumentException("DVD cannot be null!");
        }
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            //XPath
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
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }
    }

    /**
     * It retrieves dvd with given name of title. 
     * 
     * @param title   titlename on dvd
     * @return returns Document with dvd by specified title
     */
    @Override
    public Document getDvdByTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("DVD cannot be null!");
        }
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            //XPath case insensitive
            ResourceSet result = xpqs.query("//dvd[titles/title/translate(name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZĚŠČŘŽÝÁÍÉŇÓŮÚŤĎ', 'abcdefghijklmnopqrstuvwxyzěščřžýáíéňóůúťď') = '" + title.toLowerCase() + "']");
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
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
            return dvd;
        }

    }

    /**
     * Method transforms Dvd entity to XML
     * 
     * @param dvd Dvd object
     * @return String with XML data
     */
    private String dvdToXml(Dvd dvd) {
        if (dvd == null) {
            return "";
        }

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
            if (title.getLeadActor() != null) {
                xmlDvd += "<representative>" + title.getLeadActor() + "</representative>";
            }
            xmlDvd += "</title>";
        }
        xmlDvd += "</titles></dvd>";

        return xmlDvd;
    }

    /**
     * It retrieve count of dvds in DB.
     * 
     * @return count
     */
    public int getDvdCount() {

        int count = 0;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            //XPath
            ResourceSet result = xpqs.query("count(//dvd)");

            ResourceIterator i = result.getIterator();
            Resource res = null;
            if (i.hasMoreResources()) {
                res = i.nextResource();
                String countStr =(String)res.getContent();
                count = Integer.parseInt(countStr);              
            }

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.INFO, "Záznam nebyl nalezen.", ex);

        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException ex) {
                    Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE,
                            "Chyba při uvolňování zdrojů!", ex);
                    throw new RuntimeException("Chyba při uvolňování zdrojů!", ex);
                }
            }
            return count;
        }

    }
}
