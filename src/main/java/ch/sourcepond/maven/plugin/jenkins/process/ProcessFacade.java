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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * Facade to create and execute a native process to execute a CLI command.
 */
public interface ProcessFacade {

	/**
	 * Executes the CLI with the command, options and arguments specified by the
	 * {@link Config} parameter.
	 * 
	 * @param pLog
	 *            Log where to redirect <em>stdout</em> and <em>stderr</em>,
	 *            must not be {@code null}
	 * @param pConfig
	 *            {@link Config} instance which holds all necessary
	 *            configuration.
	 * @throws MojoExecutionException
	 *             Thrown, if something went wrong.
	 */
	void execute(Log pLog, Config pConfig) throws MojoExecutionException;
}
