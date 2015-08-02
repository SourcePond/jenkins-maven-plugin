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

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * Default implementation of the {@link Downloader} interface.
 */
@Named
@Singleton
final class DownloaderImpl implements Downloader {
	static final String DOWNLOADER_ERROR_NO_VERSION_HEADER = "downloader.error.noVersionHeader";
	static final String DOWNLOADER_ERROR_WRONG_STATUS_CODE = "downloader.error.wrongStatusCode";
	static final String DOWNLOADER_ERROR_ENTITY_IS_NULL = "downloader.error.entityIsNull";
	static final String DOWNLOADER_INFO_VERSION_FOUND = "downloader.info.versionFound";
	static final String DOWNLOADER_INFO_USED_CLI_JAR = "downloader.info.usedCliJar";
	static final String VERSION_HEADER_NAME = "X-Jenkins";
	static final String JAR_NAME = "jenkins-cli.jar";
	private final Messages messages;
	private final HttpClientFacade clientFacade;

	/**
	 * @param pClient
	 * @param pFs
	 */
	@Inject
	DownloaderImpl(final Messages pMessages,
			final HttpClientFacade pClientFacade) {
		messages = pMessages;
		clientFacade = pClientFacade;
	}

	/**
	 * @param pLog
	 * @param pConfig
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws MojoExecutionException
	 */
	private String determineJenkinsVersion(final Log pLog,
			final CloseableHttpClient pClient, final Config pConfig)
			throws ClientProtocolException, IOException, MojoExecutionException {
		final HttpUriRequest versionRequest = clientFacade.newGet(pConfig
				.getBaseUri());
		try (final CloseableHttpResponse response = pClient
				.execute(versionRequest)) {
			if (response.containsHeader(VERSION_HEADER_NAME)) {
				final Header[] header = response
						.getHeaders(VERSION_HEADER_NAME);
				isTrue(header.length == 1,
						"Jenkins API changed; received multiple version headers. Please report this as bug (https://github.com/SourcePond/jenkins-maven-plugin/issues)");
				final String version = header[0].getValue();

				if (pLog.isInfoEnabled()) {
					pLog.info(messages.getMessage(
							DOWNLOADER_INFO_VERSION_FOUND, version));
				}

				return version;
			} else {
				throw new MojoExecutionException(messages.getMessage(
						DOWNLOADER_ERROR_NO_VERSION_HEADER,
						pConfig.getBaseUri(), VERSION_HEADER_NAME));
			}
		}
	}

	/**
	 * @param pJenkinscliDirectory
	 * @param pJenkinsVersion
	 * @return
	 * @throws IOException
	 */
	private Path getDownloadedCliJar(final Path pJenkinscliDirectory,
			final String pJenkinsVersion) throws IOException {
		final Path downloadDirectory = pJenkinscliDirectory
				.resolve(pJenkinsVersion);
		if (!isDirectory(downloadDirectory)) {
			createDirectories(downloadDirectory);
		}
		return downloadDirectory.resolve(JAR_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.Downloader#downloadCliJar
	 * (ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public String downloadCliJar(final Log pLog, final Config pValidatedConfig)
			throws MojoExecutionException {
		try (final CloseableHttpClient client = clientFacade
				.newClient(pValidatedConfig)) {
			final String jenkinsVersion = determineJenkinsVersion(pLog, client,
					pValidatedConfig);

			final Path downloadedCliJar = getDownloadedCliJar(
					pValidatedConfig.getJenkinscliDirectory(), jenkinsVersion);

			if (!isRegularFile(downloadedCliJar)) {
				final HttpUriRequest request = clientFacade
						.newGet(pValidatedConfig.getCliJarUri());
				try {

					try (final CloseableHttpResponse response = client
							.execute(request)) {
						final StatusLine statusLine = response.getStatusLine();

						if (statusLine.getStatusCode() != SC_OK) {
							throw new MojoExecutionException(
									messages.getMessage(
											DOWNLOADER_ERROR_WRONG_STATUS_CODE,
											statusLine,
											pValidatedConfig.getCliJarUri()));
						}

						final HttpEntity entity = response.getEntity();

						if (entity != null) {
							try (final InputStream in = entity.getContent()) {
								copy(in, downloadedCliJar);
							}
						} else {
							throw new MojoExecutionException(
									messages.getMessage(
											DOWNLOADER_ERROR_ENTITY_IS_NULL,
											pValidatedConfig.getCliJarUri()));
						}
					}
				} finally {
					request.abort();
				}
			}

			final String absoluteDownloadedCliPath = downloadedCliJar
					.toAbsolutePath().toString();

			if (pLog.isInfoEnabled()) {
				pLog.info(messages.getMessage(DOWNLOADER_INFO_USED_CLI_JAR,
						absoluteDownloadedCliPath));
			}

			return absoluteDownloadedCliPath;
		} catch (final IOException | KeyManagementException
				| NoSuchAlgorithmException | KeyStoreException
				| CertificateException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
