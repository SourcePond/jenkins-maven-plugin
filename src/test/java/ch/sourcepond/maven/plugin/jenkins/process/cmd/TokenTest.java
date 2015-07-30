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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
public class TokenTest {
	private static final String ANY_KEY = "anyKey";
	private static final String ANY_VALUE = "anyValue";

	/**
	 * @author rolandhauser
	 *
	 */
	private static class StubToken extends Token {
		private boolean responsible;
		private boolean visited;

		/**
		 * @param pNext
		 */
		StubToken(final CommandToken pNext) {
			super(pNext);
		}

		private void setResponsible() {
			responsible = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * ch.sourcepond.maven.plugin.jenkins.process.cmd.Token#isResponsible
		 * (ch.sourcepond.maven.plugin.jenkins.config.Config)
		 */
		@Override
		protected boolean isVisitNecessary(final Config pConfig) {
			return responsible;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * ch.sourcepond.maven.plugin.jenkins.process.cmd.Token#doVisitToken
		 * (java.util.List, ch.sourcepond.maven.plugin.jenkins.config.Config)
		 */
		@Override
		protected void doVisitToken(final List<String> pTokens,
				final Config pConfig) {
			visited = true;
		}

	}

	/**
	 * @author rolandhauser
	 *
	 */
	private static class NoNextToken extends Token {

		/**
		 * 
		 */
		NoNextToken() {
			super(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * ch.sourcepond.maven.plugin.jenkins.process.cmd.Token#doVisitToken
		 * (java.util.List, ch.sourcepond.maven.plugin.jenkins.config.Config)
		 */
		@Override
		protected void doVisitToken(final List<String> pTokens,
				final Config pConfig) {
			// noop
		}

	}

	@SuppressWarnings("unchecked")
	private final List<String> tokens = mock(List.class);
	private final Config config = mock(Config.class);
	private final Token next = mock(Token.class);
	private final StubToken current = new StubToken(next);
	private final NoNextToken noNext = new NoNextToken();

	/**
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void visitTokenTokensIsNull() {
		current.visitToken(null, config);
	}

	/**
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void visitTokenConfigIsNull() {
		current.visitToken(tokens, null);
	}

	/**
	 * 
	 */
	@Test
	public void visitToken() {
		current.setResponsible();
		current.visitToken(tokens, config);
		assertTrue(current.visited);
		verify(next).visitToken(tokens, config);
	}

	/**
	 * 
	 */
	@Test
	public void visitTokenNotResponsible() {
		current.visitToken(tokens, config);
		assertFalse(current.visited);
		verify(next).visitToken(tokens, config);
	}

	/**
	 * 
	 */
	@Test
	public void visitTokenNextTokenNotAvailable() {
		// This should not cause a NullPointerException
		noNext.visitToken(tokens, config);
	}

	/**
	 * 
	 */
	@Test
	public void verifyDefaultValueIsReponsible() {
		assertTrue(noNext.isVisitNecessary(null));
	}

	/**
	 * 
	 */
	@Test
	public void verifyAddParameter() {
		current.addParameter(tokens, ANY_KEY, ANY_VALUE);
		final InOrder order = inOrder(tokens);
		order.verify(tokens).add(ANY_KEY);
		order.verify(tokens).add(ANY_VALUE);
		verifyNoMoreInteractions(tokens);
	}

	/**
	 * 
	 */
	@Test
	public void verifyAddSingleParameter() {
		current.addSingleParameter(tokens, ANY_KEY, ANY_VALUE);
		verify(tokens).add(ANY_KEY + "=" + ANY_VALUE);
		verifyNoMoreInteractions(tokens);
	}

	/**
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void addParameterNameIsNull() {
		current.addParameter(tokens, null, ANY_VALUE);
	}

	/**
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void addParameterValueIsNull() {
		current.addParameter(tokens, ANY_KEY, null);
	}

	/**
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void addSingleParameterNameIsNull() {
		current.addSingleParameter(tokens, null, ANY_VALUE);
	}

	/**
	 * 
	 */
	@Test(expected = AssertionError.class)
	public void addSingleParameterValueIsNull() {
		current.addSingleParameter(tokens, ANY_KEY, null);
	}
}
