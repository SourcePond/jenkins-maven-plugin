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

import static ch.sourcepond.maven.plugin.jenkins.process.xslt.TransformerInputStream.EOF;
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

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class TransformerInputStreamTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private static final byte[] DATA = new byte[] { 0, 1 };
	private final InputStream stdin = mock(InputStream.class);
	private final StreamFactory streamFactory = mock(StreamFactory.class);
	private final Transformer transformer = mock(Transformer.class);
	private final Source input = mock(Source.class);
	private final Result output = mock(Result.class);
	private final ByteArrayOutputStream transformerOut = mock(ByteArrayOutputStream.class);
	private final TransformerInputStream impl = new TransformerInputStream(
			stdin, transformer, streamFactory);

	/**
	 * 
	 */
	@Before
	public void setup() {
		when(streamFactory.newByteArrayOutputStream()).thenReturn(
				transformerOut);
		when(streamFactory.newSource(stdin)).thenReturn(input);
		when(streamFactory.newResult(transformerOut)).thenReturn(output);
		when(transformerOut.toByteArray()).thenReturn(DATA);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyRead() throws Exception {
		assertEquals(0, impl.read());
		verify(transformer).transform(input, output);
		assertEquals(1, impl.read());
		verifyNoMoreInteractions(transformer);
		assertEquals(EOF, impl.read());
		verifyNoMoreInteractions(transformer);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyReadExceptionOccurred() throws Exception {
		final TransformerException expected = new TransformerException(
				ANY_MESSAGE);
		doThrow(expected).when(transformer).transform(input, output);
		try {
			impl.read();
			fail("Exception expected");
		} catch (final IOException e) {
			assertSame(expected, e.getCause());
		}
	}

	/**
	 * 
	 */
	@Test
	public void verifyClose() throws Exception {
		impl.close();
		verify(stdin).close();
	}
}
