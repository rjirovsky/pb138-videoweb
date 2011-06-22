<%-- 
    Document   : index
    Created on : Apr 20, 2011, 5:41:01 PM
    Author     : Adam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
    boolean home = false;
    boolean actionSet = true;
    if(request.getParameter("action") == null || request.getParameter("action").matches("home")){
        home= true;
        actionSet = false;
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="inc/style.css" rel="stylesheet" type="text/css" media="screen" />
        <title>VideoWeb</title>
    </head>
    <body>
        <div id="wrapper">
            <div id="head">
                <h1>VideoWeb</h1>
            </div>
            <div id="menu">
                <h3>Nabídka</h3>
                <ul>
                    <li><a href="?action=home">Úvod</a></li>
                    <li><a href="?action=add">Přidat DVD</a></li>
                    <li><a href="?action=library">Seznam DVD</a></li>
                    <li><a href="?action=import">Import DVD</a></li>
                </ul>
            </div>
            <div id="content">
                <%if (home){%>
                    <%@ include file="WEB-INF/jspf/welcome.jspf"%>
                <%} if(actionSet){
                    if(request.getParameter("action").matches("add")){%>
                    <%@ include file="WEB-INF/jspf/add.jspf"%>
                <%} if(request.getParameter("action").matches("library")){%>
                    <%@ include file="WEB-INF/jspf/library.jspf"%>
                <%} if(request.getParameter("action").matches("import")){%>
                    <%@ include file="WEB-INF/jspf/import.jspf"%>
                <%} }%>
            </div>
            <div id="footer">
                PATA
            </div>

        </div>
    </body>
</html>
