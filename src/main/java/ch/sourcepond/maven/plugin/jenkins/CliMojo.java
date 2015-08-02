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
package ch.sourcepond.maven.plugin.jenkins;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilder;
import ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilderFactory;
import ch.sourcepond.maven.plugin.jenkins.process.ProcessFacade;
import ch.sourcepond.maven.plugin.jenkins.proxy.ProxyFinder;

/**
 * The only implementation of {@link AbstractMojo} of this plugin. The name of
 * the mojo is <em>cli</em> and its <a href=
 * "https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference"
 * >default-phase</a> is <em>verify</em>.
 */
@Mojo(name = "cli", defaultPhase = VERIFY)
public class CliMojo extends AbstractMojo {
	private static final String JENKINS_PREFIX = "jenkins.";
	static final String PROPERTY_CLI_DIRECTORY = JENKINS_PREFIX
			+ "cliDirectory";
	static final String PROPERTY_CUSTOM_CLI_JAR = JENKINS_PREFIX
			+ "customCliJar";
	static final String PROPERTY_BASE_URL = JENKINS_PREFIX + "baseURL";
	static final String PROPERTY_CLI_JAR = JENKINS_PREFIX + "cliJar";
	static final String PROPERTY_NO_KEY_AUTH = JENKINS_PREFIX + "noKeyAuth";
	static final String PROPERTY_PRIVATE_KEY = JENKINS_PREFIX + "privateKey";
	static final String PROPERTY_NO_CERTIFICATE_CHECK = JENKINS_PREFIX
			+ "noCertificateCheck";
	static final String PROPERTY_COMMAND = JENKINS_PREFIX + "command";
	static final String PROPERTY_STDIN = JENKINS_PREFIX + "stdin";
	static final String PROPERTY_STDOUT = JENKINS_PREFIX + "stdout";
	static final String PROPERTY_APPEND = JENKINS_PREFIX + "append";
	static final String PROPERTY_PROXY_ID = JENKINS_PREFIX + "proxyId";
	static final String PROPERTY_TRUST_STORE = JENKINS_PREFIX + "trustStore";
	static final String PROPERTY_TRUST_STORE_PASSWORD = JENKINS_PREFIX
			+ "trustStorePassword";

	/**
	 * Specifies where the downloaded jenkins-cli.jar should be stored.
	 */
	@Parameter(property = PROPERTY_CLI_DIRECTORY, defaultValue = "${user.home}/.m2/jenkinscli", required = true)
	private File jenkinscliDirectory;

	/**
	 * Specifies a custom jenkins-cli.jar to be used by this plugin. If set,
	 * downloading jenkins-cli.jar from the Jenkins instance specified with
	 * <em>baseUrl</em> will completely be bypassed.
	 */
	@Parameter(property = PROPERTY_CUSTOM_CLI_JAR)
	private File customJenkinsCliJar;

	/**
	 * Specifies the URL where the Jenkins instance (which shall be used by this
	 * plugin) is available.
	 */
	@Parameter(property = PROPERTY_BASE_URL, defaultValue = "${project.ciManagement.url}", required = true)
	private URL baseUrl;

	/**
	 * Specifies the path relative to {@link #baseUrl} where the CLI-jar
	 * (necessary to run this plugin) can be downloaded.
	 */
	@Parameter(property = PROPERTY_CLI_JAR, defaultValue = "jnlpJars/jenkins-cli.jar", required = true)
	private String cliJar;

	/**
	 * Specifies, whether the CLI should skip loading the SSH authentication
	 * private key ({@code true}). This parameter will be passed as "-noKeyAuth"
	 * option to the CLI. Default is {@code false} (load private key). Note: if
	 * set to {@code true} this setting conflicts with {@link #privateKey} if
	 * {@link #privateKey} is specified.
	 */
	@Parameter(property = PROPERTY_NO_KEY_AUTH)
	private boolean noKeyAuth;

	/**
	 * Specifies the SSH authentication private key to be used when connecting
	 * to Jenkins. This parameter will be passed as "-i" option to the CLI. If
	 * not specified, the CLI will look for ~/.ssh/identity, ~/.ssh/id_dsa,
	 * ~/.ssh/id_rsa and those to authenticate itself against the server. Note:
	 * this setting conflicts with {@link #noKeyAuth} if {@link #noKeyAuth} is
	 * set {@code true}.
	 */
	@Parameter(property = PROPERTY_PRIVATE_KEY)
	private File privateKey;

