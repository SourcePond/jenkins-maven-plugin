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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.config.download.HttpClientFacadeImpl;
import ch.sourcepond.maven.plugin.jenkins.config.download.SSLFactory;

/**
 * @author rolandhauser
 *
 */
public class HttpClientFacadeImplTest {
	private static final File TRUST_STORE = new File("file:///trustStore");
	private static final String TRUST_STORE_PASSWORD = "password";
	private final SSLFactory sslFactory = mock(SSLFactory.class);
	private final HostnameVerifier trustAllVerifier = mock(HostnameVerifier.class);
	private final SSLContext context = mock(SSLContext.class);
	private final SSLConnectionSocketFactory socketFactory = mock(SSLConnectionSocketFactory.class);
	private final CloseableHttpClient client = mock(CloseableHttpClient.class);
	private final Config config = mock(Config.class);
	private final HttpClientFacadeImpl impl = new HttpClientFacadeImpl(
			sslFactory, trustAllVerifier);
	private URI httpUri;
	private URI httpsUri;

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		httpUri = new URI("http://someUri");
		httpsUri = new URI("https://someUri");
		when(config.isSecure()).thenReturn(true);
	}

	/**
	 * 
	 */
	@Test
	public void verifyNewGet() {
		final HttpUriRequest request = impl.newGet(httpUri);
		assertSame(httpUri, request.getURI());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyNoCertificateCheckClient() throws Exception {
		when(config.getBaseUri()).thenReturn(httpsUri);
		when(config.isNoCertificateCheck()).thenReturn(true);
		when(sslFactory.newTrustAllContext()).thenReturn(context);
		when(sslFactory.newFactory(context, trustAllVerifier)).thenReturn(
				socketFactory);
		when(sslFactory.newClient(socketFactory)).thenReturn(client);
		assertSame(client, impl.newClient(config));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifySelfSignedCertifacteClient() throws Exception {
		when(config.getBaseUri()).thenReturn(httpsUri);
		when(config.getTrustStoreOrNull()).thenReturn(TRUST_STORE);
		when(config.getTrustStorePasswordOrNull()).thenReturn(
				TRUST_STORE_PASSWORD);
		when(sslFactory.newContext(TRUST_STORE, TRUST_STORE_PASSWORD))
				.thenReturn(context);
		final HostnameVerifier verifier = mock(HostnameVerifier.class);
		when(sslFactory.newDefaultHostnameVerifier()).thenReturn(verifier);
		when(sslFactory.newFactory(context, verifier))
				.thenReturn(socketFactory);
		when(sslFactory.newClient(socketFactory)).thenReturn(client);
		assertSame(client, impl.newClient(config));
	}

	@Test
	public void verifyUnsecureClient() throws Exception {
		when(config.isSecure()).thenReturn(false);
		when(config.getBaseUri()).thenReturn(httpUri);
		when(sslFactory.newClient(null)).thenReturn(client);
		assertSame(client, impl.newClient(config));
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void verifyTrustStoreNotSpecified() throws Exception {
		when(config.getTrustStorePasswordOrNull()).thenReturn(
				TRUST_STORE_PASSWORD);
		when(config.getBaseUri()).thenReturn(httpsUri);
		impl.newClient(config);
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void verifyTrustStorePasswordNotSpecified() throws Exception {
		when(config.getTrustStoreOrNull()).thenReturn(TRUST_STORE);
		when(config.getBaseUri()).thenReturn(httpsUri);
		impl.newClient(config);
	}
}
