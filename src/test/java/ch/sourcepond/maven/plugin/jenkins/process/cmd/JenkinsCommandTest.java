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

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;

/**
 *
 */
public class JenkinsCommandTest extends TokenBaseTest<JenkinsCommand> {
	private static final String HELP = "help";
	private static final String CREATE_JOB = "create-job";
	private static final String COMMAND = HELP + " " + CREATE_JOB;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.cmd.TokenBaseTest#setup()
	 */
	@Override
	public void setup() throws Exception {
		when(config.getCommand()).thenReturn(COMMAND);
		super.setup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.cmd.TokenBaseTest#createToken
	 * ()
	 */
	@Override
	protected JenkinsCommand createToken() {
		return new JenkinsCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.cmd.TokenBaseTest#
	 * verifyDoVisitToken()
	 */
	@Test
	@Override
	public void verifyDoVisitToken() {
		final InOrder order = inOrder(tokens);
		order.verify(tokens).add(HELP);
		order.verify(tokens).add(CREATE_JOB);
	}

}
