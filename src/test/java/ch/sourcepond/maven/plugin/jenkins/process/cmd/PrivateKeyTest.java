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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.PrivateKey.PRIVATE_KEY_SWITCH;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author rolandhauser
 *
 */
public class PrivateKeyTest extends TokenBaseTest<PrivateKey> {
	private static final String ANY_PRIVATE_KEY = "/any/path/to/privatekey";

	@Override
	public void setup() throws Exception {
		Mockito.when(config.getPrivateKeyOrNull()).thenReturn(ANY_PRIVATE_KEY);
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
	protected PrivateKey createToken() {
		return new PrivateKey(next);
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
		verifyAddParameter(PRIVATE_KEY_SWITCH, ANY_PRIVATE_KEY);
	}

}
