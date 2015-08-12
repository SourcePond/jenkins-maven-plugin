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

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
final class ResolverImpl extends ResolverBase implements Resolver {
	static final String RESOLVER_ERROR_RESOLUTION_FAILED = "resolver.error.resolutionFailed";
	private String xsltCoords;

	/**
	 * @param pMessages
	 * @param pFactory
	 */
	ResolverImpl(final Messages pMessages, final ArtifactFactory pFactory) {
		super(pMessages, pFactory);
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
	public File resolveXslt() throws MojoExecutionException {
		final Artifact artifact = getFactory().newArtifact(xsltCoords);
		final ArtifactRequest request = getFactory().newRequest();
		request.setArtifact(artifact);
		request.setRepositories(getRemoteRepos());

		try {
			final ArtifactResult result = getRepoSystem().resolveArtifact(
					getRepoSession(), request);
			final Artifact resolvedArtifact = result.getArtifact();
			if (resolvedArtifact == null) {
				for (final Exception e : result.getExceptions()) {
					getLog().error(e);
				}
				throw new MojoExecutionException(getMessages().getMessage(
						RESOLVER_ERROR_RESOLUTION_FAILED, xsltCoords));
			}
			final File xsltFile = resolvedArtifact.getFile();
			notNull(xsltFile,
					"Resolved artifact has no file associated; Please report this as bug (https://github.com/SourcePond/jenkins-maven-plugin/issues)");
			return xsltFile;
		} catch (final ArtifactResolutionException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	/**
	 * @param pXsltCoords
	 */
	public void setXsltCoords(final String pXsltCoords) {
		xsltCoords = pXsltCoords;
	}

}
