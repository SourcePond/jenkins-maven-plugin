# jenkins-maven-plugin
The Jenkins Maven Plugin allows to use the Jenkins CLI (command line interface) from within a Maven build. It allows the execution of any command supported by the CLI, see https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+CLI for further information. Therefore, it downloads the current jenkins-cli.jar from ${project.ciManagement.url}/jnlpJars/jenkins-cli.jar and uses that artifact for interacting with the Jenkins server. The architecture of the Jenkins Maven Plugin follows the UNIX philosophy "do one thing and do it well": it does not provide any logic for specific tasks, for instance creating job configuration files. This can be done with another, more appropriate plugin and its result can then be specified as stdin to the Jenkins Maven Plugin.

## Dependency
To use the Jenkins Maven Plugin in your project, add following plugin definition to the build part of your pom.xml:
```
<plugin>
	<groupId>ch.sourcepond.maven.plugins</groupId>
	<artifactId>jenkins-maven-plugin</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</plugin>
```
Note: Because the plugin is not released yet it's only available from https://oss.sonatype.org/content/repositories/snapshots

## Basic usage

| Name  | Required | Default | Description |
| ---------------------- | -------- | ------- | ----------- ||| workDirectory | yes | ${project.build.directory}/jenkins | Specifies where downloaded artifacts should be stored. |
| baseUrl | yes | ${project.ciManagement.url} | Specifies the URL where the Jenkins instance used by the plugin is available. |
| cliJar | yes | jnlpJars/jenkins-cli.jar | Specifies the relative path to baseUrl where the CLI-jar (necessary to run the plugin) can be downloaded. |
| command | yes | | Specifies the Jenkins command including all its options and arguments to be executed through the CLI. |
| stdin | no | | Specifies the file from where the standard input should read from. If set, the command receives the file data through stdin (for instance useful for "create job"). If not set, stdin does not provide any data. | Specifies the settings-id of the proxy-server (see https://maven.apache.org/guides/mini/guide-proxies.html) which the CLI should use to connect to the Jenkins instance. This parameter will be passed as "-p" option to the CLI. If set, the plugin will search for the appropriate proxy-server in the Maven settings (usually ~/.m2/settings.xml). |
| proxyId | no | | 
| noKeyAuth | no | false | Specifies, whether the CLI should skip loading the SSH authentication private key. This parameter will be passed as "-noKeyAuth" option to the CLI. Note: if set true, this setting conflicts with privateKey if privateKey is specified |
| privateKey | no | | Specifies the SSH authentication private key to be used when connecting to Jenkins. This parameter will be passed as "-i" option to the CLI. If not specified, the CLI will look for ~/.ssh/identity, ~/.ssh/id_dsa, ~/.ssh/id_rsa and those to authenticate itself against the server. Note: this setting conflicts with noKeyAuth if noKeyAuth is set true |
| noCertificateCheck | no | false | Specifies, whether certificate check should completely be disabled when the CLI connects to an SSL secured Jenkins instance. This parameter will be passed as "-noCertificateCheck" option to the CLI. This setting will bypass trustStore and trustStorePassword. Note: avoid enabling this switch because it's not secure (the CLI will trust everyone)! |
| trustStore | no | | Specifies the trust-store to be used by the CLI if it should connect to an SSL secured Jenkins instance. This parameter will be passed as "-Djavax.net.ssl.trustStore" option to the JVM which runs the CLI. |
| trustStorePassword | no | | Specifies the password for the trust-store to be used by the CLI trustStore. This parameter will be passed as "-Djavax.net.ssl.trustStorePassword" option to the JVM which runs the CLI. |