/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.maven.plugin.jenkins.config.download;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * Facility to download the Jenkins CLI jar which is necessary to run this
 * plugin.
 *
 */
public interface Downloader {

	/**
	 * Downloads the Jenkins CLI jar ({@link Config#getCliJarUri()}) and stores
	 * it in the work directory ( {@link Config#getJenkinscliDirectory()}).
	 * 
	 * @param pLog
	 *            The Maven log, must not be {@code null}
	 * @param pValidatedConfig
	 *            The config object, must not be {@code null}
	 * @return Absolute path to the jar file, never {@code null}.
	 * @throws MojoExecutionException
	 *             Thrown, if the JAR could not be downloaded or stored in the
	 *             work directory.
	 */
	String downloadCliJar(Log pLog, Config pValidatedConfig)
			throws MojoExecutionException;
}
