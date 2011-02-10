<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text"/>
<xsl:template match="/">
 <xsl:apply-templates select="form/submits/submit"/> 
</xsl:template>

<xsl:template match="form/submits/submit"><xsl:if test="position()=1">
      <xsl:apply-templates select="questions/question"/> 
	    <xsl:text>&#10;</xsl:text>
   </xsl:if>
   <xsl:apply-templates select="questions/question/responses"/> 
	    <xsl:text>&#10;</xsl:text>
 
 </xsl:template>
	
 <xsl:template match="questions/question">"<xsl:value-of select="question-title"/> 
	<xsl:text>";</xsl:text>
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