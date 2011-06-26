<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : address-book.xsl
    Created on : April 30, 2011, 8:40 PM
    Author     : Adam
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns="" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xml:lang="en" version="1.0">
    <xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes"/>

    <xsl:template match="/">        
        <xsl:choose>
            <xsl:when test="count(//dvd) = 0">
                <h2>Nebyly nalezeny žádné DVD.</h2>
            </xsl:when>
            <xsl:otherwise> 
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="dvd">
        <div class="dvd">
            <div class="head">
                <div class="name">
                    <xsl:attribute name="id">dvd<xsl:value-of select="@id"/></xsl:attribute>
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
                <xsl:attribute name="href">VideoWebServlet?action=edit&#38;Id=<xsl:value-of select="@id"/></xsl:attribute>
                <img src="inc/edit.png" alt="edit" />
            </xsl:element>
            <xsl:element name="a">
                <xsl:attribute name="href">VideoWebServlet?action=delete&#38;Id=<xsl:value-of select="@id"/></xsl:attribute>
                <xsl:attribute name="onclick">return confirmDelete(<xsl:value-of select="@id"/>)</xsl:attribute>
                <img src="inc/del.png" alt="delete" />
            </xsl:element>
        </div>
    </xsl:template>

    <xsl:template match="title">
        <li>
            <xsl:value-of select="name"/>
            <xsl:if test="representative">
                (<b><xsl:value-of select="representative"/></b>)
            </xsl:if>
        </li>
    </xsl:template>

</xsl:stylesheet>