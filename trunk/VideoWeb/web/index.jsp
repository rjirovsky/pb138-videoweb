<%--
   Document   : index
   Created on : Apr 20, 2011, 5:41:01 PM
   Author     : Adam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
   boolean actionSet = true;

   if(request.getParameter("action") == null){
       actionSet = false;
       response.sendRedirect("VideoWebServlet?action=home");
   }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
       <link href="inc/style.css" rel="stylesheet" type="text/css" media="screen" />
       <script type="text/javascript" language="Javascript" src="inc/script.js">
       </script>
       <title>VideoWeb</title>
   </head>
   <body>
       <div id="wrapper">
           <div id="head">
               <h1>VideoWeb<c:if test="${not empty title}"> - ${title}</c:if></h1>
           </div>
           <div id="menu">
               <h3>Nabídka</h3>
               <ul>
                   <li><a href="?action=home">Úvod</a></li>
                   <li><a href="VideoWebServlet?action=add">Přidat DVD</a></li>
                   <li><a href="VideoWebServlet?action=library">Seznam DVD</a></li>
                   <li><a href="VideoWebServlet?action=importODF">Import DVD</a></li>
               </ul>
           </div>
           <div id="content">
                <% if(actionSet){
                   if(request.getParameter("action").matches("home")){%>
                   <%@ include file="jspf/welcome.jspf"%>
               <%} if(request.getParameter("action").matches("add") || request.getParameter("action").matches("edit")){%>
                   <%@ include file="jspf/add.jspf"%>
               <%} if(request.getParameter("action").matches("library")||request.getParameter("action").matches("delete")){%>
                   <%@ include file="jspf/library.jspf"%>
               <%} if(request.getParameter("action").matches("importODF")){%>
                   <%@ include file="jspf/import.jspf"%>
               <%} }%>
           </div>
           <div id="footer">
               Copyright &copy; 2011 <a href="http://code.google.com/p/pb138-videoweb/" target="_blank">VideoWeb developer team</a>
           </div>
       </div>
   </body>
</html>