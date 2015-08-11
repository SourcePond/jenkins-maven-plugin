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
package ch.sourcepond.maven.plugin.jenkins.process;

import static ch.sourcepond.maven.plugin.jenkins.process.LogBridge.INFO;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.APPEND;
import static org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.input.NullInputStream;
import org.apache.maven.plugin.logging.Log;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.process.xslt.XsltTransformer;

/**
 *
 */
@Named
@Singleton
final class RedirectStreamFactoryImpl implements RedirectStreamFactory {
	private final XsltTransformer wrapperFactory;

	/**
	 * @param pWrapperFactory
	 */
	@Inject
	RedirectStreamFactoryImpl(final XsltTransformer pWrapperFactory) {
		wrapperFactory = pWrapperFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.RedirectStreamFactory
	 * #newOutputRedirect(org.apache.maven.plugin.logging.Log)
	 */
	@Override
	public OutputStream newLogRedirect(final Log pLog) {
		return new MavenLogOutputStream(pLog, INFO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.RedirectStreamFactory#newStdout
	 * (ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public OutputStream newStdout(final Config pConfig) throws IOException {
		OutputStream out;
		if (pConfig.getStdoutOrNull() != null) {
			if (pConfig.isAppending()) {
				out = newOutputStream(pConfig.getStdoutOrNull(), APPEND);
			} else {
				out = newOutputStream(pConfig.getStdoutOrNull());
			}

			out = wrapperFactory.wrapIfNecessary(pConfig.getStdoutXsltOrNull(),
					out, pConfig.getStdoutParamsOrNull());
		} else {
			out = NULL_OUTPUT_STREAM;
		}
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.StdinFactory#openStdin(ch.
	 * sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public InputStream newStdin(final Config pConfig) throws IOException {
		final Path stdinPath = pConfig.getStdinOrNull();
		final InputStream stdin;
		if (stdinPath != null) {
			stdin = wrapperFactory.wrapIfNecessary(
					pConfig.getStdinXsltOrNull(), newInputStream(stdinPath),
					pConfig.getStdinParamsOrNull());
		} else {
			stdin = new NullInputStream(0);
		}
		return stdin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.RedirectStreamFactory#newListener
	 * (org.apache.maven.plugin.logging.Log)
	 */
	@Override
	public CloseStreamsListener newListener(final Log pLog) {
		return new CloseStreamsListener(pLog);
	}
}
