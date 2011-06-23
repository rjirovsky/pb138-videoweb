/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xmldb.api.base.XMLDBException;

/**
 *
 * @author Honza
 */
public class VideoWebServlet extends HttpServlet {

    private DvdManagerImpl dm;
    private static final String xsltFile = "http://localhost:8084/VideoWeb/transform.xsl";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PageAction pageAction = PageAction.home;
        try {
            pageAction = Enum.valueOf(PageAction.class, request.getParameter("action"));
        } catch (IllegalArgumentException ex) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (NullPointerException ex) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }

        switch (pageAction) {

            case home: {
                doHome(request, response);
                break;
            }
            case add: {
                doAdd(request, response);
                break;
            }
            case library: {
                doLibrary(request, response);
                break;
            }
            case delete: {
                doDeleteDvd(request, response);
            }
            case importODF: {
                doImport(request, response);
            }
            default: {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }

        }
    }

    private void doAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("POST")) {
            try {
                int titleCounter = Integer.parseInt(request.getParameter("titleCounter"));
                Dvd dvd = getDvdFromRequest(request, titleCounter);

                dm = new DvdManagerImpl();
                dm.createDvd(dvd);
                request.setAttribute("message", "Nové DVD uspěšně přidáno.");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                request.setAttribute("message", "Chyba při přidávání nového DVD.");
            } catch (InstantiationException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                request.setAttribute("message", "Chyba při přidávání nového DVD.");
            } catch (IllegalAccessException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                request.setAttribute("message", "Chyba při přidávání nového DVD.");
            } catch (XMLDBException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                request.setAttribute("message", "Chyba při přidávání nového DVD.");
            } finally {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    private Dvd getDvdFromRequest(HttpServletRequest request, int titleCounter) {
        String dvdName = request.getParameter("name");
        String typeString = request.getParameter("type");
        String titleName = null;
        String repreName = null;
        List<Track> trackList = new ArrayList<Track>();
        Dvd dvd = new Dvd();
        dvd.setName(dvdName);
        dvd.setType(Type.valueOf(typeString));
        Track track;
        for (int i = 1; i <= titleCounter; i++) {
            track = new Track();
            titleName = request.getParameter("titleName_" + i);
            track.setName(titleName);
            repreName = request.getParameter("titleRepresentative_" + i);
            if (repreName == null || repreName.isEmpty()) {
                track.setLeadActor(null);
            } else {
                track.setLeadActor(repreName);
            }
            trackList.add(track);
            repreName = null;
        }
        dvd.setTrackList(trackList);
        return dvd;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void doHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cc = "5";
        request.setAttribute("dvdCount", cc);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private void doLibrary(HttpServletRequest request, HttpServletResponse response) {
        try {
            dm = new DvdManagerImpl();

            Document doc = dm.getAllDvds();
            DOMSource domSource = new DOMSource(doc);

            String xmlFile = transformToString(domSource);
            request.setAttribute("xmlFile", xmlFile);
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLDBException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String transformToString(DOMSource domSource) {

        InputStream is = null;
        try {
            URL url = new URL(xsltFile);
            is = url.openStream();
            final StreamSource styleSource = new StreamSource(is);
            TransformerFactory tf = TransformerFactory.newInstance();
            Templates templates = tf.newTemplates(styleSource);
            Transformer transformer = templates.newTransformer();
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            transformer.transform(domSource, sr);
            return sw.toString();
        } catch (TransformerException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            return "Error with XML DB has been reached.";
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            return "Error with XML DB has been reached.";
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                return "Error with XML DB has been reached.";
            }
        }
    }

    private void doDeleteDvd(HttpServletRequest request, HttpServletResponse response) {
        try {
            long id = Long.parseLong(request.getParameter("Id"));
            dm = new DvdManagerImpl();
            dm.deleteDvd(id);
            request.setAttribute("message", "Dvd bylo úspěšně smazáno.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("message", "Chyba při mazání DVD.");
        } catch (InstantiationException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("message", "Chyba při mazání DVD.");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("message", "Chyba při mazání DVD.");
        } catch (XMLDBException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("message", "Chyba při mazání DVD.");
        } catch (NumberFormatException ex) {
            request.setAttribute("message", "Chyba při mazání DVD.");
        } finally {
            doLibrary(request, response);
        }
    }

    private void doImport(HttpServletRequest request, HttpServletResponse response) {
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
                List fileItemsList = servletFileUpload.parseRequest(request);

                FileItem fileItem = null;

                Iterator it = fileItemsList.iterator();
                while (it.hasNext()) {
                    FileItem fileItemTemp = (FileItem) it.next();
                    if (!fileItemTemp.isFormField()) {
                        fileItem = fileItemTemp;
                    }
                }

                if (fileItem != null) {
                    String fileName = fileItem.getName();

                    /* Save the uploaded file if its size is greater than 0. */
                    if (fileItem.getSize() > 0) {

                        String dirName = "";

                        File saveTo = new File(dirName + fileName);

                        fileItem.write(saveTo);

                        VideoWebManagerImpl vwm = new VideoWebManagerImpl();
                        vwm.importDvdsFromODF(saveTo);

                        request.setAttribute("message", "Soubor ODS úspěšně importován");


                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                request.setAttribute("message", "Chyba při nahrávání souboru.");
            }
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    private enum PageAction {

        home {

            @Override
            public String toString() {
                return "home";
            }
        },
        library {

            @Override
            public String toString() {
                return "library";
            }
        },
        add {

            @Override
            public String toString() {
                return "add";
            }
        },
        delete {

            @Override
            public String toString() {
                return "deleteId";
            }
        },
        edit {

            @Override
            public String toString() {
                return "editId";
            }
        },
        importODF {

            @Override
            public String toString() {
                return "importODF";
            }
        }
    }
}
