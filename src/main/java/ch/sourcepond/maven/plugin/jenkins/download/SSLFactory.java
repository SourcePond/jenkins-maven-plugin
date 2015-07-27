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

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author rolandhauser
 *
 */
interface SSLFactory {

	/**
	 * @return
	 */
	/**
	 * @param pSslCsfOrNull
	 * @return
	 */
	CloseableHttpClient newClient(SSLConnectionSocketFactory pSslCsfOrNull);

	/**
	 * @return
	 */
	HostnameVerifier newDefaultHostnameVerifier();

	/**
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	SSLContext newTrustAllContext() throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException;

	/**
	 * @param pTrustStore
	 * @param pPassword
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 */
	SSLContext newContext(File pTrustStore, String pPassword)
			throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException;

	/**
	 * @param pContext
	 * @param pVerifier
	 * @return
	 */
	SSLConnectionSocketFactory newFactory(SSLContext pContext,
			HostnameVerifier pVerifier);
}
