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

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
abstract class ResolverBase {
	private final Messages messages;
	private final ArtifactFactory factory;
	private Log log;
	private RepositorySystem repoSystem;
	private RepositorySystemSession repoSession;
	private List<RemoteRepository> remoteRepos;

	/**
	 * @param pFactory
	 */
	ResolverBase(final Messages pMessages, final ArtifactFactory pFactory) {
		messages = pMessages;
		factory = pFactory;
	}

	/**
	 * @return
	 */
	Messages getMessages() {
		return messages;
	}

	/**
	 * @return
	 */
	ArtifactFactory getFactory() {
		return factory;
	}

	/**
	 * @return
	 */
	Log getLog() {
		return log;
	}

	/**
	 * @return
	 */
	RepositorySystem getRepoSystem() {
		return repoSystem;
	}

	/**
	 * @return
	 */
	RepositorySystemSession getRepoSession() {
		return repoSession;
	}

	/**
	 * @return
	 */
	List<RemoteRepository> getRemoteRepos() {
		return remoteRepos;
	}

	/**
	 * @param pLog
	 * @return
	 */
	public void setLog(final Log pLog) {
		log = pLog;
	}

	/**
	 * @param pRepoSystem
	 * @return
	 */
	public void setRepoSystem(final RepositorySystem pRepoSystem) {
		repoSystem = pRepoSystem;
	}

	/**
	 * @param pRepoSession
	 * @return
	 */
	public void setRepoSession(final RepositorySystemSession pRepoSession) {
		repoSession = pRepoSession;
	}

	/**
	 * @param pRemoteRepos
	 */
	public void setRemoteRepos(final List<RemoteRepository> pRemoteRepos) {
		remoteRepos = pRemoteRepos;
	}
}
