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

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * Default implementation ot the {@link HttpClientFacade} interface.
 *
 */
@Named
@Singleton
final class HttpClientFacadeImpl implements HttpClientFacade {
	private final SSLFactory sslFactory;
	private final HostnameVerifier trustAllVerifier;

	/**
	 * @param pSslFactory
	 */
	@Inject
	HttpClientFacadeImpl(final SSLFactory pSslFactory,
			final HostnameVerifier pTrustAllVerifier) {
		sslFactory = pSslFactory;
		trustAllVerifier = pTrustAllVerifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.HttpClientFacade#newGet(java
	 * .net.URI)
	 */
	@Override
	public HttpUriRequest newGet(final URI pUri) {
		return new HttpGet(pUri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.HttpClientFacade#newClient
	 * (java.net.URI)
	 */
	@Override
	public CloseableHttpClient newClient(final Config pValidatedConfig)
			throws MojoExecutionException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException {
		final CloseableHttpClient client;
		if (pValidatedConfig.isSecure()) {
			final SSLConnectionSocketFactory sslsf;

			if (pValidatedConfig.isNoCertificateCheck()) {
				// Trust own CA and all self-signed certs
				final SSLContext context = sslFactory.newTrustAllContext();
				sslsf = sslFactory.newFactory(context, trustAllVerifier);
			} else {
				// Trust own CA and all self-signed certs
				final SSLContext sslcontext = sslFactory.newContext(
						pValidatedConfig.getTrustStoreOrNull(),
						pValidatedConfig.getTrustStorePasswordOrNull());

				sslsf = sslFactory.newFactory(sslcontext,
						sslFactory.newDefaultHostnameVerifier());
			}

			client = sslFactory.newClient(sslsf);
		} else {
			client = sslFactory.newClient(null);
		}
		return client;
	}

}
