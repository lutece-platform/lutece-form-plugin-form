<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" indent="yes" cdata-section-elements="response question-title form-title"/>
<xsl:template match="/">
 <xsl:apply-templates select="form"/> 
</xsl:template>

<xsl:template match="form">
	<form>
		<form-title>
			<xsl:value-of select="form-title"/>
		</form-title>
		<submits>
			<xsl:apply-templates select="submits/submit"/> 
		</submits>
	</form>	
</xsl:template>

<xsl:template match="submit">
	<submit>
		<submit-id>
			<xsl:value-of select="submit-id"/>
		</submit-id>
		<submit-date>
			<xsl:value-of select="submit-date"/>
		</submit-date>
		<submit-ip>
			<xsl:value-of select="submit-ip"/>
		</submit-ip>
		<questions>
			<xsl:apply-templates select="questions/question"/> 
		</questions>
	</submit>
</xsl:template>
<xsl:template match="question">
	<question>
		<question-title>
			<xsl:value-of select="question-title"/>
		</question-title>
		<responses>
			<xsl:apply-templates select="responses/response"/> 
		</responses>
	</question>
</xsl:template>
<xsl:template match="response">
	<response>
		<xsl:value-of select="."/>
	</response>
</xsl:template>
</xsl:stylesheet>