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

import java.io.IOException;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import ch.sourcepond.maven.plugin.jenkins.CliMojo;

/**
 * @author rolandhauser
 *
 */
public class ProxySimulator extends Simulator {
	private static final String PROXY_ID = "SomeTestProxy";
	private Simulator delegate;
	private HttpProxyServer server;

	/**
	 * @param pDelegate
	 * @throws IOException
	 */
	public ProxySimulator(final Simulator pDelegate) throws IOException {
		super();
		delegate = pDelegate;
	}

	@Override
	public void close() throws IOException {
		try {
			delegate.close();
		} finally {
			server.stop();
		}
	}

	@Override
	public void setup(final CliMojo mojo) throws Exception {
		server = DefaultHttpProxyServer.bootstrap().withPort(getPort())
				.withAllowLocalOnly(true).start();
		delegate.setup(mojo);

		final Proxy proxy = new Proxy();
		proxy.setId(PROXY_ID);
		proxy.setActive(true);
		proxy.setHost("localhost");
		proxy.setPort(getPort());

		final Settings settings = mojo.getSettings();
		settings.addProxy(proxy);
		mojo.setSettings(settings);
		mojo.setProxyId(PROXY_ID);
	}
}
