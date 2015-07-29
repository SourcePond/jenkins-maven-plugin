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
package ch.sourcepond.maven.plugin.jenkins.proxy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
@Named
@Singleton
final class ProxyFinderImpl implements ProxyFinder {
	static final String CONFIG_VALIDATION_ERROR_NO_PROXY_FOUND = "config.validation.error.noProxyFound";
	private final Messages messages;

	/**
	 * @param pMessages
	 */
	@Inject
	ProxyFinderImpl(final Messages pMessages) {
		messages = pMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.ProxyFinder#findProxy(ch.
	 * sourcepond.maven.plugin.jenkins.config.Config)
	 */
	@Override
	public Proxy findProxy(final String pProxyIdOrNull, final Settings pSettings)
			throws MojoExecutionException {
		assert pSettings != null : "pSettings is null but should not be!";

		Proxy proxy = null;
		if (pProxyIdOrNull != null) {
			for (final Proxy p : pSettings.getProxies()) {
				if (p.getId() == null) {
					continue;
				}

				if (p.getId().equals(pProxyIdOrNull)) {
					proxy = p;
					break;
				}
			}

			if (proxy == null) {
				throw new MojoExecutionException(messages.getMessage(
						CONFIG_VALIDATION_ERROR_NO_PROXY_FOUND, pProxyIdOrNull));
			}
		}
		return proxy;
	}
}
