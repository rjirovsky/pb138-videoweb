/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        PrintWriter out = response.getWriter();

        if (request.getParameter("action").equals("home")) {
            String cc = "5";
            request.setAttribute("dvdCount", cc);
            
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
        if (request.getParameter("action").equals("library")) {
            showLibrary(request, response);
        } else {
            request.setAttribute("xmlFile", "chyba");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
        if (request.getParameter("action").equals("add"))
        {
            if (request.getMethod().equals("POST"))
            {
                int titleCounter = Integer.parseInt(request.getParameter("titleCounter"));
                
                String dvdName = request.getParameter("name");
                String typeString = request.getParameter("type");
                String titleName = null;
                String repreName = null;
                List<Track> trackList = new ArrayList<Track>();
                
                Dvd dvd = new Dvd();
                dvd.setName(dvdName);
                dvd.setType(Type.valueOf(typeString));
                
                Track track;
                
                for (int i = 1; i <= titleCounter; i++){        
                    track = new Track();
                    titleName = request.getParameter("titleName_"+i);
                    track.setName(titleName);
                    repreName = request.getParameter("titleRepresentative_"+i);
                    if (repreName == null || repreName.isEmpty()){
                        track.setLeadActor(null);
                    } else {
                        track.setLeadActor(repreName);
                    }
                    trackList.add(track);
                    repreName = null;
                }
                      
                dvd.setTrackList(trackList);
                
                
                try {
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
                } finally{
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }
            }
        }



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

    private void showLibrary(HttpServletRequest request, HttpServletResponse response){
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
}
