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

import java.io.InputStream;
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
 * @author rolandhauser
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

		final OutputStream stdout = rosFactory.newOutputRedirect(pLog);
		executor.redirectOutput(stdout);

		final OutputStream stderr = rosFactory.newErrorRedirect(pLog);
		executor.redirectError(stderr);

		final InputStream stdin = rosFactory.openStdin(pConfig);
		executor.redirectInput(stdin);

		return executor.addListener(new CloseStreamsListener(pLog, stdout,
				stderr, stdin));
	}
}
