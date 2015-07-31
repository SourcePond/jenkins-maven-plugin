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
package ch.sourcepond.maven.plugin.jenkins;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder;
import ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilderFactory;
import ch.sourcepond.maven.plugin.jenkins.process.ProcessFacade;
import ch.sourcepond.maven.plugin.jenkins.proxy.ProxyFinder;

/**
 *
 */
public class CliMojoTest {
	private static final String CLI_JAR = "/cli.jar";
	private static final String COMMAND = "anyCommand";
	private static final String PROXY_ID = "anyProxyId";
	private static final String PASSWORD = "anyPassword";
	private final Log log = mock(Log.class);
	private final ConfigBuilderFactory cbf = mock(ConfigBuilderFactory.class);
	private final ConfigBuilder builder = mock(ConfigBuilder.class);
	private final ProcessFacade procFacade = mock(ProcessFacade.class);
	private final ProxyFinder finder = mock(ProxyFinder.class);
	private final Proxy proxy = mock(Proxy.class);
	private final Settings settings = mock(Settings.class);
	private final File workDirectory = new File("workDirectory");
	private final File privateKey = new File("privateKey");
	private final File stdin = new File("stdin");
	private final File stdout = new File("stdout");
	private final File trustStore = new File("trustStore");
	private final File customJenkinsCliJar = new File("customJenkinsCliJar");
	private final Config config = mock(Config.class);
	private final CliMojo impl = new CliMojo(cbf, procFacade, finder);
	private URL baseUrl;

	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		impl.setLog(log);
		baseUrl = new URL("http://baseurl.org");
		impl.setWorkDirectory(workDirectory);
		impl.setBaseUrl(baseUrl);
		impl.setCliJar(CLI_JAR);
		impl.setNoKeyAuth(true);
		impl.setNoCertificateCheck(true);
		impl.setPrivateKey(privateKey);
		impl.setCommand(COMMAND);
		impl.setStdin(stdin);
		impl.setStdout(stdout);
		impl.setAppend(true);
		impl.setSettings(settings);
		impl.setProxyId(PROXY_ID);
		impl.setTrustStore(trustStore);
		impl.setTrustStorePassword(PASSWORD);
		impl.setCustomJenkinsCliJar(customJenkinsCliJar);
		when(finder.findProxy(PROXY_ID, settings)).thenReturn(proxy);
		when(cbf.newBuilder()).thenReturn(builder);
		when(builder.setSettings(settings)).thenReturn(builder);
		when(builder.setProxy(proxy)).thenReturn(builder);
		when(builder.setWorkDirectory(workDirectory.toPath())).thenReturn(
				builder);
		when(builder.setBaseUrl(baseUrl, CLI_JAR)).thenReturn(builder);
		when(builder.setCommand(COMMAND)).thenReturn(builder);
		when(builder.setStdin(stdin)).thenReturn(builder);
		when(builder.setStdout(stdout)).thenReturn(builder);
		when(builder.setAppend(true)).thenReturn(builder);
		when(builder.setNoKeyAuth(true)).thenReturn(builder);
		when(builder.setNoCertificateCheck(true)).thenReturn(builder);
		when(builder.setPrivateKey(privateKey)).thenReturn(builder);
		when(builder.setTrustStore(trustStore)).thenReturn(builder);
		when(builder.setTrustStorePassword(PASSWORD)).thenReturn(builder);
		when(builder.setCustomJenkinsCliJar(customJenkinsCliJar)).thenReturn(
				builder);
		when(builder.build(log)).thenReturn(config);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyExecute() throws Exception {
		impl.execute();
		verify(procFacade).execute(log, config);
	}

	/**
	 * 
	 */
	// TODO: Getter is only used by integration-test. Find a better solution and
	// remove this method
	@Test
	public void verifyGetSettings() {
		assertSame(settings, impl.getSettings());
	}
}
