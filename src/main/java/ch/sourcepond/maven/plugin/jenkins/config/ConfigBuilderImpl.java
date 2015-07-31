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
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * Default implementation of the {@link ConfigBuilder} interface.
 *
 */
final class ConfigBuilderImpl implements ConfigBuilder {
	static final String HTTPS = "https";
	private final ConfigImpl config;
	private final Downloader downloader;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param pMessages
	 *            Facade to get messages, must not be {@code null}
	 * @param pDownloader
	 *            Facade to download the CLI jar, must not be {@code null}
	 */
	ConfigBuilderImpl(final Messages pMessages, final Downloader pDownloader) {
		config = new ConfigImpl(pMessages);
		downloader = pDownloader;
	}

	/**
	 * Returns the base config instance which will cloned when {@link #build()}
	 * is called. This method is for internal usage only!
	 * 
	 * @return {@link ConfigImpl} instance, never {@code null}
	 */
	ConfigImpl getBaseConfig() {
		return config;
	}

	/**
	 * @param pDownloadedCliJarPath
	 */
	private void setDownloadedCliJar(final String pDownloadedCliJarPath) {
		config.setDownloadedCliJar(pDownloadedCliJarPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setWorkDirectory
	 * (java.io.File)
	 */
	@Override
	public ConfigBuilder setJenkinscliDirectory(final Path pWorkDirectory)
			throws MojoExecutionException {
		config.setJenkinscliDirectory(pWorkDirectory);
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
			config.setBaseUri(pBaseUrl.toURI());
			final String cliPath = pCliUri.startsWith("/") ? pCliUri : "/"
					+ pCliUri;
			config.setCliJarUri(new URL(pBaseUrl.toString() + cliPath).toURI());
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
		config.setNoKeyAuth(pNoKeyAuth);
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
		config.setNoCertificateCheck(pNoCertificateCheck);
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
			config.setPrivateKey(pPrivateKeyOrNull.getAbsolutePath());
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
		config.setCommand(pCommand);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#build()
	 */
	@Override
	public Config build(final Log pLog) throws MojoExecutionException {
		assert getBaseConfig().getBaseUri() != null : "baseUri is null";
		assert getBaseConfig().getCliJarUri() != null : "cliJarUri is null";
		assert !isBlank(getBaseConfig().getCommand()) : "command is null";
		assert getBaseConfig().getSettings() != null : "settings is null";
		assert getBaseConfig().getJenkinscliDirectory() != null : "jenkinscliDirectory is null";

		// If a local, custom jenkins-cli.jar has been defined, bypass any
		// dowload...
		if (getBaseConfig().getCustomJenkinsCliJarOrNull() != null) {
			setDownloadedCliJar(getBaseConfig().getCustomJenkinsCliJarOrNull()
					.getAbsolutePath());
		} else { // ...otherwise dowload jenkins-cli.jar from target Jenkins
					// instance.
			setDownloadedCliJar(downloader
					.downloadCliJar(pLog, getBaseConfig()));
		}
		getBaseConfig().validate(pLog);

		return (Config) config.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setStdin(java
	 * .io.File)
	 */
	@Override
	public ConfigBuilder setStdin(final File pStdin) {
		if (pStdin != null) {
			config.setStdin(pStdin.toPath());
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setStdout(java
	 * .io.File)
	 */
	@Override
	public ConfigBuilder setStdout(final File pStdout) {
		if (pStdout != null) {
			config.setStdout(pStdout.toPath());
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setAppend(boolean
	 * )
	 */
	@Override
	public ConfigBuilder setAppend(final boolean pAppend) {
		config.setAppending(pAppend);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setProxy(org.
	 * apache.maven.settings.Proxy)
	 */
	@Override
	public ConfigBuilder setProxy(final Proxy pProxy) {
		config.setProxy(pProxy);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setSettings(org
	 * .apache.maven.settings.Settings)
	 */
	@Override
	public ConfigBuilder setSettings(final Settings pSettings) {
		config.setSettings(pSettings);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#setTrustStore
	 * (java.io.File)
	 */
	@Override
	public ConfigBuilder setTrustStore(final File pTrustStore) {
		config.setTrustStore(pTrustStore);
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
		config.setTrustStorePassword(pPassword);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder#
	 * setCustomJenkinsCliJar(java.io.File)
	 */
	@Override
	public ConfigBuilder setCustomJenkinsCliJar(final File pCustomJenkinsCliJar) {
		config.setCustomJenkinsCliJarOrNull(pCustomJenkinsCliJar);
		return this;
	}
}