	/**
	 * Specifies, whether certificate check should completely be disabled when
	 * the CLI connects to an SSL secured Jenkins instance. This parameter will
	 * be passed as "-noCertificateCheck" option to the CLI. Default is
	 * {@code false}. This setting will bypass {@link #trustStore} and
	 * {@link #trustStorePassword}. Note: avoid enabling this switch because
	 * it's not secure (the CLI will trust everyone)!
	 */
	@Parameter(property = PROPERTY_NO_CERTIFICATE_CHECK)
	private boolean noCertificateCheck;

	/**
	 * Specifies the Jenkins command including all its options and arguments to
	 * be executed through the CLI.
	 */
	@Parameter(property = PROPERTY_COMMAND, required = true)
	private String command;

	/**
	 * Specifies the file from where the standard input should read from. If
	 * set, the command receives the file data through stdin (for instance
	 * useful for "create job"). If not set, stdin does not provide any data.
	 */
	@Parameter(property = PROPERTY_STDIN)
	private File stdin;

	/**
	 * Specifies the file where the standard output of the CLI should be
	 * written. If set, the command sends the data received through stdout to
	 * the file specified (useful for example if the output of a command like
	 * "list-jobs" should be further processed). If not set, stdout is only
	 * written to the log. Note: if <em>stdout</em> is set to {@code false}
	 * (default) the target file will be replaced.
	 */
	@Parameter(property = PROPERTY_STDOUT)
	private File stdout;

	/**
	 * Specifies whether the target file defined by {@link #stdout} should be
	 * replaced if existing (default). If set to {@code true} and the target
	 * file exists, all data will be appended to the existing file.
	 */
	@Parameter(property = PROPERTY_APPEND)
	private boolean append;

	/**
	 * Specifies the settings-id of the <a
	 * href="https://maven.apache.org/guides/mini/guide-proxies.html"
	 * >proxy-server</a> which the CLI should use to connect to the Jenkins
	 * instance. This parameter will be passed as "-p" option to the CLI. If
	 * set, the plugin will search for the appropriate proxy-server in the Maven
	 * settings (usually {@code ~/.m2/settings.xml}).
	 */
	@Parameter(property = PROPERTY_PROXY_ID)
	private String proxyId;

	/**
	 * Specifies the trust-store to be used by the CLI if connecting to an SSL
	 * secured Jenkins instance. This parameter will be passed as
	 * "-Djavax.net.ssl.trustStore" option to the Java interpreter which starts
	 * the CLI.
	 */
	@Parameter(property = PROPERTY_TRUST_STORE)
	private File trustStore;

	/**
	 * Specifies the password for the trust-store to be used by the CLI (see
	 * {@link #trustStore}). This parameter will be passed as
	 * "-Djavax.net.ssl.trustStorePassword" option to the Java interpreter which
	 * starts the CLI.
	 */
	@Parameter(property = PROPERTY_TRUST_STORE_PASSWORD)
	private String trustStorePassword;

	/**
	 * Settings injected by Maven.
	 */
	@Parameter(defaultValue = "${settings}", readonly = true, required = true)
	private Settings settings;

	private final ConfigBuilderFactory cbf;
	private final ProcessFacade proc;
	private final ProxyFinder pf;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param pCbf
	 *            Factory for creating a {@link ConfigBuilder} instance, must
	 *            not be {@code null}
	 * @param pProc
	 *            Facade for starting an external process, must not be
	 *            {@code null}
	 * @param pPf
	 *            Utility for determining the proxy-server to use (if any), must
	 *            not be {@code null}
	 */
	@Inject
	public CliMojo(final ConfigBuilderFactory pCbf, final ProcessFacade pProc,
			final ProxyFinder pPf) {
		cbf = pCbf;
		proc = pProc;
		pf = pPf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		proc.execute(
				getLog(),
				cbf.newBuilder().setSettings(settings)
						.setProxy(pf.findProxy(proxyId, settings))
						.setJenkinscliDirectory(jenkinscliDirectory.toPath())
						.setCustomJenkinsCliJar(customJenkinsCliJar)
						.setBaseUrl(baseUrl, cliJar).setCommand(command)
						.setStdin(stdin).setStdout(stdout).setAppend(append)
						.setNoKeyAuth(noKeyAuth)
						.setNoCertificateCheck(noCertificateCheck)
						.setPrivateKey(privateKey).setTrustStore(trustStore)
						.setTrustStorePassword(trustStorePassword)
						.build(getLog()));
	}

