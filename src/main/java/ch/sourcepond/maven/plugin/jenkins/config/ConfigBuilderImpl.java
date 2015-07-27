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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;

/**
 * @author rolandhauser
 *
 */
final class ConfigBuilderImpl implements ConfigBuilder {
	static final String HTTPS = "https";
	private final ConfigImpl config = new ConfigImpl();
	private final Downloader downloader;

	/**
	 * @param pDownloader
	 */
	ConfigBuilderImpl(final Downloader pDownloader) {
		downloader = pDownloader;
	}

	/**
	 * @return
	 */
	Config getBaseConfig() {
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
	public ConfigBuilder setWorkDirectory(final Path pWorkDirectory)
			throws MojoExecutionException {
		config.setWorkDirectory(pWorkDirectory);
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

	// TODO: Add validations here
	@Override
	public Config build() throws MojoExecutionException {
		setDownloadedCliJar(downloader.downloadCliJar(config));
		return (Config) config.clone();
	}

	@Override
	public ConfigBuilder setStdin(final File pStdin) {
		if (pStdin != null) {
			config.setStdin(pStdin.toPath());
		}
		return this;
	}

	@Override
	public ConfigBuilder setProxy(final Proxy pProxy) {
		config.setProxy(pProxy);
		return this;
	}

	@Override
	public ConfigBuilder setSettings(final Settings pSettings) {
		config.setSettings(pSettings);
		return this;
	}

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
}
