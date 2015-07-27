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
 * @author rolandhauser
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
