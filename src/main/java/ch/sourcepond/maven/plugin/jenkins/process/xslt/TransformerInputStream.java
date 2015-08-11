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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 */
final class TransformerInputStream extends InputStream {
	static final int EOF = -1;
	private final InputStream stdin;
	private final Transformer transformer;
	private final StreamFactory streamFactory;
	private int index;
	private byte[] data;

	/**
	 * @param pStdin
	 */
	TransformerInputStream(final InputStream pStdin,
			final Transformer pTransformer, final StreamFactory pStreamFactory) {
		stdin = pStdin;
		transformer = pTransformer;
		streamFactory = pStreamFactory;
	}

	@Override
	public synchronized int read() throws IOException {
		if (data == null) {
			try {
				final ByteArrayOutputStream out = streamFactory
						.newByteArrayOutputStream();
				transformer.transform(streamFactory.newSource(stdin),
						streamFactory.newResult(out));
				data = out.toByteArray();
			} catch (final TransformerException e) {
				throw new IOException(e.getMessage(), e);
			}
		}

		final int rc;
		if (data.length > index) {
			rc = data[index++];
		} else {
			rc = EOF;
		}

		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		stdin.close();
	}
}
