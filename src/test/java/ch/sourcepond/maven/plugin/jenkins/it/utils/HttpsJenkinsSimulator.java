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
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * @author rolandhauser
 *
 */
public class HttpsJenkinsSimulator extends JenkinsSimulator {
	static final String KEYSTORE_NAME = "/keystore.jks";
	public static final String TEST_PASSWORD = "test123";

	/**
	 * @throws IOException
	 */
	public HttpsJenkinsSimulator() throws IOException {
		super();
	}

	@Override
	protected String getProcotol() {
		return "https";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.it.utils.JenkinsSimulator#
	 * specializeProcessCommand(java.util.List)
	 */
	@Override
	protected void specializeProcessCommand(final List<String> pProcessCommand) {
		final URL url = getClass().getResource(KEYSTORE_NAME);
		pProcessCommand.add("--httpPort=-1");
		pProcessCommand.add("--httpsPort=" + getPort());
		pProcessCommand.add("--httpsKeyStore=" + url.getPath());
		pProcessCommand.add("--httpsKeyStorePassword=" + TEST_PASSWORD);
	}

	@Override
	protected ServerStartupBarrier createBarrier(final URI pUri) {
		return new HttpsServerStartupBarrier(pUri);
	}
}
