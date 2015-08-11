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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * Default implementation of the {@link Config} interface.
 * 
 */
final class ConfigImpl implements Config, Cloneable {
	static final String CONFIG_VALIDATION_ERROR_NO_KEY_AUTH_AND_PRIVATE_KEY_SET = "config.validation.error.noKeyAuthAndPrivateKeySet";
	static final String CONFIG_VALIDATION_ERROR_SECURE_BUT_NO_TRUSTSTORE_SPECIFIED = "config.validation.error.secureButNoTruststoreSpecified";
	static final String CONFIG_VALIDATION_ERROR_NO_TRUSTSTORE_PASSWORD_SPECIFIED = "config.validation.error.noTruststorePasswordSpecified";
	static final String CONFIG_VALIDATION_ERROR_TRUSTSTORE_PASSWORD_TOO_SHORT = "config.validation.error.truststorePasswordTooShort";
	static final String CONFIG_VALIDATION_WARN_TRUSTSTORE_PASSWORD_NOT_NECESSARY = "config.validation.warn.truststorePasswordNotNecessary";
	static final String CONFIG_VALIDATION_WARN_XSLT_NOT_APPLIABLE = "config.validation.warn.xsltNotAppliable";
	static final String CONFIG_VALIDATION_WARN_PARAMS_NOT_APPLIABLE = "config.validation.warn.paramsNotApplicable";
	static final String HTTPS = "https";
	static final String STDIN_FIELD = "stdin";
	static final String STDIN_XSLT_FIELD = "stdinXslt";
	static final String STDIN_PARAMS_FIELD = "stdinParams";
	static final String STDOUT_FIELD = "stdout";
	static final String STDOUT_XSLT_FIELD = "stdoutXslt";
	static final String STDOUT_PARAMS_FIELD = "stdoutParams";
	static final int MIN_TRUSTSTORE_PWD_LENGTH = 6;
	private final Messages messages;
	private Path jenkinscliDirectory;
	private URI baseUri;
	private URI cliJarUri;
	private boolean noKeyAuth;
	private String privateKeyOrNull;
	private String command;
	private Proxy proxy;
	private String dowloadedCliJar;
	private Path stdin;
	private File stdinXslt;
	private Map<String, String> stdinParams;
	private Settings settings;
	private boolean noCertificateCheck;
	private File trustStore;
	private String trustStorePassword;
	private Path stdout;
	private File stdoutXslt;
	private Map<String, String> stdoutParams;
	private boolean appending;
	private File customJenkinsCliJarOrNull;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param pMessages
	 *            pMessages Facade to get messages, must not be {@code null}
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
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			// Will never happen because this class implement Cloneable
			throw new UnsupportedOperationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getWorkDirectory()
	 */
	@Override
	public Path getJenkinscliDirectory() {
		return jenkinscliDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getCustomJenkinsCliJarOrNull
	 * ()
	 */
	@Override
	public File getCustomJenkinsCliJarOrNull() {
		return customJenkinsCliJarOrNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getBaseUri()
	 */
	@Override
	public URI getBaseUri() {
		return baseUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#isNoKeyAuth()
	 */
	@Override
	public boolean isNoKeyAuth() {
		return noKeyAuth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getPrivateKeyOrNull()
	 */
	@Override
	public String getPrivateKeyOrNull() {
		return privateKeyOrNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getCommand()
	 */
	@Override
	public String getCommand() {
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getCliJarUri()
	 */
	@Override
	public URI getCliJarUri() {
		return cliJarUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getDownloadedCliJar()
	 */
	@Override
	public String getDownloadedCliJar() {
		return dowloadedCliJar;
	}

	/**
	 * Sets the absolute path to the downloaded CLI jar. See
	 * {@link Downloader#downloadCliJar(Config)}
	 */
	void setDownloadedCliJar(final String pDownloadedCliJarPath) {
		dowloadedCliJar = pDownloadedCliJarPath;
	}

	/**
	 * See {@link ConfigBuilder#setBaseUrl(java.net.URL, String)}
	 */
	void setBaseUri(final URI pBaseUri) {
		baseUri = pBaseUri;
	}

	/**
	 * Sets the concatenation of the base {@link URI} and the relative CLI jar
	 * path. See {@link ConfigBuilder#setBaseUrl(java.net.URL, String)}.
	 * 
	 * @param pCliJarUri
	 *            {@link URI} must not be {@code null}
	 */
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

	@Override
	public Path getStdoutOrNull() {
		return stdout;
	}

	@Override
	public boolean isAppending() {
		return appending;
	}

	/**
	 * @param pStdin
	 */
	void setStdin(final Path pStdin) {
		stdin = pStdin;
	}

	/**
	 * @param pStdout
	 */
	void setStdout(final Path pStdout) {
		stdout = pStdout;
	}

	void setAppending(final boolean pAppending) {
		appending = pAppending;
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
	 * @param pJenkinscliDirectory
	 */
	void setJenkinscliDirectory(final Path pJenkinscliDirectory) {
		jenkinscliDirectory = pJenkinscliDirectory;
	}

	/**
	 * @param pCustomJenkinsCliJar
	 */
	void setCustomJenkinsCliJarOrNull(final File pCustomJenkinsCliJar) {
		customJenkinsCliJarOrNull = pCustomJenkinsCliJar;
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
	void validate(final Log pLog) throws MojoExecutionException {
		// Do not allow noKeyAuth when private key is set
		if (isNoKeyAuth() && getPrivateKeyOrNull() != null) {
			throw new MojoExecutionException(messages.getMessage(
					CONFIG_VALIDATION_ERROR_NO_KEY_AUTH_AND_PRIVATE_KEY_SET,
					getPrivateKeyOrNull()));
		}

		// If a secure base-uri has been set, there must be a trust-store if
		// certificate check is not disabled.
		if (isSecure() && !isNoCertificateCheck()
				&& getTrustStoreOrNull() == null) {
			throw new MojoExecutionException(
					messages.getMessage(CONFIG_VALIDATION_ERROR_SECURE_BUT_NO_TRUSTSTORE_SPECIFIED));
		}

		// If a trust-store has been specified, insure that a password is set
		if (getTrustStoreOrNull() != null) {
			if (isBlank(getTrustStorePasswordOrNull())) {
				throw new MojoExecutionException(
						messages.getMessage(
								CONFIG_VALIDATION_ERROR_NO_TRUSTSTORE_PASSWORD_SPECIFIED,
								getTrustStoreOrNull()));
			}

			if (MIN_TRUSTSTORE_PWD_LENGTH > getTrustStorePasswordOrNull()
					.length()) {
				throw new MojoExecutionException(messages.getMessage(
						CONFIG_VALIDATION_ERROR_TRUSTSTORE_PASSWORD_TOO_SHORT,
						MIN_TRUSTSTORE_PWD_LENGTH));
			}
		}

		if (getTrustStoreOrNull() == null
				&& !isBlank(getTrustStorePasswordOrNull())) {
			pLog.warn(messages
					.getMessage(CONFIG_VALIDATION_WARN_TRUSTSTORE_PASSWORD_NOT_NECESSARY));
		}

		warnFieldCannotBeApplied(CONFIG_VALIDATION_WARN_XSLT_NOT_APPLIABLE,
				pLog, stdin, stdinXslt, STDIN_FIELD, STDIN_XSLT_FIELD);
		warnFieldCannotBeApplied(CONFIG_VALIDATION_WARN_XSLT_NOT_APPLIABLE,
				pLog, stdout, stdoutXslt, STDOUT_FIELD, STDOUT_XSLT_FIELD);
		warnFieldCannotBeApplied(CONFIG_VALIDATION_WARN_PARAMS_NOT_APPLIABLE,
				pLog, stdinXslt, stdinParams, STDIN_XSLT_FIELD,
				STDIN_PARAMS_FIELD);
		warnFieldCannotBeApplied(CONFIG_VALIDATION_WARN_PARAMS_NOT_APPLIABLE,
				pLog, stdoutXslt, stdoutParams, STDOUT_XSLT_FIELD,
				STDOUT_PARAMS_FIELD);
	}

	/**
	 * @param pLog
	 * @param pRequired
	 * @param pDependant
	 * @param pMessageKey
	 */
	private void warnFieldCannotBeApplied(final String pMessageKey,
			final Log pLog, final Object pRequired, final Object pDependant,
			final String pRequiredFieldName, final String pDependantFieldName) {
		if (pRequired == null && pDependant != null) {
			pLog.warn(messages.getMessage(pMessageKey, pDependantFieldName,
					pRequiredFieldName));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getStdinXsltOrNull()
	 */
	@Override
	public File getStdinXsltOrNull() {
		return stdinXslt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.Config#getStdoutXsltOrNull()
	 */
	@Override
	public File getStdoutXsltOrNull() {
		return stdoutXslt;
	}

	/**
	 * @param pStdinXslt
	 */
	void setStdinXslt(final File pStdinXslt) {
		stdinXslt = pStdinXslt;
	}

	/**
	 * @param pStdoutXslt
	 */
	public void setStdoutXslt(final File pStdoutXslt) {
		stdoutXslt = pStdoutXslt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getStdinParams()
	 */
	@Override
	public Map<String, String> getStdinParamsOrNull() {
		return stdinParams;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.config.Config#getStdoutParams()
	 */
	@Override
	public Map<String, String> getStdoutParamsOrNull() {
		return stdoutParams;
	}

	/**
	 * @param pStdoutParams
	 */
	void setStdinParams(final Map<String, String> pStdinParams) {
		stdinParams = pStdinParams;
	}

	/**
	 * @param pStdoutParams
	 */
	void setStdoutParams(final Map<String, String> pStdoutParams) {
		stdoutParams = pStdoutParams;
	}
}
