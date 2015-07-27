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

	/**
	 * Specifies where downloaded artifacts should be stored.
	 */
	@Parameter(defaultValue = "${project.build.directory}/jenkins", required = true)
	private File workDirectory;

	/**
	 * Specifies the URL where the Jenkins instance used by this plugin is
	 * available.
	 */
	@Parameter(defaultValue = "${project.ciManagement.url}", required = true)
	private URL baseUrl;

	/**
	 * Specifies the path relative to {@link #baseUrl} where the CLI-jar
	 * (necessary to run this plugin) can be downloaded.
	 */
	@Parameter(defaultValue = "jnlpJars/jenkins-cli.jar", required = true)
	private String cliJar;

	/**
	 * Specifies, whether the CLI should skip loading the SSH authentication
	 * private key ({@code true}). This parameter will be passed as "-noKeyAuth"
	 * option to the CLI. Default is {@code false} (load private key). Note: if
	 * set to {@code true} this setting conflicts with {@link #privateKey} if
	 * {@link #privateKey} is specified.
	 */
	@Parameter
	private boolean noKeyAuth;

	/**
	 * Specifies the SSH authentication private key to be used when connecting
	 * to Jenkins. This parameter will be passed as "-i" option to the CLI. If
	 * not specified, the CLI will look for ~/.ssh/identity, ~/.ssh/id_dsa,
	 * ~/.ssh/id_rsa and those to authenticate itself against the server. Note:
	 * this setting conflicts with {@link #noKeyAuth} if {@link #noKeyAuth} is
	 * set {@code true}.
	 */
	@Parameter
	private File privateKey;

	/**
	 * Specifies, whether certificate check should completely be disabled when
	 * the CLI connects to an SSL secured Jenkins instance. This parameter will
	 * be passed as "-noCertificateCheck" option to the CLI. Default is
	 * {@code false}. This setting will bypass {@link #trustStore} and
	 * {@link #trustStorePassword}. Note: avoid enabling this switch because
	 * it's not secure (the CLI will trust everyone)!
	 */
	@Parameter
	private boolean noCertificateCheck;

	/**
	 * Specifies the Jenkins command including all its options and arguments to
	 * be executed through the CLI.
	 */
	@Parameter(required = true)
	private String command;

	/**
	 * Specifies the file from where the standard input should read from. If
	 * set, the command receives the file data through stdin (for instance
	 * useful for "create job"). If not set, stdin does not provide any data.
	 */
	@Parameter
	private File stdin;

	/**
	 * Specifies the settings-id of the <a
	 * href="https://maven.apache.org/guides/mini/guide-proxies.html"
	 * >proxy-server</a> which the CLI should use to connect to the Jenkins
	 * instance. This parameter will be passed as "-p" option to the CLI. If
	 * set, the plugin will search for the appropriate proxy-server in the Maven
	 * settings (usually {@code ~/.m2/settings.xml}).
	 */
	@Parameter
	private String proxyId;

	/**
	 * Specifies the trust-store to be used by the CLI if connecting to an SSL
	 * secured Jenkins instance. This parameter will be passed as
	 * "-Djavax.net.ssl.trustStore" option to the Java interpreter which starts
	 * the CLI.
	 */
	@Parameter
	private File trustStore;

	/**
	 * Specifies the password for the trust-store to be used by the CLI (see
	 * {@link #trustStore}). This parameter will be passed as
	 * "-Djavax.net.ssl.trustStorePassword" option to the Java interpreter which
	 * starts the CLI.
	 */
	@Parameter
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
						.setWorkDirectory(workDirectory.toPath())
						.setBaseUrl(baseUrl, cliJar).setCommand(command)
						.setStdin(stdin).setNoKeyAuth(noKeyAuth)
						.setNoCertificateCheck(noCertificateCheck)
						.setPrivateKey(privateKey).setTrustStore(trustStore)
						.setTrustStorePassword(trustStorePassword).build());
	}

	/**
	 * Mojo parameter <em>workDirectory</em>. Sets the working-directory where
	 * to store downloaded artifacts. Default is
	 * <em>${project.build.directory}/jenkins</em>.
	 * 
	 * @param pWorkDirectory
	 */
	public void setWorkDirectory(final File pWorkDirectory) {
		workDirectory = pWorkDirectory;
	}

	public void setBaseUrl(final URL pBaseUrl) {
		baseUrl = pBaseUrl;
	}

	public void setCliJar(final String pCliJar) {
		cliJar = pCliJar;
	}

	public void setNoKeyAuth(final boolean pNoKeyAuth) {
		noKeyAuth = pNoKeyAuth;
	}

	public void setNoCertificateCheck(final boolean pNoCertificateCheck) {
		noCertificateCheck = pNoCertificateCheck;
	}

	public void setPrivateKey(final File pPrivateKey) {
		privateKey = pPrivateKey;
	}

	public void setCommand(final String pCommand) {
		command = pCommand;
	}

	public void setStdin(final File pStdin) {
		stdin = pStdin;
	}

	public void setSettings(final Settings pSettings) {
		settings = pSettings;
	}

	public void setProxyId(final String pProxyId) {
		proxyId = pProxyId;
	}

	public void setTrustStore(final File pTrustStore) {
		trustStore = pTrustStore;
	}

	public void setTrustStorePassword(final String pPassword) {
		trustStorePassword = pPassword;
	}

	/**
	 * @return
	 */
	// TODO: Getter is only used by integration-test. Find a better solution and
	// remove this method
	public Settings getSettings() {
		return settings;
	}
}
