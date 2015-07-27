package ch.sourcepond.maven.plugin.jenkins.message;

/**
 * @author rolandhauser
 *
 */
public interface Messages {

	/**
	 * @param pKey
	 * @param pReplacments
	 * @return
	 */
	String getMessage(String pKey, Object... pReplacments);
}
