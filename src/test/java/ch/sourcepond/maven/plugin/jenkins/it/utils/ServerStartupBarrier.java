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

import static java.lang.System.currentTimeMillis;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 *
 */
abstract class ServerStartupBarrier extends Thread {
	private static final long DEFAULT_TIMEOUT = 60000;
	private final URI baseUri;
	private volatile Exception exception;

	/**
	 * @param pBaseUri
	 */
	public ServerStartupBarrier(final URI pBaseUri) {
		baseUri = pBaseUri;
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws CertificateException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws URISyntaxException
	 */
	protected abstract CloseableHttpClient createClient()
			throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException,
			URISyntaxException;

	@Override
	public void run() {
		try (final CloseableHttpClient client = createClient()) {
			final HttpGet request = new HttpGet(baseUri);
			final long timeout = currentTimeMillis() + DEFAULT_TIMEOUT;
			int status = -1;
			while (status != SC_OK && timeout > currentTimeMillis()) {
				try {
					final HttpResponse response = client.execute(request);
					status = response.getStatusLine().getStatusCode();
					request.reset();

					if (status == SC_OK) {
						exception = null;
						break;
					}

					sleep(500);

				} catch (final Exception e) {
					exception = e;
					try {
						sleep(500);
					} catch (final InterruptedException e1) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
		} catch (final IOException | KeyManagementException
				| NoSuchAlgorithmException | KeyStoreException
				| CertificateException | URISyntaxException e2) {
			exception = e2;
		}
	}

	/**
	 * @throws Exception
	 */
	public void waitForServerStart() throws Exception {
		start();
		join();

		if (exception != null) {
			throw exception;
		}
	}
}
