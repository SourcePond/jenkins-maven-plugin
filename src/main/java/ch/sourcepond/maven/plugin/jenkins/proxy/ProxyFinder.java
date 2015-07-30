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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * Facade to determine a concrete {@link Proxy} instance with a specified id.
 */
public interface ProxyFinder {

	/**
	 * Tries to find the appropriate {@link Proxy} instance for the proxy-id
	 * specified. If the proxy-id is {@code null} the search will be cancelled
	 * and {@code null} will be returned. If the proxy-id is not {@code null}
	 * but not {@link Proxy} could be found, a {@link MojoExecutionException}
	 * will be cause to be thrown.
	 * 
	 * @param pProxyIdOrNull
	 *            Proxy-id or {@code null}
	 * @param pSettings
	 *            Settings, must not be {@code null}
	 * @return {@link Proxy} instance or {@code null} if proxy-id specified was
	 *         {@code null}
	 * @throws MojoExecutionException
	 *             Thrown, if proxy-id was not {@code null} but no proxy could
	 *             be found.
	 */
	Proxy findProxy(String pProxyIdOrNull, Settings pSettings)
			throws MojoExecutionException;
}
