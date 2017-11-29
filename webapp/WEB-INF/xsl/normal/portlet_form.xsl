<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>
    <xsl:template match="portlet/form-portlet/form-portlet-content">
        <div class="portlet -lutece-border-radius append-bottom">
            <xsl:for-each select="category-form">
                <xsl:variable name="color">
                    <xsl:value-of select="category-color"/>
                </xsl:variable>
                <div class="category color_{$color}">
                    <div class="category-title">
                        <xsl:value-of select="category-title"/>
                    </div>
                </div>
            </xsl:for-each>
            <ul class="list_form">
                <xsl:for-each select="form">
                    <xsl:variable name="url_form">jsp/site/Portal.jsp?page=form&amp;id_form=<xsl:value-of select="form-id"/></xsl:variable>
                    <li>
                        <a href="{$url_form}">
                            <xsl:value-of select="form-title"/>
                        </a>
                    </li>
                </xsl:for-each>
            </ul>
        </div>
    </xsl:template>
    <xsl:template match="/|*">
        <xsl:apply-templates select="portlet/form-portlet/form-portlet-content" />
    </xsl:template>
</xsl:stylesheet>
