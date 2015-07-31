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

import static ch.sourcepond.maven.plugin.jenkins.config.download.DownloaderImpl.DOWNLOADER_INFO_VERSION_FOUND;
import static ch.sourcepond.maven.plugin.jenkins.config.download.DownloaderImpl.JAR_NAME;
import static ch.sourcepond.maven.plugin.jenkins.config.download.DownloaderImpl.VERSION_HEADER_NAME;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
public class DownloaderImplTest {

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

	private static final String ANY_STRING = "anyString";
	private static final String JENKINS_VERSION = "1.609.2";
	private final byte[] testData = new byte[8192];
	private final FileSystem fs = mock(FileSystem.class);
	private final FileSystemProvider provider = mock(FileSystemProvider.class);
	private final Path jenkinscliDirectory = mock(Path.class);
	private final Path versionDirectory = mock(Path.class);
	private final BasicFileAttributes versionDirectoryAttrs = mock(BasicFileAttributes.class);
	private final Path jar = mock(Path.class);
	private final BasicFileAttributes jarAttrs = mock(BasicFileAttributes.class);
	private final TestInputStream source = new TestInputStream(testData);
	private final OutputStream sink = mock(OutputStream.class);
	private final HttpClientFacade facade = mock(HttpClientFacade.class);
	private final HttpUriRequest versionRequest = mock(HttpUriRequest.class);
	private final HttpUriRequest downloadRequest = mock(HttpUriRequest.class);
	private final CloseableHttpClient client = mock(CloseableHttpClient.class);
	private final CloseableHttpResponse versionResponse = mock(CloseableHttpResponse.class);
	private final Header[] versionHeader = new Header[] { mock(Header.class) };
	private final CloseableHttpResponse downloadResponse = mock(CloseableHttpResponse.class);
	private final StatusLine versionStatusLine = mock(StatusLine.class);
	private final StatusLine downloadStatusLine = mock(StatusLine.class);
	private final HttpEntity entity = mock(HttpEntity.class);
	private final Config config = mock(Config.class);
	private final Messages messages = mock(Messages.class);
	private final Log log = mock(Log.class);
	private final DownloaderImpl impl = new DownloaderImpl(messages, facade);
	private URI baseUri;
	private URI cliJarUri;

	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		baseUri = new URI("http://anyBaseUri");
		cliJarUri = new URI("http://anyCliUri");
		testData[0] = 17;

		when(config.getBaseUri()).thenReturn(baseUri);
		when(config.getCliJarUri()).thenReturn(cliJarUri);
		when(facade.newGet(baseUri)).thenReturn(versionRequest);
		when(facade.newGet(cliJarUri)).thenReturn(downloadRequest);
		when(facade.newClient(config)).thenReturn(client);

		when(versionResponse.containsHeader(VERSION_HEADER_NAME)).thenReturn(
				true);
		when(versionHeader[0].getValue()).thenReturn(JENKINS_VERSION);
		when(versionResponse.getHeaders(VERSION_HEADER_NAME)).thenReturn(
				versionHeader);

		when(client.execute(versionRequest)).thenReturn(versionResponse);
		when(client.execute(downloadRequest)).thenReturn(downloadResponse);
		when(versionResponse.getStatusLine()).thenReturn(versionStatusLine);
		when(downloadResponse.getStatusLine()).thenReturn(downloadStatusLine);
		when(downloadResponse.getEntity()).thenReturn(entity);
		when(downloadStatusLine.getStatusCode()).thenReturn(SC_OK);
		when(config.getJenkinscliDirectory()).thenReturn(jenkinscliDirectory);
		when(entity.getContent()).thenReturn(source);

