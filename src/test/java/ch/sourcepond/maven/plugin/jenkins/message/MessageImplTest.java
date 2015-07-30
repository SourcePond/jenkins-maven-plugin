package ch.sourcepond.maven.plugin.jenkins.message;

import static java.util.Locale.getDefault;
import static java.util.Locale.setDefault;
import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class MessageImplTest {
	private static final String ANY_KEY = "anyKey";
	private final MessageImpl impl = new MessageImpl();
	private Locale currentLocale;

	/**
	 * 
	 */
	@Before
	public void setup() {
		currentLocale = getDefault();
		setDefault(Locale.ENGLISH);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		Locale.setDefault(currentLocale);
	}

	/**
	 * 
	 */
	@Test
	public void verifyTranslationsKeyNotFound() {
		assertEquals(ANY_KEY, impl.getMessage(ANY_KEY));
	}

	/**
	 * 
	 */
	@Test
	public void verifyTranslations() {
		assertEquals(
				"\"noKeyAuth\" cannot be true when \"privateKey\" has value test. Correct POM and try again.",
				impl.getMessage(
						"config.validation.error.noKeyAuthAndPrivateKeySet",
						"test"));
	}
}
