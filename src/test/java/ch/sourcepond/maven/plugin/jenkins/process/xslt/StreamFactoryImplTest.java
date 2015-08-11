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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

/**
 *
 */
public class StreamFactoryImplTest {
	private final StreamFactoryImpl impl = new StreamFactoryImpl();
	private final Transformer transformer = mock(Transformer.class);
	private final InputStream in = mock(InputStream.class);
	private final OutputStream out = mock(OutputStream.class);

	/**
	 * 
	 */
	@Test
	public void verifyNewResultWithStream() {
		final StreamResult result = (StreamResult) impl.newResult(out);
		assertSame(out, result.getOutputStream());
	}

	/**
	 * 
	 */
	@Test
	public void verifyNewSourceWithStream() {
		final StreamSource source = (StreamSource) impl.newSource(in);
		assertSame(in, source.getInputStream());
	}

	/**
	 * 
	 */
	@Test
	public void verifyNewSourceWithBuffer() throws Exception {
		final byte[] buffer = new byte[] { 10 };
		final StreamSource source = (StreamSource) impl.newSource(buffer);
		final InputStream stream = source.getInputStream();
		assertEquals(buffer[0], stream.read());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyNewSourceWithFile() throws Exception {
		final File file = new File("file:///anyFile");
		final StreamSource source = (StreamSource) impl.newSource(file);
		assertNull(source.getInputStream());
		assertEquals(file.toURI().toASCIIString(), source.getSystemId());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyNewTransformerInputStream() throws Exception {
		// Extensively tested by TransformerInputStreamTest
		final InputStream stream = impl.newTransformerInputStream(in,
				transformer);
		assertNotNull(stream);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyNewTransformerOutputStream() throws Exception {
		// Extensively tested by TransformerOutputStreamTest
		final OutputStream stream = impl.newTransformerOutputStream(out,
				transformer);
		assertNotNull(stream);
	}

	/**
	 * 
	 */
	@Test
	public void verifyNewByteArrayOutputStream() {
		assertNotNull(impl.newByteArrayOutputStream());
	}
}
