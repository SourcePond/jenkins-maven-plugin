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
package ch.sourcepond.maven.plugin.jenkins.process.cmd;

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.BaseUri.BASE_URI_SWITCH;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.Jar.JAR_SWITCH;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.Java.JAVA_EXECUTABLE;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.NoCertificateCheck.NO_CERTIFICATE_CHECK_SWITCH;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.NoKeyAuth.NO_KEY_AUTH_SWITCH;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.PrivateKey.PRIVATE_KEY_SWITCH;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.ProxyParam.PROXY_SWITCH;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.TrustStore.TRUST_STORE_PARAMETER;
import static ch.sourcepond.maven.plugin.jenkins.process.cmd.TrustStorePassword.TRUST_STORE_PASSWORD;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.InjectorInstance;
import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * @author rolandhauser
 *
 */
public class CommandFactoryImplTest {
	private static final String PROXY_HOST = "anyhostname";
	private static final int PROXY_PORT = 8080;
	private static final File TRUST_STORE = new File("file:///truststore.jks");
	private static final File PRIVATE_KEY = new File("file:///id_rsa");
	private static final String PASSWORD = "password";
	private static final String BASE_URI = "http://jenkins.ch";
	private static final String JAR = "/cli.jar";
	private static final String JENKINS_HELP = "help";
	private static final String JENKINS_CREATE_JOB = "create-job";
	private static final String COMMAND = JENKINS_HELP + " "
			+ JENKINS_CREATE_JOB;
	private final Config config = mock(Config.class);
	private final Proxy proxy = mock(Proxy.class);
	private Java impl;
	private URI baseUri;

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		baseUri = new URI(BASE_URI);
		when(config.getBaseUri()).thenReturn(baseUri);
		when(config.getDownloadedCliJar()).thenReturn(JAR);
		when(config.getCommand()).thenReturn(COMMAND);
		when(config.getTrustStoreOrNull()).thenReturn(TRUST_STORE);
		when(config.getTrustStorePasswordOrNull()).thenReturn(PASSWORD);
		when(proxy.getHost()).thenReturn(PROXY_HOST);
		when(proxy.getPort()).thenReturn(PROXY_PORT);
		impl = InjectorInstance.INJECTOR.getInstance(Java.class);
	}

	/**
	 * @param it
	 */
	private void verifyBaseCommand(final Iterator<String> it,
			final boolean secure) {
		assertEquals(JAVA_EXECUTABLE, it.next());
		if (secure) {
			assertEquals(
					TRUST_STORE_PARAMETER + "=" + TRUST_STORE.getAbsolutePath(),
					it.next());
			assertEquals(TRUST_STORE_PASSWORD + "=" + PASSWORD, it.next());
		}
		assertEquals(JAR_SWITCH, it.next());
		assertEquals(JAR, it.next());
		assertEquals(BASE_URI_SWITCH, it.next());
		assertEquals(BASE_URI, it.next());
	}

	/**
	 * @param it
	 */
	private void verifySecureBaseCommand(final Iterator<String> it) {
		verifyBaseCommand(it, true);
	}

	/**
	 * @param it
	 */
	private void verifyUnsecureBaseCommand(final Iterator<String> it) {
		verifyBaseCommand(it, false);
	}

	/**
	 * @param it
	 */
	private void verifyJenkinsCommand(final Iterator<String> it) {
		assertEquals(JENKINS_HELP, it.next());
		assertEquals(JENKINS_CREATE_JOB, it.next());
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifySimpleHttpCommand() throws MojoExecutionException {
		final List<String> command = impl.newCommand(config);
		assertEquals(7, command.size());
		final Iterator<String> it = command.iterator();
		verifyUnsecureBaseCommand(it);
		verifyJenkinsCommand(it);
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifySimpleHttpCommandWithNoKeyAuth()
			throws MojoExecutionException {
		when(config.isNoKeyAuth()).thenReturn(true);
		final List<String> command = impl.newCommand(config);
		assertEquals(8, command.size());
		final Iterator<String> it = command.iterator();
		verifyUnsecureBaseCommand(it);
		assertEquals(NO_KEY_AUTH_SWITCH, it.next());
		verifyJenkinsCommand(it);
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifySimpleHttpWithPrivateKeyCommand()
			throws MojoExecutionException {
		when(config.getPrivateKeyOrNull()).thenReturn(
				PRIVATE_KEY.getAbsolutePath());
		final List<String> command = impl.newCommand(config);
		assertEquals(9, command.size());
		final Iterator<String> it = command.iterator();
		verifyUnsecureBaseCommand(it);
		assertEquals(PRIVATE_KEY_SWITCH, it.next());
		assertEquals(PRIVATE_KEY.getAbsolutePath(), it.next());
		verifyJenkinsCommand(it);
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifyHttpWithProxyCommand() throws MojoExecutionException {
		when(config.getProxyOrNull()).thenReturn(proxy);
		final List<String> command = impl.newCommand(config);
		assertEquals(9, command.size());
		final Iterator<String> it = command.iterator();
		verifyUnsecureBaseCommand(it);
		assertEquals(PROXY_SWITCH, it.next());
		assertEquals(PROXY_HOST + ":" + PROXY_PORT, it.next());
		verifyJenkinsCommand(it);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyHttpsCommand() throws MojoExecutionException {
		when(config.isSecure()).thenReturn(true);
		final List<String> command = impl.newCommand(config);
		assertEquals(9, command.size());
		final Iterator<String> it = command.iterator();
		verifySecureBaseCommand(it);
		verifyJenkinsCommand(it);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyHttpsWithNoCertifacteCheckCommand()
			throws MojoExecutionException {
		when(config.isSecure()).thenReturn(true);
		when(config.isNoCertificateCheck()).thenReturn(true);
		final List<String> command = impl.newCommand(config);
		assertEquals(10, command.size());
		final Iterator<String> it = command.iterator();
		verifySecureBaseCommand(it);
		assertEquals(NO_CERTIFICATE_CHECK_SWITCH, it.next());
		verifyJenkinsCommand(it);
	}
}
