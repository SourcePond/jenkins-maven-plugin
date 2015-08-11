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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/**
 * 
 */
@Named
@Singleton
final class XsltTransformerImpl implements XsltTransformer {
	private final TransformerFactory transformerFactory;
	private final StreamFactory streamFactory;

	/**
	 * @param pStreamFactory
	 */
	@Inject
	XsltTransformerImpl(final TransformerFactory pTransformerFactory,
			final StreamFactory pStreamFactory) {
		transformerFactory = pTransformerFactory;
		streamFactory = pStreamFactory;
	}

	/**
	 * @param pXsltFile
	 * @param pXmlIn
	 * @param pXmlOut
	 * @param pParamsOrNull
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws Exception
	 */
	private Transformer newTransformer(final File pXsltFile,
			final Map<String, String> pParamsOrNull) throws IOException {
		final Source xslt = streamFactory.newSource(pXsltFile);
		try {
			final Transformer transformer = transformerFactory
					.newTransformer(xslt);

			// If parameters are available transfer them to the transformer.
			if (pParamsOrNull != null) {
				for (final Map.Entry<String, String> entry : pParamsOrNull
						.entrySet()) {
					transformer.setParameter(entry.getKey(), entry.getValue());
				}
			}

			return transformer;
		} catch (final TransformerConfigurationException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.XsltTransformer#wrap(
	 * java.io.File, java.io.OutputStream, java.util.Map)
	 */
	@Override
	public OutputStream wrapIfNecessary(final File pXsltOrNull,
			final OutputStream pStdout,
			final Map<String, String> pStdoutParamsOrNull) throws IOException {
		if (pXsltOrNull != null) {
			return streamFactory.newTransformerOutputStream(pStdout,
					newTransformer(pXsltOrNull, pStdoutParamsOrNull));
		}
		return pStdout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.xslt.XsltTransformer#wrap(
	 * java.io.File, java.io.InputStream, java.util.Map)
	 */
	@Override
	public InputStream wrapIfNecessary(final File pXsltOrNull,
			final InputStream pStdin,
			final Map<String, String> pStdinParamsOrNull) throws IOException {
		if (pXsltOrNull != null) {
			return streamFactory.newTransformerInputStream(pStdin,
					newTransformer(pXsltOrNull, pStdinParamsOrNull));
		}
		return pStdin;
	}
}