		when(jenkinscliDirectory.getFileSystem()).thenReturn(fs);
		when(jenkinscliDirectory.resolve(JENKINS_VERSION)).thenReturn(
				versionDirectory);
		when(versionDirectoryAttrs.isDirectory()).thenReturn(true);
		when(
				provider.readAttributes(versionDirectory,
						BasicFileAttributes.class)).thenReturn(
				versionDirectoryAttrs);
		when(versionDirectory.getFileSystem()).thenReturn(fs);
		when(versionDirectory.resolve(JAR_NAME)).thenReturn(jar);
		when(provider.readAttributes(jar, BasicFileAttributes.class))
				.thenReturn(jarAttrs);
		when(jar.getFileSystem()).thenReturn(fs);
		when(jar.toAbsolutePath()).thenReturn(jar);
		when(fs.provider()).thenReturn(provider);
		when(provider.newOutputStream(jar, CREATE_NEW, WRITE)).thenReturn(sink);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void verifyInvalidVersionHeaderArraySize()
			throws MojoExecutionException {
		when(versionResponse.getHeaders(VERSION_HEADER_NAME)).thenReturn(
				new Header[0]);
		impl.downloadCliJar(log, config);
	}

	/**
	 * 
	 */
	@Test
	public void verifyLogVersionIfInfoEnabled() throws MojoExecutionException {
		when(log.isInfoEnabled()).thenReturn(true);
		when(
				messages.getMessage(DOWNLOADER_INFO_VERSION_FOUND,
						JENKINS_VERSION)).thenReturn(ANY_STRING);
		impl.downloadCliJar(log, config);
		verify(log).info(ANY_STRING);
	}

	/**
	 * 
	 */
	@Test
	public void verifyJenkinsVersionResponseHasNoVersionHeader() {
		when(versionResponse.containsHeader(VERSION_HEADER_NAME)).thenReturn(
				false);
		when(
				messages.getMessage(
						DownloaderImpl.DOWNLOADER_ERROR_NO_VERSION_HEADER,
						baseUri, VERSION_HEADER_NAME)).thenReturn(ANY_STRING);
		try {
			impl.downloadCliJar(log, config);
			fail("Exception expected!");
		} catch (final MojoExecutionException e) {
			assertEquals(ANY_STRING, e.getMessage());
		}
	}

	/**
	 * @throws MojoExecutionException
	 */
	@Test
	public void verifyCreateVersionDirectory() throws Exception {
		when(versionDirectoryAttrs.isDirectory()).thenReturn(false);
		impl.downloadCliJar(log, config);
		verify(provider).createDirectory(versionDirectory);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDownloadCliJar() throws Exception {
		assertEquals(jar.toString(), impl.downloadCliJar(log, config));
		final InOrder order = Mockito.inOrder(sink, source.closeVerifier,
				config, downloadResponse, client);
		order.verify(sink).write(aryEq(testData), Mockito.eq(0),
				Mockito.eq(testData.length));
		order.verify(sink).close();
		order.verify(source.closeVerifier).close();
		order.verify(downloadResponse).close();
		order.verify(client).close();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDownloadCliJarAlreadyExisting() throws Exception {
		when(jarAttrs.isRegularFile()).thenReturn(true);
		assertEquals(jar.toString(), impl.downloadCliJar(log, config));
		final InOrder order = Mockito.inOrder(sink, source.closeVerifier,
				config, downloadResponse, client);
		order.verify(sink, never()).write(aryEq(testData), Mockito.eq(0),
				Mockito.eq(testData.length));
		order.verify(sink, never()).close();
		order.verify(source.closeVerifier, never()).close();
		order.verify(downloadResponse, never()).close();
		order.verify(client).close();
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void verifyDownloadCliJarInvalidStatus() throws Exception {
		when(downloadStatusLine.getStatusCode()).thenReturn(
				SC_SERVICE_UNAVAILABLE);
		impl.downloadCliJar(log, config);
	}

	/**
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void verifyDownloadCliJarNoEntity() throws Exception {
		when(downloadResponse.getEntity()).thenReturn(null);
		impl.downloadCliJar(log, config);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyDownloadCliJarIOException() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(entity).getContent();

		try {
			impl.downloadCliJar(log, config);
			fail("Exception expected here");
		} catch (final MojoExecutionException e) {
			assertSame(expected, e.getCause());
		}
	}
}
