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
package ch.sourcepond.maven.plugin.jenkins.proxy;

import static ch.sourcepond.maven.plugin.jenkins.proxy.ProxyFinderImpl.CONFIG_VALIDATION_ERROR_NO_PROXY_FOUND;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
public class ProxyFinderImplTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private static final String MATCHING_ID = "matchingId";
	private static final String IGNORED_ID = "ignoredId";
	private final Messages messages = mock(Messages.class);
	private final ProxyFinderImpl impl = new ProxyFinderImpl(messages);
	private final Settings settings = mock(Settings.class);
	private final Proxy matchingProxy = mock(Proxy.class);
	private final Proxy ignoredProxy = mock(Proxy.class);

	/**
	 * 
	 */
	@Before
	public void setup() {
		when(matchingProxy.getId()).thenReturn(MATCHING_ID);
		when(ignoredProxy.getId()).thenReturn(IGNORED_ID);
		when(settings.getProxies()).thenReturn(
				asList(ignoredProxy, matchingProxy));
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void verifyNoSettings() throws MojoExecutionException {
		impl.findProxy(MATCHING_ID, null);
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifyFindProxy() throws MojoExecutionException {
		assertSame(matchingProxy, impl.findProxy(MATCHING_ID, settings));
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifyNoProxyIdSpecified() throws MojoExecutionException {
		assertNull(impl.findProxy(null, settings));
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifyFindProxyIgnoredProxyHasNoId()
			throws MojoExecutionException {
		when(ignoredProxy.getId()).thenReturn(null);
		assertSame(matchingProxy, impl.findProxy(MATCHING_ID, settings));
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifyNoProxyFound() {
		when(
				messages.getMessage(CONFIG_VALIDATION_ERROR_NO_PROXY_FOUND,
						MATCHING_ID)).thenReturn(ANY_MESSAGE);
		when(settings.getProxies()).thenReturn(asList(ignoredProxy));
		try {
			impl.findProxy(MATCHING_ID, settings);
			fail("Exception expected");
		} catch (final MojoExecutionException expected) {
			assertEquals(ANY_MESSAGE, expected.getMessage());
		}
	}
}
