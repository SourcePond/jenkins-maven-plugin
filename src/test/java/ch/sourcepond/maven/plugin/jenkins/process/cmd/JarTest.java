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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.Jar.JAR_SWITCH;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 *
 */
public class JarTest extends TokenBaseTest<Jar> {
	private static final String ANY_JAR = "/any.jar";

	@Override
	protected Jar createToken() {
		return new Jar(next);
	}

	@Override
	public void setup() throws Exception {
		when(config.getDownloadedCliJar()).thenReturn(ANY_JAR);
		super.setup();
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
		verifyAddParameter(JAR_SWITCH, ANY_JAR);
	}

}
