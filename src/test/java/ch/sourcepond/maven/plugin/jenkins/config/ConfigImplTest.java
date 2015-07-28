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
package ch.sourcepond.maven.plugin.jenkins.config;

import static ch.sourcepond.maven.plugin.jenkins.config.ConfigImpl.CONFIG_VALIDATION_NO_KEY_AUTH_AND_PRIVATE_KEY_SET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 *
 */
public class ConfigImplTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private static final String ANY_PRIVATE_KEY = "anyPrivateKey";
	private final Messages messages = mock(Messages.class);
	private final ConfigImpl impl = new ConfigImpl(messages);

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyNoKeyAuthPrivateKey() throws MojoExecutionException {
		when(
				messages.getMessage(
						CONFIG_VALIDATION_NO_KEY_AUTH_AND_PRIVATE_KEY_SET,
						ANY_PRIVATE_KEY)).thenReturn(ANY_MESSAGE);
		impl.setNoKeyAuth(true);
		impl.setPrivateKey(ANY_PRIVATE_KEY);
		try {
			impl.validate();
			fail("Exception expected");
		} catch (final MojoExecutionException expected) {
			assertEquals(ANY_MESSAGE, expected.getMessage());
		}
	}
}
