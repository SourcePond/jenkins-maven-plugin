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

/**
 *
 */
public interface ResolverFactory {

	void setLog(Log pLog);

	void setRepoSystem(RepositorySystem pRepoSystem);

	void setRepoSession(RepositorySystemSession pRepoSession);

	void setRemoteRepos(List<RemoteRepository> pRemoteRepos);

	Resolver newResolver(String pXsltCoords);
}
