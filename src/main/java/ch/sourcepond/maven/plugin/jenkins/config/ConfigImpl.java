package ch.sourcepond.maven.plugin.jenkins.config;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * @author rolandhauser
 *
 */
final class ConfigImpl implements Config, Cloneable {
	static final String CONFIG_VALIDATION_NO_KEY_AUTH_AND_PRIVATE_KEY_SET = "config.validation.noKeyAuthAndPrivateKeySet";
	static final String HTTPS = "https";
	private final Messages messages;
	private Path workDirectory;
	private URI baseUri;
	private URI cliJarUri;
	private boolean noKeyAuth;
	private String privateKeyOrNull;
	private String command;
	private Proxy proxy;
	private String dowloadedCliJar;
	private Path stdin;
	private Settings settings;
	private boolean noCertificateCheck;
	private File trustStore;
	private String trustStorePassword;

	/**
	 * @param pMessages
	 */
	ConfigImpl(final Messages pMessages) {
		messages = pMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		final ConfigImpl clone = new ConfigImpl(messages);
		clone.setBaseUri(getBaseUri());
		clone.setCliJarUri(getCliJarUri());
		clone.setCommand(getCommand());
		clone.setDownloadedCliJar(getDownloadedCliJar());
		clone.setNoCertificateCheck(isNoCertificateCheck());
		clone.setNoKeyAuth(isNoKeyAuth());
		clone.setPrivateKey(getPrivateKeyOrNull());
		clone.setProxy(getProxyOrNull());
		clone.setSettings(getSettings());
		clone.setStdin(getStdinOrNull());
		clone.setTrustStore(getTrustStoreOrNull());
		clone.setTrustStorePassword(getTrustStorePasswordOrNull());
		return clone;
	}

	/**
	 * @return
	 */
	@Override
	public Path getWorkDirectory() {
		return workDirectory;
	}

	/**
	 * @return
	 */
	@Override
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * @return
	 */
	@Override
	public boolean isNoKeyAuth() {
		return noKeyAuth;
	}

	/**
	 * @return
	 */
	@Override
	public String getPrivateKeyOrNull() {
		return privateKeyOrNull;
	}

	/**
	 * @return
	 */
	@Override
	public String getCommand() {
		return command;
	}

	/**
	 * @return
	 */
	@Override
	public URI getCliJarUri() {
		return cliJarUri;
	}

	@Override
	public String getDownloadedCliJar() {
		return dowloadedCliJar;
	}

	/**
	 * @param pDownloadedCliJarPath
	 */
	void setDownloadedCliJar(final String pDownloadedCliJarPath) {
		dowloadedCliJar = pDownloadedCliJarPath;
	}

	/**
	 * @param pBaseUri
	 */
	void setBaseUri(final URI pBaseUri) {
		baseUri = pBaseUri;
	}

	void setCliJarUri(final URI pCliJarUri) {
		cliJarUri = pCliJarUri;
	}

	/**
	 * @param pNoKeyAuth
	 */
	void setNoKeyAuth(final boolean pNoKeyAuth) {
		noKeyAuth = pNoKeyAuth;
	}

	/**
	 * @param pNoCertificateCheck
	 */
	void setNoCertificateCheck(final boolean pNoCertificateCheck) {
		noCertificateCheck = pNoCertificateCheck;
	}

	/**
	 * @param pPrivateOrNull
	 */
	void setPrivateKey(final String pPrivateOrNull) {
		privateKeyOrNull = pPrivateOrNull;
	}

	/**
	 * @param pCommand
	 */
	void setCommand(final String pCommand) {
		command = pCommand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#isNoCertificateCheck()
	 */
	@Override
	public boolean isNoCertificateCheck() {
		return noCertificateCheck;
	}

	@Override
	public Path getStdinOrNull() {
		return stdin;
	}

	/**
	 * @param pStdin
	 */
	void setStdin(final Path pStdin) {
		stdin = pStdin;
	}

	@Override
	public Proxy getProxyOrNull() {
		return proxy;
	}

	/**
	 * @param pProxy
	 */
	void setProxy(final Proxy pProxy) {
		proxy = pProxy;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param pSettings
	 */
	void setSettings(final Settings pSettings) {
		settings = pSettings;
	}

	@Override
	public File getTrustStoreOrNull() {
		return trustStore;
	}

	@Override
	public String getTrustStorePasswordOrNull() {
		return trustStorePassword;
	}

	/**
	 * @param pTrustStore
	 */
	void setTrustStore(final File pTrustStore) {
		trustStore = pTrustStore;
	}

	/**
	 * @param pPassword
	 */
	void setTrustStorePassword(final String pPassword) {
		trustStorePassword = pPassword;
	}

	/**
	 * @param pWorkDirectory
	 */
	void setWorkDirectory(final Path pWorkDirectory) {
		workDirectory = pWorkDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#isSecure()
	 */
	@Override
	public boolean isSecure() {
		return HTTPS.equals(getBaseUri().getScheme());
	}

	/**
	 * @throws MojoExecutionException
	 */
	public void validate() throws MojoExecutionException {
		if (isNoKeyAuth() && getPrivateKeyOrNull() != null) {
			throw new MojoExecutionException(messages.getMessage(
					CONFIG_VALIDATION_NO_KEY_AUTH_AND_PRIVATE_KEY_SET,
					getPrivateKeyOrNull()));
		}
	}
}
