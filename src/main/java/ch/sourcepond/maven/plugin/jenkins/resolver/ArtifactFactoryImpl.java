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

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;

/**
 *
 */
@Named
@Singleton
final class ArtifactFactoryImpl implements ArtifactFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.resolver.ArtifactFactory#newArtifact
	 * (java.lang.String)
	 */
	@Override
	public Artifact newArtifact(final String pCoords)
			throws MojoExecutionException {
		try {
			return new DefaultArtifact(pCoords);
		} catch (final IllegalArgumentException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.resolver.ArtifactFactory#newRequest()
	 */
	@Override
	public ArtifactRequest newRequest() {
		return new ArtifactRequest();
	}
}
