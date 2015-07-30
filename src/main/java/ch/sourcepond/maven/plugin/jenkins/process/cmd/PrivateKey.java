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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.JenkinsCommand.JENKINS_COMMAND_TOKEN;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 *
 */
@Named(PrivateKey.PRIVATE_KEY_SWITCH)
@Singleton
final class PrivateKey extends Token {
	static final String PRIVATE_KEY_SWITCH = "-i";

	/**
	 * @param pNext
	 */
	@Inject
	PrivateKey(@Named(JENKINS_COMMAND_TOKEN) final CommandToken pNext) {
		super(pNext);
	}

	@Override
	protected boolean isVisitNecessary(final Config pConfig) {
		return pConfig.getPrivateKeyOrNull() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.cmd.Token#visitToken(java
	 * .util.List, ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public void doVisitToken(final List<String> pTokens, final Config pConfig) {
		addParameter(pTokens, PRIVATE_KEY_SWITCH, pConfig.getPrivateKeyOrNull());
	}
}
