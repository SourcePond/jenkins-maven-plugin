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

/**
 * @author Roland Hauser, SourcePond
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

	Settings getSettings();

	Path getWorkDirectory();

	URI getBaseUri();

	/**
	 * From <a
	 * href="https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+CLI">Jenkins
	 * cli Wiki</a>:
	 * 
	 * <pre>
	 * Whenever the CLI tries to to connect to the Jenkins server, it offers the
	 * before mentioned SSH keys. When the user has those keys but don't want
	 * use them to authenticate, preventing being prompted by the key's
	 * password, it's possible to use the -noKeyAuth argument. This way the CLI
	 * will never try to use the SSH keys available.
	 * </pre>
	 * 
	 * @return {@code true} if the {@code noKeyAuth} parameter has been
	 *         specified in the POM, {@code false} otherwise.
	 */
	boolean isNoKeyAuth();

	String getPrivateKeyOrNull();

	Proxy getProxyOrNull();

	String getCommand();

	Path getStdin();

	URI getCliJarUri();

	File getTrustStoreOrNull();

	String getTrustStorePasswordOrNull();

	String getDownloadedCliJar();

	void setDownloadedCliJar(String pDownloadedCliJarPath);

	boolean isNoCertificateCheck();
}
