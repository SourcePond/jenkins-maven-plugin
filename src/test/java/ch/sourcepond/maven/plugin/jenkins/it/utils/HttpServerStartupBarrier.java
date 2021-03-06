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

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 */
final class HttpServerStartupBarrier extends ServerStartupBarrier {

	/**
	 * @param pBaseUri
	 */
	public HttpServerStartupBarrier(final URI pBaseUri) {
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
	protected CloseableHttpClient createClient() {
		return HttpClients.createDefault();
	}

}
