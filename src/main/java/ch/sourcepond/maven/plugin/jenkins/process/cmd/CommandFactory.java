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
package ch.sourcepond.maven.plugin.jenkins.process.cmd;

import java.util.List;

import org.zeroturnaround.exec.ProcessExecutor;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * Factory to create the actual CLI command to be executed by a
 * {@link ProcessExecutor}.
 */
public interface CommandFactory {

	/**
	 * Creates the actual CLI command to be executed.
	 * 
	 * @param pConfig
	 *            {@link Config} instance which contains all necessary
	 *            information to build the command, must not be {@code null}
	 * @return List with all command tokens, never {@code null} or empty.
	 */
	List<String> newCommand(Config pConfig);
}