	/**
	 * Sets the download directory for jenkins-cli.jar, see
	 * {@link #jenkinscliDirectory}.
	 * 
	 * @param pJenkinscliDirectory
	 *            Download directory, must not be {@code null}.
	 */
	public void setJenkinscliDirectory(final File pJenkinscliDirectory) {
		jenkinscliDirectory = pJenkinscliDirectory;
	}

	/**
	 * Sets the base URL specified, see {@link #baseUrl}.
	 * 
	 * @param pBaseUrl
	 *            Base URL, must no be {@code null}
	 */
	public void setBaseUrl(final URL pBaseUrl) {
		baseUrl = pBaseUrl;
	}

	/**
	 * Sets the relative CLI-jar path, see {@link #cliJar}.
	 * 
	 * @param pCliJar
	 *            Relative path, must not be {@code null}
	 */
	public void setCliJar(final String pCliJar) {
		cliJar = pCliJar;
	}

	/**
	 * See {@link #noKeyAuth}
	 * 
	 * @param pNoKeyAuth
	 *            {@code true} if loading of SSH authentication private key
	 *            skipped, {@code false} otherwise.
	 */
	public void setNoKeyAuth(final boolean pNoKeyAuth) {
		noKeyAuth = pNoKeyAuth;
	}

	/**
	 * See {@link #noCertificateCheck}
	 * 
	 * @param pNoCertificateCheck
	 *            {@code true} if certificate check should be skipped,
	 *            {@code false} otherwise.
	 */
	public void setNoCertificateCheck(final boolean pNoCertificateCheck) {
		noCertificateCheck = pNoCertificateCheck;
	}

	/**
	 * Sets the SSH authentication private key, see {@link #privateKey}.
	 * 
	 * @param pPrivateKeyOrNull
	 *            Private key or {@code null}.
	 */
	public void setPrivateKey(final File pPrivateKeyOrNull) {
		privateKey = pPrivateKeyOrNull;
	}

	/**
	 * Sets the command to be executed through the CLI, see {@link #command}.
	 * 
	 * @param pCommand
	 *            Command, must not be {@code null}
	 */
	public void setCommand(final String pCommand) {
		command = pCommand;
	}

	/**
	 * Sets the standard input where the CLI command should read from, see
	 * {@link #stdin}.
	 * 
	 * @param pStdinOrNull
	 *            Standard input or {@code null}
	 */
	public void setStdin(final File pStdinOrNull) {
		stdin = pStdinOrNull;
	}

	/**
	 * @param pSettings
	 */
	// TODO: This method is only used by tests; find a better solution and
	// remove this method.
	public void setSettings(final Settings pSettings) {
		settings = pSettings;
	}

	/**
	 * Sets the id of the proxy server to be used by the CLI, see
	 * {@link #proxyId}.
	 * 
	 * @param pProxyIdOrNull
	 *            Proxy-id or {@code null}
	 */
	public void setProxyId(final String pProxyIdOrNull) {
		proxyId = pProxyIdOrNull;
	}

	/**
	 * Sets the trust-store to be used by the CLI, see {@link #trustStore}.
	 * 
	 * @param pTrustStoreOrNull
	 *            Trust-store or {@code null}
	 */
	public void setTrustStore(final File pTrustStoreOrNull) {
		trustStore = pTrustStoreOrNull;
	}

	/**
	 * Sets the password of the trust-store used by the CLI, see
	 * {@link #trustStorePassword}.
	 * 
	 * @param pPassword
	 *            Password
	 */
	public void setTrustStorePassword(final String pPassword) {
		trustStorePassword = pPassword;
	}

	/**
	 * Returns the Maven settings injected via ${settings}
	 * 
	 * @return Maven settings, never {@code null}
	 */
	// TODO: Getter is only used by integration-test. Find a better solution and
	// remove this method
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param pStdout
	 */
	public void setStdout(final File pStdout) {
		stdout = pStdout;
	}

	/**
	 * @param pAppend
	 */
	public void setAppend(final boolean pAppend) {
		append = pAppend;
	}

	/**
	 * @param pCustomJenkinsCliJar
	 */
	public void setCustomJenkinsCliJar(final File pCustomJenkinsCliJar) {
		customJenkinsCliJar = pCustomJenkinsCliJar;
	}
}
