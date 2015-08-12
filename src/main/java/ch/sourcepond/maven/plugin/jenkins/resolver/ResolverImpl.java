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

import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
final class ResolverImpl implements Resolver {
	static final String RESOLVER_ERROR_RESOLUTION_FAILED = "resolver.error.resolutionFailed";
	private final Messages messages;
	private final ArtifactFactory factory;
	private Log log;
	private RepositorySystem repoSystem;
	private RepositorySystemSession repoSession;
	private List<RemoteRepository> remoteRepos;

	/**
	 * @param pMessages
	 * @param pFactory
	 */
	ResolverImpl(final Messages pMessages, final ArtifactFactory pFactory) {
		messages = pMessages;
		factory = pFactory;
	}

	/**
	 * @param pLog
	 * @return
	 */
	void setLog(final Log pLog) {
		log = pLog;
	}

	/**
	 * @param pRepoSystem
	 * @return
	 */
	void setRepoSystem(final RepositorySystem pRepoSystem) {
		repoSystem = pRepoSystem;
	}

	/**
	 * @param pRepoSession
	 * @return
	 */
	void setRepoSession(final RepositorySystemSession pRepoSession) {
		repoSession = pRepoSession;
	}

	/**
	 * @param pRemoteRepos
	 */
	void setRemoteRepos(final List<RemoteRepository> pRemoteRepos) {
		remoteRepos = pRemoteRepos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.resolver.Resolver#resolveXslt(org.
	 * eclipse.aether.RepositorySystem,
	 * org.eclipse.aether.RepositorySystemSession, java.util.List,
	 * java.lang.String)
	 */
	@Override
	public File resolveXslt(final String pXsltCoords)
			throws MojoExecutionException {
		final Artifact artifact = factory.newArtifact(pXsltCoords);
		final ArtifactRequest request = factory.newRequest();
		request.setArtifact(artifact);
		request.setRepositories(remoteRepos);

		try {
			final ArtifactResult result = repoSystem.resolveArtifact(
					repoSession, request);
			final Artifact resolvedArtifact = result.getArtifact();
			if (resolvedArtifact == null) {
				for (final Exception e : result.getExceptions()) {
					log.error(e);
				}
				throw new MojoExecutionException(messages.getMessage(
						RESOLVER_ERROR_RESOLUTION_FAILED, pXsltCoords));
			}
			final File xsltFile = resolvedArtifact.getFile();
			notNull(xsltFile,
					"Resolved artifact has no file associated; Please report this as bug (https://github.com/SourcePond/jenkins-maven-plugin/issues)");
			return xsltFile;
		} catch (final ArtifactResolutionException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
