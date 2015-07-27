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

import static ch.sourcepond.maven.plugin.jenkins.process.LogBridge.ERROR;
import static ch.sourcepond.maven.plugin.jenkins.process.LogBridge.INFO;
import static java.nio.file.Files.newInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
final class RedirectStreamFactoryImpl implements RedirectStreamFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.RedirectStreamFactory
	 * #newOutputRedirect(org.apache.maven.plugin.logging.Log)
	 */
	@Override
	public OutputStream newOutputRedirect(final Log pLog) {
		return new MavenLogOutputStream(pLog, INFO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.RedirectStreamFactory
	 * #newErrorRedirect(org.apache.maven.plugin.logging.Log)
	 */
	@Override
	public OutputStream newErrorRedirect(final Log pLog) {
		return new MavenLogOutputStream(pLog, ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.StdinFactory#openStdin(ch.
	 * sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public InputStream openStdin(final Config pConfig)
			throws MojoExecutionException {
		final Path stdinPath = pConfig.getStdin();
		final InputStream stdin;
		if (stdinPath != null) {
			try {
				stdin = newInputStream(stdinPath);
			} catch (final IOException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		} else {
			stdin = new ByteArrayInputStream(new byte[0]);
		}
		return stdin;
	}

}
