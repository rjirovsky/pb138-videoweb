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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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
                break;
            }
            case importODF: {
                doImport(request, response);
                break;
            }
            case edit: {
                doEdit(request, response);
                break;
            }
            default: {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }

        }
    }

    private void doAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("POST")) {
            try {
                dm = new DvdManagerImpl();
                // check if we are updating dvd and delete the previous version
                if(request.getParameter("editedID")!=null){
                    dm.deleteDvd(Long.parseLong(request.getParameter("editedID").toString()));
                }
                int titleCounter = Integer.parseInt(request.getParameter("titleCounter"));
                Dvd dvd = getDvdFromRequest(request, titleCounter);

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
        try {
            dm = new DvdManagerImpl();
            String cc = "" + dm.getDvdCount();
            request.setAttribute("dvdCount", cc);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
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

    private void doLibrary(HttpServletRequest request, HttpServletResponse response) {
        if (request.getMethod().equals("GET")) {
            try {
                dm = new DvdManagerImpl();

                Document doc = dm.getAllDvds();

                int count = getDvdCountFromDocument(doc);
                request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

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
        } else {
            if (request.getParameter("search").equals("type")) {
                try {
                    String typeString = request.getParameter("type");
                    Type typeToSearch = Type.valueOf(typeString);
                    dm = new DvdManagerImpl();

                    Document doc = dm.getDvdByType(typeToSearch);

                    int count = getDvdCountFromDocument(doc);
                    request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

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
            } else {
                try {
                    String searchString = request.getParameter("title");
                    dm = new DvdManagerImpl();
                    Document doc = null;
                    if (searchString.isEmpty()) {
                        doc = dm.getAllDvds();
                    } else {
                        doc = dm.getDvdByTitle(searchString);
                    }

                    int count = getDvdCountFromDocument(doc);
                    request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

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
            Document doc = dm.getAllDvds();

            int count = getDvdCountFromDocument(doc);
            request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

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
        }
    }

    private void doImport(HttpServletRequest request, HttpServletResponse response) {

        if (request.getMethod().equals("POST")) {
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
                    try {
                        Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
                        request.setAttribute("message", "Chyba při nahrávání souboru.");
                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                    } catch (ServletException ex1) {
                        Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (IOException ex1) {
                        Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }
        try {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    private void doEdit(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getParameter("action") != null) {
                if (request.getParameter("action").matches("edit") && request.getParameter("Id") != null) {
                    dm = new DvdManagerImpl();
                    Dvd myDvd = new Dvd();
                    myDvd = parseDvd(dm.getDvdById(Long.parseLong(request.getParameter("Id").toString())));
                    request.setAttribute("editID", myDvd.getId());
                    request.setAttribute("name", myDvd.getName());
                    Map tempTrack;
                    ArrayList list = new ArrayList();
                    for (int i = 0; i < myDvd.getTrackList().size(); i++) {
                        tempTrack = new HashMap();
                        tempTrack.put("name", myDvd.getTrackList().get(i).getName());
                        tempTrack.put("actor", myDvd.getTrackList().get(i).getLeadActor());
                        list.add(tempTrack);
                    }
                    request.setAttribute("tracks", myDvd.getTrackList().size());
                    request.setAttribute("tracklist", list);

                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }

            }
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLDBException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Dvd parseDvd(Document dvdXML) {
        Dvd myDvd = new Dvd();
        myDvd.setName(dvdXML.getElementsByTagName("name").item(0).getTextContent());
        myDvd.setId(Long.parseLong(dvdXML.getElementsByTagName("dvd").item(0).getAttributes().getNamedItem("id").getTextContent()));
        NodeList titles = dvdXML.getElementsByTagName("title");
        List<Track> trackList = new ArrayList<Track>();
        Track track;
        for (int i = 0; i < titles.getLength(); i++) {
            track = new Track();
            track.setName(titles.item(i).getChildNodes().item(1).getTextContent());
            if(titles.item(i).getChildNodes().item(3)!=null)
                track.setLeadActor(titles.item(i).getChildNodes().item(3).getTextContent());
            
            trackList.add(track);
        }
        myDvd.setTrackList(trackList);
        return myDvd;
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

    private int getDvdCountFromDocument(Document doc) {
        int count = 0;
        if (doc != null) {
            NodeList dvdNodeList = doc.getElementsByTagName("dvd");
            count = dvdNodeList.getLength();
        }
        return count;
    }
}
