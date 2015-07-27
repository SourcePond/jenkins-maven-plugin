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
package ch.sourcepond.maven.plugin.jenkins.download;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.getDefaultHostnameVerifier;
import static org.apache.http.impl.client.HttpClients.createDefault;
import static org.apache.http.impl.client.HttpClients.custom;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
final class SSLFactoryImpl implements SSLFactory {
	private final TrustStrategy trustAllStrategy;

	/**
	 * @param pTrustAllStrategy
	 */
	@Inject
	SSLFactoryImpl(final TrustStrategy pTrustAllStrategy) {
		trustAllStrategy = pTrustAllStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.SSLFactory#newFactory(javax
	 * .net.ssl.SSLContext, javax.net.ssl.HostnameVerifier)
	 */
	@Override
	public SSLConnectionSocketFactory newFactory(final SSLContext pContext,
			final HostnameVerifier pVerifier) {
		return new SSLConnectionSocketFactory(pContext, pVerifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.SSLFactory#newTrustAllContext
	 * ()
	 */
	@Override
	public SSLContext newTrustAllContext() throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException {
		return SSLContexts.custom().loadTrustMaterial(null, trustAllStrategy)
				.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.SSLFactory#newContext(java
	 * .io.File, java.lang.String)
	 */
	@Override
	public SSLContext newContext(final File pTrustStore, final String pPassword)
			throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException {
		return SSLContexts
				.custom()
				.loadTrustMaterial(pTrustStore, pPassword.toCharArray(),
						new TrustSelfSignedStrategy()).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.download.SSLFactory#newClient(org.
	 * apache.http.conn.ssl.SSLConnectionSocketFactory)
	 */
	@Override
	public CloseableHttpClient newClient(
			final SSLConnectionSocketFactory pSslCsfOrNull) {
		final CloseableHttpClient client;
		if (pSslCsfOrNull != null) {
			client = custom().setSSLSocketFactory(pSslCsfOrNull).build();
		} else {
			client = createDefault();
		}
		return client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.download.SSLFactory#
	 * newDefaultHostnameVerifier()
	 */
	@Override
	public HostnameVerifier newDefaultHostnameVerifier() {
		return getDefaultHostnameVerifier();
	}
}
