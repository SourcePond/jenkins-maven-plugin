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
package ch.sourcepond.maven.plugin.jenkins.it.utils;

import static ch.sourcepond.maven.plugin.jenkins.it.utils.HttpsJenkinsSimulator.KEYSTORE_NAME;
import static ch.sourcepond.maven.plugin.jenkins.it.utils.HttpsJenkinsSimulator.TEST_PASSWORD;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

/**
 *
 */
public class HttpsServerStartupBarrier extends ServerStartupBarrier {

	/**
	 * @param pBaseUri
	 */
	public HttpsServerStartupBarrier(final URI pBaseUri) {
		super(pBaseUri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.it.utils.ServerStartupBarrier#createClient
	 * ()
	 */
	@Override
	protected CloseableHttpClient createClient() throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException, URISyntaxException {
		final URL url = getClass().getResource(KEYSTORE_NAME);
		// Trust own CA and all self-signed certs
		final SSLContext sslcontext = SSLContexts
				.custom()
				.loadTrustMaterial(new File(url.toURI()),
						TEST_PASSWORD.toCharArray(),
						new TrustSelfSignedStrategy()).build();
		// Allow TLSv1 protocol only
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		final CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf).build();

		return httpclient;
	}
}
