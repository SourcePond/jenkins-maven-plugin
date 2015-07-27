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
	 * Returns the work-directory specified through mojo parameter
	 * <em>workDirectory</em>.
	 * 
	 * @return Work-directory, never {@code null}
	 */
	Path getWorkDirectory();

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

	Path getStdin();

	URI getCliJarUri();

	File getTrustStoreOrNull();

	String getTrustStorePasswordOrNull();

	String getDownloadedCliJar();

	void setDownloadedCliJar(String pDownloadedCliJarPath);

	boolean isNoCertificateCheck();
}
