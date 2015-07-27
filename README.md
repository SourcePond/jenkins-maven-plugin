# jenkins-maven-plugin
The Jenkins Maven Plugin allows the usage of the Jenkins CLI (command line interface) from within a Maven build. It allows the execution of any command supported by the CLI, see https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+CLI for further information. Therefore, it downloads the the current jenkins-cli.jar from ${project.ciManagement.url}/jnlpJars/jenkins-cli.jar and uses that artifact for interacting with the Jenkins server. The architecture of the Jenkins Maven Plugin follows the UNIX philosophy "do one thing and do it well": it does not provide any logic for specific tasks, for instance creating job configuration files. This can be done with another, more appropriate plugin and its result can then be specified as stdin to the Jenkins Maven Plugin.
