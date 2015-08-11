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
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 */
final class TransformerOutputStream extends ByteArrayOutputStream {
	private final Transformer transformer;
	private final StreamFactory streamFactory;
	private final OutputStream stdout;

	/**
	 * @param pDelegate
	 */
	TransformerOutputStream(final OutputStream pStdout,
			final Transformer pTransformer, final StreamFactory pStreamFactory) {
		stdout = pStdout;
		transformer = pTransformer;
		streamFactory = pStreamFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.io.output.ByteArrayOutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			transformer.transform(streamFactory.newSource(toByteArray()),
					streamFactory.newResult(stdout));
		} catch (final TransformerException e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			stdout.close();
		}
	}
}
