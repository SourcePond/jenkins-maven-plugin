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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.TrustStorePassword.TRUST_STORE_PASSWORD;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * @author rolandhauser
 *
 */
@Named(TrustStore.TRUST_STORE_PARAMETER)
@Singleton
final class TrustStore extends Token {
	static final String TRUST_STORE_PARAMETER = "-Djavax.net.ssl.trustStore";

	@Inject
	TrustStore(@Named(TRUST_STORE_PASSWORD) final CommandToken pNext) {
		super(pNext);
	}

	/**
	 * @param pConfig
	 * @return
	 */
	@Override
	protected boolean isResponsible(final Config pConfig) {
		return pConfig.isSecure() && pConfig.getTrustStoreOrNull() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.cmd.Token#visitToken(java
	 * .util.List, ch.sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public void doVisitToken(final List<String> pTokens, final Config pConfig) {
		addSingleParameter(pTokens, TRUST_STORE_PARAMETER, pConfig
				.getTrustStoreOrNull().getAbsolutePath());
	}
}
