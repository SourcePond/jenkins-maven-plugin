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
package ch.sourcepond.maven.plugin.jenkins.process;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
public class RedirectStreamFactoryImplTest {
	private static final String TEST_LINE = "testline";
	private final Log log = mock(Log.class);
	private final Path stdinPath = mock(Path.class);
	private final Path stdoutPath = mock(Path.class);
	private final FileSystem fs = mock(FileSystem.class);
	private final FileSystemProvider provider = mock(FileSystemProvider.class);
	private final InputStream stdin = mock(InputStream.class);
	private final OutputStream stdout = mock(OutputStream.class);
	private final Config config = mock(Config.class);
	private final RedirectStreamFactoryImpl impl = new RedirectStreamFactoryImpl();

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		when(stdinPath.getFileSystem()).thenReturn(fs);
		when(stdoutPath.getFileSystem()).thenReturn(fs);
		when(fs.provider()).thenReturn(provider);
		when(provider.newInputStream(stdinPath)).thenReturn(stdin);
		when(config.getStdinOrNull()).thenReturn(stdinPath);
	}

	/**
	 * 
	 */
	@Test
	public void verifyOutputRedirect() throws IOException {
		final OutputStream out = impl.newLogRedirect(log);
		out.write((TEST_LINE + "\n").getBytes());
		verify(log).info(TEST_LINE);
		verifyNoMoreInteractions(log);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyOpenStdin() throws Exception {
		assertSame(stdin, impl.newStdin(config));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyOpenStdinFail() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(provider).newInputStream(stdinPath);

		try {
			impl.newStdin(config);
			fail("Exception expected");
		} catch (final IOException e) {
			assertSame(expected, e);
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyOpenStdinNoInput() throws Exception {
		when(config.getStdinOrNull()).thenReturn(null);
		final InputStream stdin = impl.newStdin(config);
		assertEquals(-1, stdin.read());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyStdoutTruncateMode() throws Exception {
		when(config.getStdoutOrNull()).thenReturn(stdoutPath);
		when(provider.newOutputStream(stdoutPath)).thenReturn(stdout);
		assertSame(stdout, impl.newStdout(config));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyStdoutAppendMode() throws Exception {
		when(config.isAppending()).thenReturn(true);
		when(config.getStdoutOrNull()).thenReturn(stdoutPath);
		when(provider.newOutputStream(stdoutPath, APPEND)).thenReturn(stdout);
		assertSame(stdout, impl.newStdout(config));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyNoStdout() throws Exception {
		when(provider.newOutputStream(stdoutPath, APPEND)).thenReturn(stdout);
		final OutputStream stdout = impl.newStdout(config);
		assertEquals(NULL_OUTPUT_STREAM, stdout);
	}
}
