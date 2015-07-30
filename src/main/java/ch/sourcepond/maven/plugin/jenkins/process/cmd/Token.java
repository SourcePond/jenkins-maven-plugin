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

import org.apache.maven.plugin.MojoExecutionException;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
abstract class Token implements CommandToken {
	private CommandToken next;

	/**
	 * @param pNext
	 */
	Token(final CommandToken pNext) {
		next = pNext;
	}

	/**
	 * Determines whether this token should be visited.
	 * 
	 * @param pConfig
	 *            {@link Config} instance which contains all necessary
	 *            information, must not be {@code null}.
	 * @return {@code true} if this token should be visited, {@code false}
	 *         otherwise
	 */
	protected boolean isVisitNecessary(final Config pConfig) {
		return true;
	}

	/**
	 * @param pConfig
	 * @param pTokens
	 * @throws MojoExecutionException
	 */
	protected abstract void doVisitToken(List<String> pTokens, Config pConfig);

	/**
	 * @param pTokens
	 * @param pConfig
	 * @throws MojoExecutionException
	 */
	@Override
	public final void visitToken(final List<String> pTokens,
			final Config pConfig) {
		assert pTokens != null : "pTokens is null";
		assert pConfig != null : " pConfig is null";

		if (isVisitNecessary(pConfig)) {
			doVisitToken(pTokens, pConfig);
		}
		if (next != null) {
			next.visitToken(pTokens, pConfig);
		}
	}

	/**
	 * @param pTokens
	 * @param pName
	 * @param pValue
	 */
	protected final void addParameter(final List<String> pTokens,
			final String pName, final Object pValue) {
		assert pName != null : "pName is null";
		assert pValue != null : "pValue is null";
		pTokens.add(pName);
		pTokens.add(pValue.toString());
	}

	/**
	 * @param pTokens
	 * @param pName
	 * @param pValue
	 */
	protected final void addSingleParameter(final List<String> pTokens,
			final String pName, final Object pValue) {
		assert pName != null : "pName is null";
		assert pValue != null : "pValue is null";
		pTokens.add(pName + "=" + pValue.toString());
	}
}
