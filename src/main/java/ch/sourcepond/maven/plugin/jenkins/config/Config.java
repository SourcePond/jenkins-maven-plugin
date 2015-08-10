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
package ch.sourcepond.maven.plugin.jenkins.config;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.CliMojo;

/**
 * Provides access to all parameters passed to {@link CliMojo}. A {@link Config}
 * instance should only be created by {@link CliMojo#execute()}.
 *
 */
public interface Config {

	/**
	 * Determines whether SSL is used for connecting to Jenkins. If this method
	 * returns {@code true} {@link #getTrustStoreOrNull()} and
	 * {@link #getTrustStorePasswordOrNull()} must return valid non-null values.
	 * 
	 * @return {@code true} if a secure connection is used, {@code false}
	 *         otherwise.
	 */
	boolean isSecure();

	/**
	 * Returns the {@link Settings} instance passed by Maven to {@link CliMojo}.
	 * 
	 * @return Settings, never {@code null}
	 */
	Settings getSettings();

	/**
	 * Returns the download directory of jenkins-cli.jar specified through mojo
	 * parameter <em>jenkinscliDirectory</em>.
	 * 
	 * @return Work-directory, never {@code null}
	 */
	Path getJenkinscliDirectory();

	/**
	 * Returns the custom jenkins-cli.jar to be used by this plugin; specified
	 * through mojo parameter <em>customJenkinsCliJar</em>.
	 * 
	 * @return Custom jenkins-cli.jar or {@code null}
	 */
	File getCustomJenkinsCliJarOrNull();

	/**
	 * Returns the base URL specified through mojo parameter <em>baseUrl</em>.
	 * 
	 * @return Base URL, never {@code null}
	 */
	URI getBaseUri();

	/**
	 * Returns whether private key loading is skipped. Specified through mojo
	 * parameter <em>noKeyAuth</em>
	 * 
	 * @return {@code true} if loading is skipped, {@code false} otherwise.
	 */
	boolean isNoKeyAuth();

	/**
	 * Returns whether SSL certificate check is skipped entirely. Specified
	 * through mojo parameter <em>noCertificateCheck</em>
	 * 
	 * @return {@code true} if certificate check is skipped, {@code false}
	 *         otherwise.
	 */
	boolean isNoCertificateCheck();

	/**
	 * Returns the SSH authentication private key specified through mojo
	 * parameter <em>privateKey</em>.
	 * 
	 * @return Private key or {@code null}
	 */
	String getPrivateKeyOrNull();

	/**
	 * Returns the proxy from the Maven settings which has been specified
	 * through mojo parameter <em>proxyId</em>.
	 * 
	 * @return Proxy definition or {@code null}
	 */
	Proxy getProxyOrNull();

	/**
	 * Returns the command to be executed by the CLI specified through mojo
	 * parameter <em>command</em>.
	 * 
	 * @return Command, never {@code null}
	 */
	String getCommand();

	/**
	 * Returns the file to be used as stdin by the CLI; specified through mojo
	 * parameter <em>stdin</em>.
	 * 
	 * @return Path to stdin or {@code null}
	 */
	Path getStdinOrNull();

	/**
	 * Returns the XSLT file to be applied on the stdin; specified through mojo
	 * parameter <em>stdinXslt</em>.
	 * 
	 * @return Path to XSLT or {@code null}
	 */
	Path getStdinXsltOrNull();

	/**
	 * Returns the file to be used as stdout by the CLI; specified through mojo
	 * parameter <em>stdout</em>.
	 * 
	 * @return Path to stdout or {@code null}
	 */
	Path getStdoutOrNull();

	/**
	 * Returns the XSLT file to be applied on the stdout; specified through mojo
	 * parameter <em>stdoutXslt</em>.
	 * 
	 * @return Path to XSLT or {@code null}
	 */
	Path getStdoutXsltOrNull();

	/**
	 * Returns whether the standard output of the CLI should be appended to the
	 * target file. Specified through mojo parameter <em>append</em>.
	 * 
	 * @return {@code true} if appending, {@code false} if overwriting.
	 */
	boolean isAppending();

	/**
	 * Returns the download link of the jenkins-cli.jar to be used by the
	 * plugin. The cli-uri is the concatenation of the required mojo parameters
	 * <em>baseUrl</em> and <em>cliJar</em>.
	 * 
	 * @return Cli-jar URI, never {@code null}.
	 */
	URI getCliJarUri();

	/**
	 * Returns the trust-store to be used by the CLI specified through mojo
	 * parameter <em>trustStore</em>.
	 * 
	 * @return Trust-store or {@code null}
	 */
	File getTrustStoreOrNull();

	/**
	 * Returns the password of the trust-store to be used by the CLI. Specified
	 * through mojo parameter <em>trustStorePassword</em>.
	 * 
	 * @return Password or {@code null}
	 */
	String getTrustStorePasswordOrNull();

	/**
	 * Returns the absolute path to the local copy of the jenkins-cli.jar. The
	 * file is downloaded from the URI returned by {@link #getCliJarUri()}. The
	 * download is be performed during building this config (see
	 * {@link ConfigBuilder#build(org.apache.maven.plugin.logging.Log)}).
	 * 
	 * @return Absolute path, never {@code null}
	 */
	String getDownloadedCliJar();
}
