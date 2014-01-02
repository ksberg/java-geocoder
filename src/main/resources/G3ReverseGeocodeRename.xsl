<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!-- Copy all nodes that we dont want to handle specificly -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="GeocodeResponse/result/type">
        <xsl:element name="result_type">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <xsl:template match="GeocodeResponse/result/address_component/type">
        <xsl:element name="address_component_type">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>