# Quick Overview
The Jenkins Maven Plugin allows to use the Jenkins CLI (command line interface) from within a Maven build. It allows the execution of any command supported by the CLI, see https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+CLI for further information. Therefore, it downloads the current jenkins-cli.jar from ${project.ciManagement.url}/jnlpJars/jenkins-cli.jar and uses that artifact for interacting with the Jenkins server. The architecture of the Jenkins Maven Plugin follows the UNIX philosophy "do one thing and do it well": it does not provide any logic for specific tasks, for instance creating job configuration files. This can be done with another, more appropriate plugin and its result can then be specified as stdin to the Jenkins Maven Plugin.

## Installation
To use the Jenkins Maven Plugin in your project, add following plugin definition to the build part of your pom.xml:
```
<plugin>
	<groupId>ch.sourcepond.maven.plugins</groupId>
	<artifactId>jenkins-maven-plugin</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</plugin>
```
Note: Because the plugin is not released yet it's only available from https://oss.sonatype.org/content/repositories/snapshots

## Configuration reference
The table below gives an overview about the parameters which can be specified. 

| Configuration element | Description |
| ---------------------- | ----------- |
| **jenkinscliDirectory** *(required)* | Specifies where the downloaded jenkins-cli.jar should be stored. Defaults to *${user.home}/.m2/jenkinscli* |
| **baseUrl** *(required)* | Specifies the URL where the Jenkins instance used by the plugin is available. Defaults to *${project.ciManagement.url}* |
| **cliJar** *(required)* | Specifies the relative path to baseUrl where the CLI-jar (necessary to run the plugin) can be downloaded. Defaults to *jnlpJars/jenkins-cli.jar* |
| **command** *(required)* | Specifies the Jenkins command including all its options and arguments to be executed through the CLI. |
| **customJenkinsCliJar** | Specifies a custom jenkins-cli.jar to be used by this plugin. If set, downloading jenkins-cli.jar from the Jenkins instance specified with *baseUrl* will completely be bypassed. |
| **stdin** | Specifies the file from where the standard input should read from. If set, the command receives the file data through stdin (for instance useful for "create job"). If not set, stdin does not provide any data. |
| **stdout** | Specifies the file where the standard output of the CLI should be written to. If set, the command sends the data received through stdout to the file specified (useful for example if the output of a command like "list-jobs" should be further processed). If not set, stdout is only written to the log. Note: if *append* is set to false (default) the target file will be replaced. |
| **append** | Specifies whether the target file defined by *stdout* should be replaced if existing. If set to true and the target file exists, all data will be appended to the existing file. If *stdout* is not set, this property has no effect. Defaults to *false* (overwrite file). |
| **proxyId** | Specifies the settings-id of the proxy-server which the CLI should use to connect to the Jenkins instance. This parameter will be passed as "-p" option to the CLI. If set, the plugin will search for the appropriate proxy-server in the Maven settings (usually ~/.m2/settings.xml, see https://maven.apache.org/guides/mini/guide-proxies.html) |
| **noKeyAuth** | Specifies, whether the CLI should skip loading the SSH authentication private key. This parameter will be passed as "-noKeyAuth" option to the CLI. Note: if set true, this setting conflicts with privateKey if privateKey is specified. Defaults to *false* |
| **privateKey** | Specifies the SSH authentication private key to be used when connecting to Jenkins. This parameter will be passed as "-i" option to the CLI. If not specified, the CLI will look for ~/.ssh/identity, ~/.ssh/id_dsa, ~/.ssh/id_rsa and those to authenticate itself against the server. Note: this setting conflicts with noKeyAuth if noKeyAuth is set true |
| **noCertificateCheck** | Specifies, whether certificate check should completely be disabled when the CLI connects to an SSL secured Jenkins instance. This parameter will be passed as "-noCertificateCheck" option to the CLI. This setting will bypass trustStore and trustStorePassword. Note: avoid enabling this switch because it's not secure (the CLI will trust everyone). Defaults to *false* |
| **trustStore** | Specifies the trust-store to be used by the CLI if it should connect to an SSL secured Jenkins instance. This parameter will be passed as "-Djavax.net.ssl.trustStore" option to the JVM which runs the CLI. If specified, a password must be set with configuration element *trustStorePassword*. |
| **trustStorePassword** | Specifies the password for the trust-store to be used by the CLI trustStore. This parameter will be passed as "-Djavax.net.ssl.trustStorePassword" option to the JVM which runs the CLI. According to keytool the password must be at least 6 characters. |

## Properties
The configuration described above can also be done through properties. Properties can be defined through the pom.xml, settings.xml, or, can be passed as command line arguments. Following table shows which configuration element corresponds to which property:

| Configuration element | Property |
| ---------------------- | ----------- |
| **jenkinscliDirectory** | jenkins.cliDirectory |
| **baseUrl** | jenkins.baseURL |
| **cliJar** | jenkins.cliJar |
| **command** | jenkins.command |
| **customJenkinsCliJar** | jenkins.customCliJar |
| **stdin** | jenkins.stdin |
| **stdout** | jenkins.stdout |
| **append** | jenkins.append |
| **proxyId** | jenkins.proxyId |
| **noKeyAuth** | jenkins.noKeyAuth |
| **privateKey** | jenkins.privateKey |
| **noCertificateCheck** | jenkins.noCertificateCheck |
| **trustStore** | jenkins.trustStore |
| **trustStorePassword** | jenkins.trustStorePassword |

## Examples
You can find the full source code of the examples below in the *examples* directory.

### Create a new job on Jenkins (examples/create-job)
```
<plugin>
	<groupId>net.sf.xsltmp</groupId>
	<artifactId>xslt-generator-maven-plugin</artifactId>
	<executions>
		<execution>
			<id>transform-config</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>many-to-many</goal>
			</goals>
			<configuration>
				<parameters>
					<description>${project.description}</description>
					<giturl>${project.scm.connection}</giturl>
					<credentialsId>${credentialsId}</credentialsId>
					<groupId>${project.groupId}</groupId>
					<artifactId>${project.artifactId}</artifactId>
				</parameters>
				<xslTemplate>${resources.directory}/${xslt.name}</xslTemplate>
				<srcDir>${resources.directory}</srcDir>
				<srcIncludes>**/config.xml</srcIncludes>
			</configuration>
		</execution>
	</executions>
</plugin>
<plugin>
	<groupId>ch.sourcepond.maven.plugins</groupId>
	<artifactId>jenkins-maven-plugin</artifactId>
	<executions>
		<execution>
			<id>create-job</id>
			<goals>
				<goal>cli</goal>
			</goals>
			<configuration>
				<stdin>${transformed.config}</stdin>
				<command>create-job ${project.artifactId}</command>
			</configuration>
		</execution>
	</executions>
</plugin>
```