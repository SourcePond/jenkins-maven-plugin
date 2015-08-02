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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * Simple facade to separate {@link HttpUriRequest} and
 * {@link CloseableHttpClient} creation to make the rest of the code better
 * testable.
 *
 */
interface HttpClientFacade {

	/**
	 * Creates a new {@link HttpGet} object.
	 * 
	 * @param pUri
	 *            {@link URI}, must not be {@code null}.
	 * @return New get-request, never {@code null}
	 */
	HttpUriRequest newGet(URI pUri);

	/**
	 * Creates a new {@link CloseableHttpClient} instance. Dependening on the
	 * {@link Config} specified this can be an ordinary HTTP client or a secure
	 * HTTPS client.
	 * 
	 * @param pValidatedConfig
	 *            {@link Config} instance, must not be {@code null}
	 * @return New client instance, never {@code null}
	 * @throws MojoExecutionException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 */
	CloseableHttpClient newClient(Config pValidatedConfig)
			throws MojoExecutionException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException;
}
