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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.BaseUri.BASE_URI_SWITCH;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * @author rolandhauser
 *
 */
@Named(Jar.JAR_SWITCH)
@Singleton
public class Jar extends Token {
	static final String JAR_SWITCH = "-jar";

	/**
	 * @param pNext
	 */
	@Inject
	Jar(@Named(BASE_URI_SWITCH) final CommandToken pNext) {
		super(pNext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.cmd.Token#visitToken(java
	 * .util.List, ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public void doVisitToken(final List<String> pTokens, final Config pConfig) {
		addParameter(pTokens, JAR_SWITCH, pConfig.getDownloadedCliJar());
	}
}
