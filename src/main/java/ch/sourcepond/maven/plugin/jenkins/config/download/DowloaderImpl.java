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
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
final class DowloaderImpl implements Downloader {
	static final String JAR_NAME = "jenkins-cli.jar";
	private final HttpClientFacade clientFacade;

	/**
	 * @param pClient
	 * @param pFs
	 */
	@Inject
	DowloaderImpl(final HttpClientFacade pClientFacade) {
		clientFacade = pClientFacade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.Downloader#downloadCliJar
	 * (ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public void downloadCliJar(final Config pConfig)
			throws MojoExecutionException {
		final HttpUriRequest request = clientFacade.newGet(pConfig
				.getCliJarUri());

		try (final CloseableHttpClient client = clientFacade.newClient(pConfig)) {
			try (final CloseableHttpResponse response = client.execute(request)) {
				final StatusLine statusLine = response.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
					throw new MojoExecutionException(statusLine + ": "
							+ pConfig.getCliJarUri());
				}

				final HttpEntity entity = response.getEntity();

				if (entity != null) {
					final Path jar = pConfig.getWorkDirectory().resolve(
							JAR_NAME);

					try (final InputStream in = entity.getContent()) {
						copy(in, jar, REPLACE_EXISTING);
					}

					pConfig.setDownloadedCliJar(jar.toString());
				} else {
					throw new MojoExecutionException(pConfig.getCliJarUri()
							+ " not found");
				}
			}
		} catch (final IOException | KeyManagementException
				| NoSuchAlgorithmException | KeyStoreException
				| CertificateException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			request.abort();
		}
	}
}
