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

import static ch.sourcepond.maven.plugin.jenkins.config.ConfigImpl.CONFIG_VALIDATION_ERROR_NO_KEY_AUTH_AND_PRIVATE_KEY_SET;
import static ch.sourcepond.maven.plugin.jenkins.config.ConfigImpl.CONFIG_VALIDATION_ERROR_NO_TRUSTSTORE_PASSWORD_SPECIFIED;
import static ch.sourcepond.maven.plugin.jenkins.config.ConfigImpl.CONFIG_VALIDATION_ERROR_TRUSTSTORE_PASSWORD_TOO_SHORT;
import static ch.sourcepond.maven.plugin.jenkins.config.ConfigImpl.CONFIG_VALIDATION_WARN_TRUSTSTORE_PASSWORD_NOT_NECESSARY;
import static ch.sourcepond.maven.plugin.jenkins.config.ConfigImpl.MIN_TRUSTSTORE_PWD_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 *
 */
public class ConfigImplTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private static final String ANY_PRIVATE_KEY = "anyPrivateKey";
	private static final String ANY_PASSWORD = "anyPassword";
	private static final File ANY_TRUSTSTORE = new File("file:///anyFile");
	private final Log log = mock(Log.class);
	private final Messages messages = mock(Messages.class);
	private final ConfigImpl impl = new ConfigImpl(messages);

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyNoKeyAuthPrivateKey() throws MojoExecutionException {
		when(
				messages.getMessage(
						CONFIG_VALIDATION_ERROR_NO_KEY_AUTH_AND_PRIVATE_KEY_SET,
						ANY_PRIVATE_KEY)).thenReturn(ANY_MESSAGE);
		impl.setNoKeyAuth(true);
		impl.setPrivateKey(ANY_PRIVATE_KEY);
		try {
			impl.validate(log);
			fail("Exception expected");
		} catch (final MojoExecutionException expected) {
			assertEquals(ANY_MESSAGE, expected.getMessage());
		}

		// Should not cause an exception
		impl.setNoKeyAuth(false);
		impl.validate(log);

		// Should not cause an exception
		impl.setNoKeyAuth(true);
		impl.setPrivateKey(null);
		impl.validate(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyNoTruststorePassword() throws MojoExecutionException {
		when(
				messages.getMessage(
						CONFIG_VALIDATION_ERROR_NO_TRUSTSTORE_PASSWORD_SPECIFIED,
						ANY_TRUSTSTORE)).thenReturn(ANY_MESSAGE);
		impl.setTrustStore(ANY_TRUSTSTORE);
		try {
			impl.validate(log);
			fail("Exception expected");
		} catch (final MojoExecutionException expected) {
			assertEquals(ANY_MESSAGE, expected.getMessage());
		}

		// Should not cause an exception
		impl.setTrustStorePassword(ANY_PASSWORD);
		impl.validate(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyTruststorePasswordTooShort()
			throws MojoExecutionException {
		when(
				messages.getMessage(
						CONFIG_VALIDATION_ERROR_TRUSTSTORE_PASSWORD_TOO_SHORT,
						MIN_TRUSTSTORE_PWD_LENGTH)).thenReturn(ANY_MESSAGE);
		impl.setTrustStore(ANY_TRUSTSTORE);
		impl.setTrustStorePassword("12345");
		try {
			impl.validate(log);
			fail("Exception expected");
		} catch (final MojoExecutionException expected) {
			assertEquals(ANY_MESSAGE, expected.getMessage());
		}

		// Should not cause an exception
		impl.setTrustStorePassword("123456");
		impl.validate(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyTruststorePasswordNotNecessary()
			throws MojoExecutionException {
		when(
				messages.getMessage(CONFIG_VALIDATION_WARN_TRUSTSTORE_PASSWORD_NOT_NECESSARY))
				.thenReturn(ANY_MESSAGE);
		impl.setTrustStorePassword(ANY_PASSWORD);
		impl.validate(log);
		verify(log).warn(ANY_MESSAGE);

		impl.setTrustStorePassword(null);
		impl.validate(log);
		verifyNoMoreInteractions(log);
	}
}
