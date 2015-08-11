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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

/**
 *
 */
public class TransformerOutputStreamTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private final Transformer transformer = mock(Transformer.class);
	private final StreamFactory streamFactory = mock(StreamFactory.class);
	private final Source input = mock(Source.class);
	private final Result output = mock(Result.class);
	private final OutputStream stdout = mock(OutputStream.class);
	private final TransformerOutputStream impl = new TransformerOutputStream(
			stdout, transformer, streamFactory);

	/**
	 * 
	 */
	@Before
	public void setup() {
		when(streamFactory.newSource(impl.toByteArray())).thenReturn(input);
		when(streamFactory.newResult(stdout)).thenReturn(output);
	}

	/**
	 * 
	 */
	@Test
	public void verifyClose() throws Exception {
		impl.close();
		final InOrder order = inOrder(transformer, stdout);
		order.verify(transformer).transform(input, output);
		order.verify(stdout).close();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyCloseExceptionOccurred() throws Exception {
		final TransformerException expected = new TransformerException(
				ANY_MESSAGE);
		doThrow(expected).when(transformer).transform(input, output);
		try {
			impl.close();
			fail("Exception expected");
		} catch (final IOException e) {
			assertSame(expected, e.getCause());
		}
		verify(stdout).close();
	}
}
