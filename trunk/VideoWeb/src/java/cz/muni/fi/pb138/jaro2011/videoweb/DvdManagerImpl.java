package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.OutputStream;
import javax.xml.transform.OutputKeys;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 *
 * @author Honza
 */
public class DvdManagerImpl implements DvdManager {

    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db";

    public DvdManagerImpl() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {

        final String driver = "org.exist.xmldb.DatabaseImpl";
// initialize database driver

        Class cl = Class.forName(driver);

        Database database = (Database) cl.newInstance();

        database.setProperty("create-database", "true");

        DatabaseManager.registerDatabase(database);



        Collection col = null;

        XMLResource res = null;

        try {

// get the collection

            col = DatabaseManager.getCollection(URI);

            col.setProperty(OutputKeys.INDENT, "no");

            res = (XMLResource) col.getResource("dvd.xml");



            if (res == null) {

                System.out.println("document not found!");

            } else {

                System.out.println(res.getContent());

            }

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
    public void createDvd(Dvd dvd) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public OutputStream getDvdById(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream getAllDvds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream getDvdByType(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream getDvdByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
