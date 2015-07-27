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

import static java.nio.file.Files.createDirectories;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * @author rolandhauser
 *
 */
final class ConfigBuilderImpl implements ConfigBuilder {
	static final String HTTPS = "https";
	private Path workDirectory;
	private URI baseUri;
	private URI cliJarUri;
	private boolean noKeyAuth;
	private String privateKeyOrNull;
	private String command;
	private Proxy proxy;
	private String dowloadedCliJar;
	private Path stdin;
	private Settings settings;
	private boolean noCertificateCheck;
	private File trustStore;
	private String trustStorePassword;

	/**
	 * @return
	 */
	@Override
	public Path getWorkDirectory() {
		return workDirectory;
	}

	/**
	 * @return
	 */
	@Override
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * @return
	 */
	@Override
	public boolean isNoKeyAuth() {
		return noKeyAuth;
	}

	/**
	 * @return
	 */
	@Override
	public String getPrivateKeyOrNull() {
		return privateKeyOrNull;
	}

	/**
	 * @return
	 */
	@Override
	public String getCommand() {
		return command;
	}

	/**
	 * @return
	 */
	@Override
	public URI getCliJarUri() {
		return cliJarUri;
	}

	@Override
	public String getDownloadedCliJar() {
		return dowloadedCliJar;
	}

	@Override
	public void setDownloadedCliJar(final String pDownloadedCliJarPath) {
		dowloadedCliJar = pDownloadedCliJarPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setWorkDirectory
	 * (java.io.File)
	 */
	@Override
	public ConfigBuilder setWorkDirectory(final Path pWorkDirectory)
			throws MojoExecutionException {
		workDirectory = pWorkDirectory;
		try {
			createDirectories(pWorkDirectory);
		} catch (final IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setBaseUrl(java
	 * .net.URL)
	 */
	@Override
	public ConfigBuilder setBaseUrl(final URL pBaseUrl, final String pCliUri)
			throws MojoExecutionException {
		try {
			baseUri = pBaseUrl.toURI();
			final String cliPath = pCliUri.startsWith("/") ? pCliUri : "/"
					+ pCliUri;
			cliJarUri = new URL(pBaseUrl.toString() + cliPath).toURI();
		} catch (final URISyntaxException | MalformedURLException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setNoKeyAuth(
	 * boolean)
	 */
	@Override
	public ConfigBuilder setNoKeyAuth(final boolean pNoKeyAuth) {
		noKeyAuth = pNoKeyAuth;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setNoCertificateCheck
	 * (boolean)
	 */
	@Override
	public ConfigBuilder setNoCertificateCheck(final boolean pNoCertificateCheck) {
		noCertificateCheck = pNoCertificateCheck;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setPrivateKey
	 * (java.io.File)
	 */
	@Override
	public ConfigBuilder setPrivateKey(final File pPrivateKeyOrNull) {
		if (pPrivateKeyOrNull != null) {
			privateKeyOrNull = pPrivateKeyOrNull.getAbsolutePath();
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setCommand(java
	 * .lang.String)
	 */
	@Override
	public ConfigBuilder setCommand(final String pCommand) {
		command = pCommand;
		return this;
	}

	// TODO: Add validations here
	@Override
	public Config build() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#isNoCertificateCheck()
	 */
	@Override
	public boolean isNoCertificateCheck() {
		return noCertificateCheck;
	}

	@Override
	public Path getStdinOrNull() {
		return stdin;
	}

	@Override
	public ConfigBuilder setStdin(final File pStdin) {
		if (pStdin != null) {
			stdin = pStdin.toPath();
		}
		return this;
	}

	@Override
	public Proxy getProxyOrNull() {
		return proxy;
	}

	@Override
	public ConfigBuilder setProxy(final Proxy pProxy) {
		proxy = pProxy;
		return this;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	@Override
	public ConfigBuilder setSettings(final Settings pSettings) {
		settings = pSettings;
		return this;
	}

	@Override
	public File getTrustStoreOrNull() {
		return trustStore;
	}

	@Override
	public String getTrustStorePasswordOrNull() {
		return trustStorePassword;
	}

	@Override
	public ConfigBuilder setTrustStore(final File pTrustStore) {
		trustStore = pTrustStore;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setTrustStorePassword
	 * (java.lang.String)
	 */
	@Override
	public ConfigBuilder setTrustStorePassword(final String pPassword) {
		trustStorePassword = pPassword;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#isSecure()
	 */
	@Override
	public boolean isSecure() {
		return HTTPS.equals(getBaseUri().getScheme());
	}
}
