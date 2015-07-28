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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.NoKeyAuth.NO_KEY_AUTH_SWITCH;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 *
 */
public class NoKeyAuthTest extends TokenBaseTest<NoKeyAuth> {

	@Override
	public void setup() throws Exception {
		when(config.isNoKeyAuth()).thenReturn(true);
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
	protected NoKeyAuth createToken() {
		return new NoKeyAuth(next);
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
		verify(tokens).add(NO_KEY_AUTH_SWITCH);
		verifyNoMoreInteractions(tokens);
	}

}
