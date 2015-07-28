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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import org.junit.Before;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
public abstract class TokenBaseTest<T extends Token> {
	protected final CommandToken next = mock(CommandToken.class);
	protected final Config config = mock(Config.class);
	@SuppressWarnings("unchecked")
	protected final List<String> tokens = mock(List.class);
	protected final T impl = createToken();

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		impl.visitToken(tokens, config);
	}

	/**
	 * @return
	 */
	protected abstract T createToken();

	/**
	 * 
	 */
	public abstract void verifyDoVisitToken();

	/**
	 * 
	 */
	protected final void verifyAddParameter(final String pName,
			final String pValue) {
		verify(tokens).add(pName);
		verify(tokens).add(pValue);
		verifyNoMoreInteractions(tokens);
	}

	/**
	 * 
	 */
	protected final void verifyAddSingleParameter(final String pName,
			final String pValue) {
		verify(tokens).add(pName + "=" + pValue);
		verifyNoMoreInteractions(tokens);
	}
}
