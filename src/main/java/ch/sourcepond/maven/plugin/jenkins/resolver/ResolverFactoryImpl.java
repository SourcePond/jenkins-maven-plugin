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
package ch.sourcepond.maven.plugin.jenkins.resolver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
@Named
@Singleton
final class ResolverFactoryImpl extends ResolverBase implements ResolverFactory {

	/**
	 * @param pMessages
	 * @param pFactory
	 */
	@Inject
	ResolverFactoryImpl(final Messages pMessages, final ArtifactFactory pFactory) {
		super(pMessages, pFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.resolver.ResolverFactory#newResolver
	 * (java.lang.String)
	 */
	@Override
	public Resolver newResolver(final String pXsltCoords) {
		final ResolverImpl resolver = new ResolverImpl(getMessages(),
				getFactory());
		resolver.setXsltCoords(pXsltCoords);
		resolver.setLog(getLog());
		resolver.setRemoteRepos(getRemoteRepos());
		resolver.setRepoSession(getRepoSession());
		resolver.setRepoSystem(getRepoSystem());
		return resolver;
	}

}
