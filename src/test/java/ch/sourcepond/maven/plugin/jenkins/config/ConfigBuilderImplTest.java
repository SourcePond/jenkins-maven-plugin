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

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Test;

/**
 * @author rolandhauser
 *
 */
public class ConfigBuilderImplTest {
	private static final String ANY_STRING = "anyString";
	private final FileSystem fs = mock(FileSystem.class);
	private final FileSystemProvider provider = mock(FileSystemProvider.class);
	private final Path workDirectory = mock(Path.class);
	private final ConfigBuilderImpl impl = new ConfigBuilderImpl();

	/**
	 * 
	 */
	@Before
	public void setup() {
		when(fs.provider()).thenReturn(provider);
		when(workDirectory.getFileSystem()).thenReturn(fs);
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetWorkDirectory() throws Exception {
		assertSame(impl, impl.setWorkDirectory(workDirectory));
		assertSame(workDirectory, impl.getWorkDirectory());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetWorkDirectoryIoException() throws Exception {
		final FileAlreadyExistsException expected = new FileAlreadyExistsException(
				"any");
		final BasicFileAttributes attrs = mock(BasicFileAttributes.class);
		when(
				provider.readAttributes(workDirectory,
						BasicFileAttributes.class, NOFOLLOW_LINKS)).thenReturn(
				attrs);
		doThrow(expected).when(provider).createDirectory(workDirectory);

		try {
			impl.setWorkDirectory(workDirectory);
			fail("Exception expected");
		} catch (final MojoExecutionException e) {
			assertSame(expected, e.getCause());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifySetGetBaseUrl() throws Exception {
		final URL baseUrl = new URL("http://valid.org");
		assertSame(impl, impl.setBaseUrl(baseUrl, "/any/path/to/cli.jar"));
		assertEquals("http://valid.org", impl.getBaseUri().toString());
		assertEquals("http://valid.org/any/path/to/cli.jar", impl
				.getCliJarUri().toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifySetGetBaseUrlNoLeadingSlash() throws Exception {
		final URL baseUrl = new URL("http://valid.org");
		assertSame(impl, impl.setBaseUrl(baseUrl, "any/path/to/cli.jar"));
		assertEquals("http://valid.org", impl.getBaseUri().toString());
		assertEquals("http://valid.org/any/path/to/cli.jar", impl
				.getCliJarUri().toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifySetGetBaseUrlInvalidUrl() throws Exception {
		final URL baseUrl = new URL("http://valid.org");
		try {
			impl.setBaseUrl(baseUrl, "[invalid]");
			fail("Exception expected!");
		} catch (final MojoExecutionException e) {
			assertEquals(URISyntaxException.class, e.getCause().getClass());
		}
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetDownloadedCliJar() {
		impl.setDownloadedCliJar(ANY_STRING);
		assertEquals(ANY_STRING, impl.getDownloadedCliJar());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetCommand() {
		assertSame(impl, impl.setCommand(ANY_STRING));
		assertEquals(ANY_STRING, impl.getCommand());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetPrivateKey() {
		final File privateKey = new File(ANY_STRING);
		assertSame(impl, impl.setPrivateKey(privateKey));
		assertEquals(privateKey.getAbsolutePath(), impl.getPrivateKeyOrNull());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetNullPrivateKey() {
		assertSame(impl, impl.setPrivateKey(null));
		assertNull(impl.getPrivateKeyOrNull());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetStdin() {
		final File privateKey = new File(ANY_STRING);
		assertSame(impl, impl.setStdin(privateKey));
		assertEquals(privateKey, impl.getStdin().toFile());
	}

	/**
	 * 
	 */
	@Test
	public void verifyBuild() {
		assertSame(impl, impl.build());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetNullStdin() {
		assertSame(impl, impl.setStdin(null));
		assertNull(impl.getStdin());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetNoKeyAuth() {
		assertFalse(impl.isNoKeyAuth());
		assertSame(impl, impl.setNoKeyAuth(true));
		assertTrue(impl.isNoKeyAuth());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetNoCertificateCheck() {
		assertFalse(impl.isNoCertificateCheck());
		assertSame(impl, impl.setNoCertificateCheck(true));
		assertTrue(impl.isNoCertificateCheck());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetProxy() {
		final Proxy proxy = mock(Proxy.class);
		assertSame(impl, impl.setProxy(proxy));
		assertSame(proxy, impl.getProxyOrNull());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetSettings() {
		final Settings settings = mock(Settings.class);
		assertSame(impl, impl.setSettings(settings));
		assertSame(settings, impl.getSettings());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetTrustStore() {
		final File trustStore = new File(ANY_STRING);
		assertSame(impl, impl.setTrustStore(trustStore));
		assertEquals(trustStore, impl.getTrustStoreOrNull());
	}

	/**
	 * 
	 */
	@Test
	public void verifySetGetTrustStorePassword() {
		assertSame(impl, impl.setTrustStorePassword(ANY_STRING));
		assertEquals(ANY_STRING, impl.getTrustStorePasswordOrNull());
	}

	/**
	 * 
	 */
	@Test
	public void verifyIsSecure() throws Exception {
		impl.setBaseUrl(new URL("http://valid.ord"), "any");
		assertFalse(impl.isSecure());
		impl.setBaseUrl(new URL("https:/valid.ord"), "any");
		assertTrue(impl.isSecure());
	}
}
