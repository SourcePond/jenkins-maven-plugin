/*Copyright (C) 2015 Roland Hauser, <sourcepond@gmailimport java.io.File;
import java.net.URL;

import org.apache.maven.project.MavenProject;
is file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.maven.plugin.jenkins.config;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * Builder to create new {@link Config} instances. An instance of this interface
 * can be obtained through {@link ConfigBuilderFactory#newBuilder()}.
 */
public interface ConfigBuilder {

	/**
	 * Sets the Maven settings injected into the mojo ${settings}.
	 * 
	 * @param pSettings
	 *            Settings object, must not be {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setSettings(Settings pSettings);

	/**
	 * Sets the download directory of jenkins-cli.jar, see
	 * {@link Config#getJenkinscliDirectory()}.
	 * 
	 * @param pJenkinscliDirectory
	 *            Download directory, must not be {@code null}
	 * @return This builder, never {@code null}
	 * @throws MojoExecutionException
	 *             Thrown, if the directory specified does not exist and could
	 *             not be created.
	 */
	ConfigBuilder setJenkinscliDirectory(Path pJenkinscliDirectory)
			throws MojoExecutionException;

	/**
	 * Set the custom jenkins-cli.jar to be used by this plugin, see
	 * {@link Config#getCustomJenkinsCliJarOrNull()}.
	 * 
	 * @param pCustomJenkinsCliJar
	 *            Custom jenkins-cli.jar or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setCustomJenkinsCliJar(File pCustomJenkinsCliJar);

	/**
	 * Sets the base {@link URI} where the Jenkins instance to be used is
	 * available (see {@link Config#getBaseUri()}). Furthermore, initializes the
	 * {@link URI} where the Jenkins CLI jar can be downloaded (see
	 * {@link Config#getCliJarUri()}).
	 * 
	 * @param pBaseUrl
	 *            Base {@link URL}, must not be {@code null}
	 * @param pCliJar
	 *            Relative path, must not be {@code null}
	 * @return This builder, never {@code null}
	 * @throws MojoExecutionException
	 *             Thrown, if the {@link URL} specified could not be transformed
	 *             into an {@link URI}, or, if the CLI jar {@link URI} could not
	 *             be created.
	 */
	ConfigBuilder setBaseUrl(URL pBaseUrl, String pCliJar)
			throws MojoExecutionException;

	/**
	 * Sets whether SSH authentication key loading is disabled, see
	 * {@link Config#isNoKeyAuth()}.
	 * 
	 * @param pNoKeyAuth
	 *            {@code true} if disabled, {@code false} otherwise.
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setNoKeyAuth(boolean pNoKeyAuth);

	/**
	 * Sets whether SSL certificate check is skipped entirely, see
	 * {@link Config#isNoCertificateCheck()}.
	 * 
	 * @param pNoCertificateCheck
	 *            {@code true} if skipped, {@code false} otherwise
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setNoCertificateCheck(boolean pNoCertificateCheck);

	/**
	 * Sets the SSH authentication private key, see
	 * {@link Config#getPrivateKeyOrNull()}.
	 * 
	 * @param pPrivateKeyOrNull
	 *            Private key or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setPrivateKey(File pPrivateKeyOrNull);

	/**
	 * Sets the actual command to be executed by the CLI, see
	 * {@link Config#getCommand()}.
	 * 
	 * @param pCommand
	 *            Command including all options and parameters, must not be
	 *            {@code null} or blank.
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setCommand(String pCommand);

	/**
	 * Sets the {@link File} where to redirect the standard input, see
	 * {@link Config#getStdinOrNull()}.
	 * 
	 * @param pStdin
	 *            {@link File} or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setStdin(File pStdin);

	/**
	 * Sets the XSLT {@link File} to be used to transform the standard input,
	 * see {@link Config#getStdinXsltOrNull()}.
	 * 
	 * @param pStdinXslt
	 *            {@link File} or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setStdinXslt(File pStdinXslt);

	/**
	 * Sets the {@link File} where to redirect the standard output, see
	 * {@link Config#getStdoutOrNull()}.
	 * 
	 * @param pStdout
	 *            {@link File} or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setStdout(File pStdout);

	/**
	 * Sets the XSLT {@link File} to be used to transform the standard output,
	 * see {@link Config#getStdoutXsltOrNull()}.
	 * 
	 * @param pStdoutXslt
	 *            {@link File} or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setStdoutXslt(File pStdoutXslt);

	/**
	 * Sets whether the standard output shall be appended to the file specified
	 * by {@link #setStdout(File)}. See {@link Config#isAppending()}
	 * 
	 * @param pAppend
	 * @return
	 */
	ConfigBuilder setAppend(boolean pAppend);

	/**
	 * Sets the proxy if any specified, see {@link Config#getProxyOrNull()}.
	 * 
	 * @param pProxy
	 *            {@link Proxy} instance or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setProxy(Proxy pProxy);

	/**
	 * Sets the trust-store if an SSL connection is used, see
	 * {@link Config#getTrustStoreOrNull()}.
	 * 
	 * @param pTrustStore
	 *            Trust-store {@link File} or {@code null}
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setTrustStore(File pTrustStore);

	/**
	 * Sets the trust-store password, see
	 * {@link Config#getTrustStorePasswordOrNull()}.
	 * 
	 * @param pPassword
	 *            Trust-store password or {@code null}.
	 * @return This builder, never {@code null}
	 */
	ConfigBuilder setTrustStorePassword(String pPassword);

	/**
	 * Builds a new instance of {@link Config} based on the parameters specified
	 * on this builder.
	 * 
	 * @param pLog
	 *            Logger, must not be {@code null}
	 * @return New {@link Config} instance, never {@code null}.
	 * @throws MojoExecutionException
	 *             Thrown, if the CLI jar cannot be downloaded or if a
	 *             validation fails.
	 */
	Config build(Log pLog) throws MojoExecutionException;
}
