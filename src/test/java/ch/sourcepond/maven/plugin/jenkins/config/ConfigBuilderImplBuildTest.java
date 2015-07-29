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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class ConfigBuilderImplBuildTest extends ConfigBuilderImplBaseTest {
	private static final String CLI_JAR = "cliJar";
	private static final String COMMAND = "command";
	private final Log log = mock(Log.class);
	private final Settings settings = mock(Settings.class);
	private URL baseUrl;

	/**
	 * 
	 */
	@Override
	@Before
	public void setup() throws Exception {
		super.setup();
		baseUrl = new URL("http://jenkins.org");
		impl.setBaseUrl(baseUrl, CLI_JAR).setCommand(COMMAND)
				.setSettings(settings).setWorkDirectory(workDirectory);
	}

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Test
	public void verifyConfigIsCloned() throws MojoExecutionException {
		final Config config1 = impl.build(log);
		final Config config2 = impl.build(log);
		assertNotNull(config1);
		assertNotNull(config2);
		assertNotSame(config1, config2);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkBaseUriAssert() throws MojoExecutionException {
		impl.getBaseConfig().setBaseUri(null);
		impl.build(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkCliJarUriAssert() throws MojoExecutionException {
		impl.getBaseConfig().setCliJarUri(null);
		impl.build(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkCommandAssert() throws MojoExecutionException {
		impl.getBaseConfig().setCommand(" ");
		impl.build(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkSettingsAssert() throws MojoExecutionException {
		impl.getBaseConfig().setSettings(null);
		impl.build(log);
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkWorkDirectoryAssert() throws MojoExecutionException {
		impl.getBaseConfig().setWorkDirectory(null);
		impl.build(log);
	}
}
