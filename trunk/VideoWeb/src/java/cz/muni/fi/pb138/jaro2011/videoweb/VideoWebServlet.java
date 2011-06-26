/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.jaro2011.videoweb;

import java.io.File;
import java.io.IOException;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Honza
 */
public class VideoWebServlet extends HttpServlet {

    private VideoWebManager vm;
    private static final String xsltFile = "http://localhost:8084/VideoWeb/transform.xsl";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        
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
                doAddGet(request, response);
                break;
            }
            case library: {
                doLibraryGet(request, response);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        
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
                doAddPost(request, response);
                break;
            }
            case library: {
                doLibraryPost(request, response);
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

    private void doAddPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // check if we are updating dvd and delete the previous version
            if (request.getParameter("editedID") != null) {
                vm.deleteDvd(Long.parseLong(request.getParameter("editedID").toString()));
            }
            int titleCounter = Integer.parseInt(request.getParameter("titleCounter"));
            Dvd dvd = getDvdFromRequest(request, titleCounter);

            vm.addDvd(dvd);
            request.setAttribute("message", "Nové DVD uspěšně přidáno.");
        } finally {
            try {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            } catch (ServletException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void doAddGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDvdByTitle(HttpServletRequest request, HttpServletResponse response) {
        try {
            String searchString = request.getParameter("title");
            String xmlFile = null;
            if (searchString.isEmpty()) {
                xmlFile = vm.getAllDvds();
            } else {
                xmlFile = vm.getDvdByTitle(searchString);
            }

            int count = getDvdCountFromDocumentString(xmlFile);
            request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

            request.setAttribute("xmlFile", xmlFile);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDvdByType(HttpServletRequest request, HttpServletResponse response) {
        try {
            String typeString = request.getParameter("type");
            Type typeToSearch = Type.valueOf(typeString);

            String xmlFile = vm.getDvdByType(typeToSearch);

            int count = getDvdCountFromDocumentString(xmlFile);
            request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

            request.setAttribute("xmlFile", xmlFile);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
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
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void doHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cc = "" + vm.getDvdCount();
        request.setAttribute("dvdCount", cc);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private void doLibraryGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String xmlFile = vm.getAllDvds();

            int count = getDvdCountFromDocumentString(xmlFile);
            request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

            request.setAttribute("xmlFile", xmlFile);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doLibraryPost(HttpServletRequest request, HttpServletResponse response) {

        if (request.getParameter("search").equals("type")) {
            getDvdByType(request, response);
        } else {
            getDvdByTitle(request, response);
        }

    }

    private void doDeleteDvd(HttpServletRequest request, HttpServletResponse response) {
        try {
            long id = Long.parseLong(request.getParameter("Id"));
            vm.deleteDvd(id);
            request.setAttribute("message", "Dvd bylo úspěšně smazáno.");
            String xmlFile = vm.getAllDvds();

            int count = getDvdCountFromDocumentString(xmlFile);
            request.setAttribute("countDvdMessage", "Nalezeno " + count + " dvd.");

            request.setAttribute("xmlFile", xmlFile);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        vm = new VideoWebManagerImpl();
    }

    private void doEdit(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getParameter("action") != null) {
                if (request.getParameter("action").matches("edit") && request.getParameter("Id") != null) {
                    Dvd myDvd = new Dvd();
                    myDvd = vm.getDvdById(Long.parseLong(request.getParameter("Id").toString()));
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
        } catch (ServletException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoWebServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getDvdCountFromDocumentString(String doc) {
        int count = 0;
        int index = 0;
        String strToSearch = "<div class=\"dvd\">";
        String parseString = doc;
        while (parseString.length() > 0) {
            index = parseString.indexOf(strToSearch);
            if (index != -1) {
                parseString = parseString.substring(index + 4);
                count++;
            } else {
                break;
            }
        }
        return count;
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
