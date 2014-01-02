<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text"/>
    <xsl:template match="GeocodeResponse">
        <xsl:apply-templates select="result"/>
    </xsl:template>
    <xsl:template match="result">
        <xsl:apply-templates select="address_component"/>
        <xsl:apply-templates select="geometry"/>
    </xsl:template>
    <xsl:template match="address_component">
        <xsl:value-of select="type"/>=<xsl:value-of select="short_name"/><xsl:text>&#10;</xsl:text>
    </xsl:template>
    <xsl:template match="geometry">
        <xsl:apply-templates select="location"/>
    </xsl:template>
    <xsl:template match="location">
        lat=<xsl:value-of select="lat"/><xsl:text>&#10;</xsl:text>
        lng=<xsl:value-of select="lng"/><xsl:text>&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>