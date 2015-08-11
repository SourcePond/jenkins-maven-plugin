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
package ch.sourcepond.maven.plugin.jenkins.process.xslt;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 */
public class XsltTransformerImplTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private static final String PARAM_KEY = "paramKey";
	private static final String PARAM_VALUE = "paramValue";
	private static final File XSLT_FILE = new File("file:///anyXslt");
	private final TransformerFactory transformerFactory = mock(TransformerFactory.class);
	private final Transformer transformer = mock(Transformer.class);
	private final Source xsltSource = mock(Source.class);
	private final StreamFactory streamFactory = mock(StreamFactory.class);
	private final InputStream stdin = mock(InputStream.class);
	private final OutputStream stdout = mock(OutputStream.class);
	private final InputStream transformerIn = mock(InputStream.class);
	private final OutputStream transformerOut = mock(OutputStream.class);
	private final Map<String, String> params = new HashMap<>();
	private final XsltTransformerImpl impl = new XsltTransformerImpl(
			transformerFactory, streamFactory);

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		params.put(PARAM_KEY, PARAM_VALUE);
		when(streamFactory.newSource(XSLT_FILE)).thenReturn(xsltSource);
		when(transformerFactory.newTransformer(xsltSource)).thenReturn(
				transformer);
		when(streamFactory.newTransformerInputStream(stdin, transformer))
				.thenReturn(transformerIn);
		when(streamFactory.newTransformerOutputStream(stdout, transformer))
				.thenReturn(transformerOut);
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdinNoWrapNecessary() throws IOException {
		assertSame(stdin, impl.wrapIfNecessary(null, stdin, null));
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdin() throws IOException {
		assertSame(transformerIn,
				impl.wrapIfNecessary(XSLT_FILE, stdin, params));
		verify(transformer).setParameter(PARAM_KEY, PARAM_VALUE);
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdinNoParams() throws IOException {
		assertSame(transformerIn, impl.wrapIfNecessary(XSLT_FILE, stdin, null));
		verify(transformer, Mockito.never()).setParameter(Mockito.anyString(),
				Mockito.any());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdinExceptionOccurred() throws Exception {
		final TransformerConfigurationException expected = new TransformerConfigurationException(
				ANY_MESSAGE);
		doThrow(expected).when(transformerFactory).newTransformer(xsltSource);

		try {
			impl.wrapIfNecessary(XSLT_FILE, stdin, params);
			fail("Exception expected!");
		} catch (final IOException e) {
			assertSame(expected, e.getCause());
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdoutNoWrapNecessary() throws IOException {
		assertSame(stdout, impl.wrapIfNecessary(null, stdout, null));
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdout() throws IOException {
		assertSame(transformerOut,
				impl.wrapIfNecessary(XSLT_FILE, stdout, params));
		verify(transformer).setParameter(PARAM_KEY, PARAM_VALUE);
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdoutNoParams() throws IOException {
		assertSame(transformerOut,
				impl.wrapIfNecessary(XSLT_FILE, stdout, null));
		verify(transformer, Mockito.never()).setParameter(Mockito.anyString(),
				Mockito.any());
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void verifyWrapStdoutExceptionOccurred() throws Exception {
		final TransformerConfigurationException expected = new TransformerConfigurationException(
				ANY_MESSAGE);
		doThrow(expected).when(transformerFactory).newTransformer(xsltSource);

		try {
			impl.wrapIfNecessary(XSLT_FILE, stdout, params);
			fail("Exception expected!");
		} catch (final IOException e) {
			assertSame(expected, e.getCause());
		}
	}
}
