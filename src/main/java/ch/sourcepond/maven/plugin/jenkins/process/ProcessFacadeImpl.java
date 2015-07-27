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
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.process.cmd.CommandFactory;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
final class ProcessFacadeImpl implements ProcessFacade {
	private final CommandFactory cmdFactory;
	private final ProcessExecutorFactory procExecFactory;

	/**
	 * @param pRedirectOutputStreamFactory
	 */
	@Inject
	public ProcessFacadeImpl(final CommandFactory pCmdFactory,
			final ProcessExecutorFactory pProcExecFactory) {
		cmdFactory = pCmdFactory;
		procExecFactory = pProcExecFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.ProcessFacade#execute(org.
	 * apache.maven.plugin.logging.Log,
	 * ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public void execute(final Log pLog, final Config pConfig)
			throws MojoExecutionException {
		final List<String> command = cmdFactory.newCommand(pConfig);

		try {
			final ProcessExecutor executor = procExecFactory.newExecutor(pLog,
					pConfig, command);
			final int exitValue = executor.execute().getExitValue();

			if (exitValue != 0) {
				final StringBuilder builder = new StringBuilder();
				for (final String token : command) {
					builder.append(token).append(" ");
				}
				builder.append(" [exit value: ").append(exitValue).append("]");
				throw new MojoExecutionException(builder.toString());
			}
		} catch (InvalidExitValueException | IOException | InterruptedException
				| TimeoutException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
