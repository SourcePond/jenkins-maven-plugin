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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.Java.JAVA_EXECUTABLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;

/**
 *
 */
public class JavaTest extends TokenBaseTest<Java> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.cmd.TokenBaseTest#createToken
	 * ()
	 */
	@Override
	protected Java createToken() {
		return new Java(next);
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
		verify(tokens).add(JAVA_EXECUTABLE);
	}

	/**
	 * 
	 */
	@Test
	public void verifyNewCommand() {
		final List<String> commands = impl.newCommand(config);
		assertEquals(1, commands.size());
		assertEquals(JAVA_EXECUTABLE, commands.get(0));
	}
}
