# jenkins-maven-plugin
The Jenkins Maven Plugin allows to use the Jenkins CLI (command line interface) from within a Maven build. It allows the execution of any command supported by the CLI, see https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+CLI for further information. Therefore, it downloads the current jenkins-cli.jar from ${project.ciManagement.url}/jnlpJars/jenkins-cli.jar and uses that artifact for interacting with the Jenkins server. The architecture of the Jenkins Maven Plugin follows the UNIX philosophy "do one thing and do it well": it does not provide any logic for specific tasks, for instance creating job configuration files. This can be done with another, more appropriate plugin and its result can then be specified as stdin to the Jenkins Maven Plugin.

## Dependency
To use the Jenkins Maven Plugin in your project add following element to your pom.xml:
```
<plugin>
	<groupId>ch.sourcepond.maven.plugins</groupId>
	<artifactId>jenkins-maven-plugin</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</plugin>
```
Note: Because the plugin is not released yet it's only available from https://oss.sonatype.org/content/repositories/snapshots