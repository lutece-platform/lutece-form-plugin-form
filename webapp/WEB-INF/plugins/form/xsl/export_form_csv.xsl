<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text"/>

<xsl:template match="/">
	<xsl:apply-templates select="form/form-entries/form-entry" />
	<xsl:text>&#10;</xsl:text>
	<xsl:apply-templates select="form/submits/submit"/>
</xsl:template>

<xsl:template match="form/form-entries/form-entry">
	<xsl:text>"</xsl:text>
	<xsl:value-of select="form-entry-title" /> 
	<xsl:text>";</xsl:text>
</xsl:template>

<xsl:template match="form/submits/submit">
	<xsl:variable name="submit-id" select="submit-id" />
	<xsl:for-each select="../../form-entries/form-entry">
		<xsl:variable name="form-entry-id" select="form-entry-id" />
		<xsl:choose>
			<xsl:when test="string(../../submits/submit[submit-id=$submit-id]/questions/question[question-id=$form-entry-id])">
				<xsl:apply-templates select="../../submits/submit[submit-id=$submit-id]/questions/question[question-id=$form-entry-id]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>"";</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
	<xsl:text>&#10;</xsl:text>
</xsl:template>

<xsl:template match="questions/question">
	<xsl:apply-templates select="responses"/> 
</xsl:template>

<xsl:template match="questions/question/responses">
	<xsl:text>"</xsl:text>
	<xsl:apply-templates select="response"/>
	<xsl:text>";</xsl:text>
</xsl:template>

<xsl:template match="questions/question/responses/response">
	<xsl:value-of select="."/>
	<xsl:if test="position()!=last()">
		<xsl:text>;</xsl:text>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
