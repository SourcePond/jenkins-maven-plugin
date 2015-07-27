package ch.sourcepond.maven.plugin.jenkins.message;

import static java.text.MessageFormat.format;
import static java.util.ResourceBundle.getBundle;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
final class MessageImpl implements Messages {
	static final String BASE_NAME = "resources";
	private final ResourceBundle bundle;

	@Inject
	MessageImpl() {
		bundle = getBundle(BASE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.message.Messages#getMessage(java.lang
	 * .String, java.lang.Object[])
	 */
	@Override
	public String getMessage(final String pKey, final Object... pArguments) {
		try {
			final String value = bundle.getString(pKey);
			return format(value, pArguments);
		} catch (final MissingResourceException e) {
			return "[No resource found for key '" + pKey + "']";
		}
	}

}
