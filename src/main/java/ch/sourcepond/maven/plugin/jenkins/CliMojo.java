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

import ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilderFactory;
import ch.sourcepond.maven.plugin.jenkins.process.ProcessFacade;
import ch.sourcepond.maven.plugin.jenkins.proxy.ProxyFinder;

/**
 * @author Roland Hauser, SourcePond
 *
 */
@Mojo(name = "cli", defaultPhase = VERIFY)
public class CliMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}/jenkins", required = true)
	private File workDirectory;

	@Parameter(defaultValue = "${project.ciManagement.url}", required = true)
	private URL baseUrl;

	@Parameter(defaultValue = "jnlpJars/jenkins-cli.jar", required = true)
	private String cliJar;

	@Parameter
	private boolean noKeyAuth;

	@Parameter
	private boolean noCertificateCheck;

	@Parameter
	private File privateKey;

	@Parameter(required = true)
	private String command;

	@Parameter
	private File stdin;

	@Parameter
	private String proxyId;

	@Parameter
	private File trustStore;

	@Parameter
	private String trustStorePassword;

	@Parameter(defaultValue = "${settings}", readonly = true, required = true)
	private Settings settings;

	private final ConfigBuilderFactory configBuilderFactory;
	private final ProcessFacade process;
	private final ProxyFinder proxyFinder;

	/**
	 * @param pConfigBuilderFactory
	 */
	@Inject
	public CliMojo(final ConfigBuilderFactory pConfigBuilderFactory,
			final ProcessFacade pProcess, final ProxyFinder pProxyFinder) {
		configBuilderFactory = pConfigBuilderFactory;
		process = pProcess;
		proxyFinder = pProxyFinder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		process.execute(
				getLog(),
				configBuilderFactory.newBuilder().setSettings(settings)
						.setProxy(proxyFinder.findProxy(proxyId, settings))
						.setWorkDirectory(workDirectory.toPath())
						.setBaseUrl(baseUrl, cliJar).setCommand(command)
						.setStdin(stdin).setNoKeyAuth(noKeyAuth)
						.setNoCertificateCheck(noCertificateCheck)
						.setPrivateKey(privateKey).setTrustStore(trustStore)
						.setTrustStorePassword(trustStorePassword).build());
	}

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
