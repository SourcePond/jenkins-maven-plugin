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

import static ch.sourcepond.maven.plugin.jenkins.resolver.ResolverImpl.RESOLVER_ERROR_RESOLUTION_FAILED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
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
import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 *
 */
public class ResolverFactoryImplTest {
	private static final String ANY_MESSAGE = "anyMessage";
	private static final String ANY_COORDS = "anyCoords";
	private static final File RESOLVED_FILE = new File("file:///resolved");
	private final Log log = mock(Log.class);
	private final RepositorySystem repoSystem = mock(RepositorySystem.class);
	private final RepositorySystemSession repoSession = mock(RepositorySystemSession.class);
	private final List<RemoteRepository> remoteRepos = new ArrayList<>();
	private final ArtifactFactory factory = mock(ArtifactFactory.class);
	private final Artifact artifact = mock(Artifact.class);
	private final ArtifactRequest request = new ArtifactRequest();
	private final ArtifactResult result = new ArtifactResult(request);
	private final Messages messages = mock(Messages.class);
	private final ResolverFactoryImpl impl = new ResolverFactoryImpl(messages,
			factory);

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		impl.setLog(log);
		impl.setRemoteRepos(remoteRepos);
		impl.setRepoSession(repoSession);
		impl.setRepoSystem(repoSystem);
		when(factory.newArtifact(ANY_COORDS)).thenReturn(artifact);
		when(factory.newRequest()).thenReturn(request);
		result.setArtifact(artifact);
		when(repoSystem.resolveArtifact(repoSession, request)).thenReturn(
				result);
		when(artifact.getFile()).thenReturn(RESOLVED_FILE);
	}

	/**
	 * 
	 */
	@Test
	public void verifyResolveArtifact() throws Exception {
		final Resolver resolver = impl.newResolver(ANY_COORDS);
		assertSame(RESOLVED_FILE, resolver.resolveXslt());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyResolveArtifactFailed() throws Exception {
		final Exception expected = new Exception();
		result.addException(expected);
		result.setArtifact(null);
		when(messages.getMessage(RESOLVER_ERROR_RESOLUTION_FAILED, ANY_COORDS))
				.thenReturn(ANY_MESSAGE);
		final Resolver resolver = impl.newResolver(ANY_COORDS);
		try {
			resolver.resolveXslt();
			fail("Exception expected");
		} catch (final MojoExecutionException e) {
			assertEquals(ANY_MESSAGE, e.getMessage());
		}
		verify(log).error(expected);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyResolveArtifactFailedWithArtifactResolutionException()
			throws Exception {
		final ArtifactResolutionException expected = new ArtifactResolutionException(
				new ArrayList<ArtifactResult>());
		doThrow(expected).when(repoSystem)
				.resolveArtifact(repoSession, request);
		final Resolver resolver = impl.newResolver(ANY_COORDS);
		try {
			resolver.resolveXslt();
			fail("Exception expected");
		} catch (final MojoExecutionException e) {
			assertSame(expected, e.getCause());
		}
	}
}
