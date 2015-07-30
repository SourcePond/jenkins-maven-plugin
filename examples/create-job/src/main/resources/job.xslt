<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output indent="yes" encoding="UTF-8" method="xml" />
	<xsl:param name="description" />
	<xsl:param name="giturl" />
	<xsl:param name="credentialsId" />
	<xsl:param name="groupId" />
	<xsl:param name="artifactId" />

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="/maven2-moduleset/description/text()">
		<xsl:value-of select="$description" />
	</xsl:template>

	<xsl:template
		match="/maven2-moduleset/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/url/text()">
		<xsl:value-of select="$giturl" />
	</xsl:template>

	<xsl:template
		match="/maven2-moduleset/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/credentialsId/text()">
		<xsl:value-of select="$credentialsId" />
	</xsl:template>

	<xsl:template match="/maven2-moduleset/rootModule/groupId/text()">
		<xsl:value-of select="$groupId" />
	</xsl:template>

	<xsl:template match="/maven2-moduleset/rootModule/artifactId/text()">
		<xsl:value-of select="$artifactId" />
	</xsl:template>

</xsl:stylesheet>