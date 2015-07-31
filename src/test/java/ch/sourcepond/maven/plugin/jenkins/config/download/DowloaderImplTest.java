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
package ch.sourcepond.maven.plugin.jenkins.config.download;

import static ch.sourcepond.maven.plugin.jenkins.config.download.DowloaderImpl.JAR_NAME;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
public class DowloaderImplTest {

	/**
	 * @author rolandhauser
	 *
	 */
	private final static class TestInputStream extends ByteArrayInputStream {
		protected InputStream closeVerifier = mock(InputStream.class);

		public TestInputStream(final byte[] buf) {
			super(buf);
		}

		@Override
		public void close() throws IOException {
			closeVerifier.close();
		}
	}

	private final byte[] testData = new byte[8192];
	private final FileSystem fs = mock(FileSystem.class);
	private final FileSystemProvider provider = mock(FileSystemProvider.class);
	private final Path jenkinscliDirectory = mock(Path.class);
	private final Path jar = mock(Path.class);
	private final TestInputStream source = new TestInputStream(testData);
	private final OutputStream sink = mock(OutputStream.class);
	private final HttpClientFacade facade = mock(HttpClientFacade.class);
	private final HttpUriRequest request = mock(HttpUriRequest.class);
	private final CloseableHttpClient client = mock(CloseableHttpClient.class);
	private final CloseableHttpResponse response = mock(CloseableHttpResponse.class);
	private final StatusLine statusLine = mock(StatusLine.class);
	private final HttpEntity entity = mock(HttpEntity.class);
	private final Config config = mock(Config.class);
	private final DowloaderImpl impl = new DowloaderImpl(facade);
	private URI cliJarUri;

	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		cliJarUri = new URI("http://anyuri");
		testData[0] = 17;

		when(config.getCliJarUri()).thenReturn(cliJarUri);
		when(facade.newGet(cliJarUri)).thenReturn(request);
		when(facade.newClient(config)).thenReturn(client);
		when(client.execute(request)).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(response.getEntity()).thenReturn(entity);
		when(statusLine.getStatusCode()).thenReturn(SC_OK);
		when(config.getJenkinscliDirectory()).thenReturn(jenkinscliDirectory);
		when(entity.getContent()).thenReturn(source);

		when(jenkinscliDirectory.getFileSystem()).thenReturn(fs);
		when(jenkinscliDirectory.resolve(JAR_NAME)).thenReturn(jar);
		when(jar.getFileSystem()).thenReturn(fs);
		when(jar.toAbsolutePath()).thenReturn(jar);
		when(fs.provider()).thenReturn(provider);
		when(provider.newOutputStream(jar, CREATE_NEW, WRITE)).thenReturn(sink);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDownloadCliJar() throws Exception {
		impl.downloadCliJar(config);
		final InOrder order = Mockito.inOrder(sink, source.closeVerifier,
				config, response, client);
		order.verify(sink).write(aryEq(testData), Mockito.eq(0),
				Mockito.eq(testData.length));
		order.verify(sink).close();
		order.verify(source.closeVerifier).close();
		order.verify(response).close();
		order.verify(client).close();
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void verifyDownloadCliJarInvalidStatus() throws Exception {
		when(statusLine.getStatusCode()).thenReturn(SC_SERVICE_UNAVAILABLE);
		impl.downloadCliJar(config);
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void verifyDownloadCliJarNoEntity() throws Exception {
		when(response.getEntity()).thenReturn(null);
		impl.downloadCliJar(config);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDownloadCliJarIOException() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(entity).getContent();

		try {
			impl.downloadCliJar(config);
			fail("Exception expected here");
		} catch (final MojoExecutionException e) {
			assertSame(expected, e.getCause());
		}
	}
}
