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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author rolandhauser
 *
 */
interface HttpClientFacade {

	/**
	 * @param pUri
	 * @return
	 */
	HttpUriRequest newGet(URI pUri);

	/**
	 * @param pSecure
	 * @param pNoCertificateCheck
	 * @param pTrustStoreOrNull
	 * @param pTrustStorePasswordOrNull
	 * @return
	 * @throws MojoExecutionException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 */
	CloseableHttpClient newClient(boolean pSecure, boolean pNoCertificateCheck,
			File pTrustStoreOrNull, String pTrustStorePasswordOrNull)
			throws MojoExecutionException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException;
}
