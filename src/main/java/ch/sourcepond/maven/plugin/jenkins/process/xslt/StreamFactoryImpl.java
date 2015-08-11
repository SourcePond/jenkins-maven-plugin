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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 */
@Named
@Singleton
final class StreamFactoryImpl implements StreamFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#newResult
	 * (java.io.OutputStream)
	 */
	@Override
	public Result newResult(final OutputStream pOut) {
		return new StreamResult(pOut);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#newSource
	 * (java.io.InputStream)
	 */
	@Override
	public Source newSource(final InputStream pIn) {
		return new StreamSource(pIn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#newSource
	 * (byte[])
	 */
	@Override
	public Source newSource(final byte[] pBuffer) {
		return newSource(new ByteArrayInputStream(pBuffer));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#newSource
	 * (java.io.File)
	 */
	@Override
	public Source newSource(final File pFile) {
		return new StreamSource(pFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#
	 * newTransformerInputStream(java.io.InputStream,
	 * javax.xml.transform.Transformer,
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory)
	 */
	@Override
	public InputStream newTransformerInputStream(final InputStream pStdin,
			final Transformer pTransformer) {
		return new TransformerInputStream(pStdin, pTransformer, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#
	 * newTransformerOutputStream(java.io.OutputStream,
	 * javax.xml.transform.Transformer,
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory)
	 */
	@Override
	public OutputStream newTransformerOutputStream(final OutputStream pStdout,
			final Transformer pTransformer) {
		return new TransformerOutputStream(pStdout, pTransformer, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.xslt.StreamFactory#
	 * newByteArrayOutputStream()
	 */
	@Override
	public ByteArrayOutputStream newByteArrayOutputStream() {
		return new ByteArrayOutputStream();
	}
}
