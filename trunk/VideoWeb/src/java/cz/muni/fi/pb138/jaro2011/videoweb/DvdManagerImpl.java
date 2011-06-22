package cz.muni.fi.pb138.jaro2011.videoweb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import org.exist.xmldb.XQueryService;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
        Collection col = null;

        try {

            col = DatabaseManager.getCollection(dbURI);
            XQueryService xqs = (XQueryService) col.getService("XQueryService", "1.0");
            xqs.setProperty("indent", "yes");



            CompiledExpression compiled = xqs.compile("xquery");

            ResourceSet result = xqs.execute(compiled);


        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        }

    }

    @Override
    public void updateDvd(Dvd dvd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteDvd(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteDvd(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
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

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    System.err.println("Chyba při získávání dat z DB! " + xe);
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
            System.out.println("XML získáno z DB.");

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    System.err.println("Chyba při získávání dat z DB! " + xe);
                }
            }
            return allDvds;
        }
    }

    @Override
    public Document getDvdByType(String type) {
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[type = '" + type + "']");

            XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

            dvd = (Document) xmlRes.getContentAsDOM();

        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dvd dle typu!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    System.err.println("Chyba při získávání dat z DB! " + xe);
                }
            }
            return dvd;
        }
    }

    @Override
    public Document getDvdByName(String name) {
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[name = '" + name + "']");
            if (result.getSize() > 0) {
                XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

                dvd = (Document) xmlRes.getContentAsDOM();
            } else {
                return null;
            }
        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dvd dle názvu!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    System.err.println("Chyba při získávání dat z DB! " + xe);
                }
            }
            return dvd;
        }
    }

    @Override
    public Document getDvdByTitle(String title) {
        Document dvd = null;
        Collection col = null;
        try {
            col = DatabaseManager.getCollection(dbURI);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query("//dvd[titles/title/name = '" + title + "']");
            if (result.getSize() > 0) {
                XMLResource xmlRes = (XMLResource) result.getMembersAsResource();

                dvd = (Document) xmlRes.getContentAsDOM();
            } else {
                return null;
            }
        } catch (XMLDBException ex) {
            Logger.getLogger(DvdManagerImpl.class.getName()).log(Level.SEVERE, "Chyba při získávání dvd dle titulu!", ex);
        } finally {
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    System.err.println("Chyba při získávání dat z DB! " + xe);
                }
            }
            return dvd;
        }

    }
}
