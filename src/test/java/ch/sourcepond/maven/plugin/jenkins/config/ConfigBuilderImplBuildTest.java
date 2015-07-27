package ch.sourcepond.maven.plugin.jenkins.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class ConfigBuilderImplBuildTest extends ConfigBuilderImplBaseTest {
	private static final String CLI_JAR = "cliJar";
	private static final String COMMAND = "command";
	private final Settings settings = mock(Settings.class);
	private final ConfigBuilderImpl impl = new ConfigBuilderImpl(downloader);
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
		final Config config1 = impl.build();
		final Config config2 = impl.build();
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
		impl.build();
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkCliJarUriAssert() throws MojoExecutionException {
		impl.getBaseConfig().setCliJarUri(null);
		impl.build();
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkCommandAssert() throws MojoExecutionException {
		impl.getBaseConfig().setCommand(" ");
		impl.build();
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkSettingsAssert() throws MojoExecutionException {
		impl.getBaseConfig().setSettings(null);
		impl.build();
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test(expected = AssertionError.class)
	public void checkWorkDirectoryAssert() throws MojoExecutionException {
		impl.getBaseConfig().setWorkDirectory(null);
		impl.build();
	}
}
