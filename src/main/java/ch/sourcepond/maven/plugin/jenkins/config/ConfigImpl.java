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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * Default implementation of the {@link Config} interface.
 * 
 */
final class ConfigImpl implements Config, Cloneable {
	static final String CONFIG_VALIDATION_NO_KEY_AUTH_AND_PRIVATE_KEY_SET = "config.validation.noKeyAuthAndPrivateKeySet";
	static final String HTTPS = "https";
	private final Messages messages;
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
	 * Creates a new instance of this class.
	 * 
	 * @param pMessages
	 *            pMessages Facade to get messages, must not be {@code null}
	 */
	ConfigImpl(final Messages pMessages) {
		messages = pMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		final ConfigImpl clone = new ConfigImpl(messages);
		clone.setBaseUri(getBaseUri());
		clone.setCliJarUri(getCliJarUri());
		clone.setCommand(getCommand());
		clone.setDownloadedCliJar(getDownloadedCliJar());
		clone.setNoCertificateCheck(isNoCertificateCheck());
		clone.setNoKeyAuth(isNoKeyAuth());
		clone.setPrivateKey(getPrivateKeyOrNull());
		clone.setProxy(getProxyOrNull());
		clone.setSettings(getSettings());
		clone.setStdin(getStdinOrNull());
		clone.setTrustStore(getTrustStoreOrNull());
		clone.setTrustStorePassword(getTrustStorePasswordOrNull());
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getWorkDirectory()
	 */
	@Override
	public Path getWorkDirectory() {
		return workDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getBaseUri()
	 */
	@Override
	public URI getBaseUri() {
		return baseUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#isNoKeyAuth()
	 */
	@Override
	public boolean isNoKeyAuth() {
		return noKeyAuth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getPrivateKeyOrNull()
	 */
	@Override
	public String getPrivateKeyOrNull() {
		return privateKeyOrNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getCommand()
	 */
	@Override
	public String getCommand() {
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getCliJarUri()
	 */
	@Override
	public URI getCliJarUri() {
		return cliJarUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getDownloadedCliJar()
	 */
	@Override
	public String getDownloadedCliJar() {
		return dowloadedCliJar;
	}

	/**
	 * Sets the absolute path to the downloaded CLI jar. See
	 * {@link Downloader#downloadCliJar(Config)}
	 */
	void setDownloadedCliJar(final String pDownloadedCliJarPath) {
		dowloadedCliJar = pDownloadedCliJarPath;
	}

	/**
	 * See {@link ConfigBuilder#setBaseUrl(java.net.URL, String)}
	 */
	void setBaseUri(final URI pBaseUri) {
		baseUri = pBaseUri;
	}

	/**
	 * Sets the concatenation of the base {@link URI} and the relative CLI jar
	 * path. See {@link ConfigBuilder#setBaseUrl(java.net.URL, String)}.
	 * 
	 * @param pCliJarUri
	 *            {@link URI} must not be {@code null}
	 */
	void setCliJarUri(final URI pCliJarUri) {
		cliJarUri = pCliJarUri;
	}

	/**
	 * @param pNoKeyAuth
	 */
	void setNoKeyAuth(final boolean pNoKeyAuth) {
		noKeyAuth = pNoKeyAuth;
	}

	/**
	 * @param pNoCertificateCheck
	 */
	void setNoCertificateCheck(final boolean pNoCertificateCheck) {
		noCertificateCheck = pNoCertificateCheck;
	}

	/**
	 * @param pPrivateOrNull
	 */
	void setPrivateKey(final String pPrivateOrNull) {
		privateKeyOrNull = pPrivateOrNull;
	}

	/**
	 * @param pCommand
	 */
	void setCommand(final String pCommand) {
		command = pCommand;
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

	/**
	 * @param pStdin
	 */
	void setStdin(final Path pStdin) {
		stdin = pStdin;
	}

	@Override
	public Proxy getProxyOrNull() {
		return proxy;
	}

	/**
	 * @param pProxy
	 */
	void setProxy(final Proxy pProxy) {
		proxy = pProxy;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param pSettings
	 */
	void setSettings(final Settings pSettings) {
		settings = pSettings;
	}

	@Override
	public File getTrustStoreOrNull() {
		return trustStore;
	}

	@Override
	public String getTrustStorePasswordOrNull() {
		return trustStorePassword;
	}

	/**
	 * @param pTrustStore
	 */
	void setTrustStore(final File pTrustStore) {
		trustStore = pTrustStore;
	}

	/**
	 * @param pPassword
	 */
	void setTrustStorePassword(final String pPassword) {
		trustStorePassword = pPassword;
	}

	/**
	 * @param pWorkDirectory
	 */
	void setWorkDirectory(final Path pWorkDirectory) {
		workDirectory = pWorkDirectory;
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

	/**
	 * @throws MojoExecutionException
	 */
	public void validate() throws MojoExecutionException {
		if (isNoKeyAuth() && getPrivateKeyOrNull() != null) {
			throw new MojoExecutionException(messages.getMessage(
					CONFIG_VALIDATION_NO_KEY_AUTH_AND_PRIVATE_KEY_SET,
					getPrivateKeyOrNull()));
		}
	}
}
