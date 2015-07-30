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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.exec.ProcessExecutor;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
@Named
@Singleton
final class ProcessExecutorFactoryImpl implements ProcessExecutorFactory {
	private final RedirectStreamFactory rosFactory;

	/**
	 * @param pRosFactory
	 */
	@Inject
	ProcessExecutorFactoryImpl(final RedirectStreamFactory pRosFactory) {
		rosFactory = pRosFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.ProcessExecutorFactory#newExecutor
	 * (org.apache.maven.plugin.logging.Log,
	 * ch.sourcepond.maven.plugin.jenkins.config.Config, java.util.List)
	 */
	@Override
	public ProcessExecutor newExecutor(final Log pLog, final Config pConfig,
			final List<String> pCommand) throws MojoExecutionException {
		final ProcessExecutor executor = new ProcessExecutor(pCommand);
		final CloseStreamsListener listener = rosFactory.newListener(pLog);

		try {
			final OutputStream logOut = rosFactory.newLogRedirect(pLog);

			// stdout/stderr to log
			executor.redirectOutput(logOut);
			executor.redirectError(logOut);

			final OutputStream out = listener.setStdout(rosFactory
					.newStdout(pConfig));

			// Stdout to file (if any)
			executor.redirectOutputAlsoTo(out);
			executor.redirectErrorAlsoTo(out);

			// Stdin
			executor.redirectInput(listener.setStdin(rosFactory
					.newStdin(pConfig)));
		} catch (final IOException e) {
			listener.closeAll();
			throw new MojoExecutionException(e.getMessage(), e);
		}

		return executor.addListener(listener);
	}
}
