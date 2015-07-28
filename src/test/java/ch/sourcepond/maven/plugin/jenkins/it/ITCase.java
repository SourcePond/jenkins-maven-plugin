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
package ch.sourcepond.maven.plugin.jenkins.it;

import static ch.sourcepond.maven.plugin.jenkins.it.utils.Simulator.CLI_JAR_PATH;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.CliMojo;
import ch.sourcepond.maven.plugin.jenkins.it.utils.MojoFactory;
import ch.sourcepond.maven.plugin.jenkins.it.utils.OutputVerificationLog;
import ch.sourcepond.maven.plugin.jenkins.it.utils.Simulator;

/**
 *
 */
public abstract class ITCase {
	protected final CliMojo mojo = new MojoFactory().newMojo();
	protected OutputVerificationLog log = new OutputVerificationLog();
	protected Simulator simulator;

	/**
	 * @return
	 * @throws Exception
	 */
	protected abstract Simulator newSimulator() throws Exception;

	@Before
	public void setup() throws Exception {
		simulator = newSimulator();
		simulator.setup(mojo);
		mojo.setLog(log);
		mojo.setWorkDirectory(new File("target"));
		mojo.setCommand("help create-job");
		mojo.setCliJar(CLI_JAR_PATH);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyHttpRequest() throws Exception {
		mojo.execute();
		log.verifyContent(
				"java -jar jenkins-cli.jar create-job NAME",
				"Creates a new job by reading stdin as a configuration XML file.",
				" NAME : Name of the job to create");
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		simulator.close();
	}
}
