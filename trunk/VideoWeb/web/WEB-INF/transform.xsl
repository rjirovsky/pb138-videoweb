<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : address-book.xsl
    Created on : April 30, 2011, 8:40 PM
    Author     : Adam
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" encoding="utf-8"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->

    <xsl:template match="dvd-library">
        <html>
            <head>
                <link href="inc/style.css" rel="stylesheet" type="text/css" media="screen" /> 
                <title>Knihovna DVD</title>
            </head>
            <body>
                <h1>Knihovna DVD</h1>
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="dvd">
        <div class="dvd">
            <div class="head">
                <div class="name">
                    <xsl:value-of select="name"/>
                </div>
                <div class="type">
                    <xsl:value-of select="type"/>
                </div>
            </div>
            <ul>
                <xsl:apply-templates select="titles"/>
            </ul>
            <xsl:element name="a">
                <xsl:attribute name="href">#<xsl:value-of select="@id"/></xsl:attribute>
                <img src="inc/edit.png" alt="edit"/>
            </xsl:element>
            <xsl:element name="a">
                <xsl:attribute name="href">#<xsl:value-of select="@id"/></xsl:attribute>
                <img src="inc/del.png" alt="delete"/>
            </xsl:element>
        </div>
    </xsl:template>
    
    <xsl:template match="title">
        <li>
            <xsl:value-of select="name"/>
            <xsl:if test="representative"> (
                <b>
                    <xsl:value-of select="representative"/>
                </b>)
            </xsl:if>
        </li>
    </xsl:template>

</xsl:stylesheet>
