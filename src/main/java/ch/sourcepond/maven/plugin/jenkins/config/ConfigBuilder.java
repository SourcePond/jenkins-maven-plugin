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
import java.net.URL;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * @author rolandhauser
 *
 */
public interface ConfigBuilder {

	ConfigBuilder setSettings(Settings pSettings);

	/**
	 * @param pWorkDirectory
	 * @return
	 * @throws MojoExecutionException
	 */
	ConfigBuilder setWorkDirectory(Path pWorkDirectory)
			throws MojoExecutionException;

	ConfigBuilder setBaseUrl(URL pBaseUrl, String pCliJar)
			throws MojoExecutionException;

	ConfigBuilder setNoKeyAuth(boolean pNoKeyAuth);

	ConfigBuilder setNoCertificateCheck(boolean pNoCertificateCheck);

	ConfigBuilder setPrivateKey(File pPrivateKey);

	ConfigBuilder setCommand(String pCommand);

	ConfigBuilder setStdin(File pStdin);

	ConfigBuilder setProxy(Proxy pProxy);

	ConfigBuilder setTrustStore(File pKeystore);

	ConfigBuilder setTrustStorePassword(String pPassword);

	Config build();
}
