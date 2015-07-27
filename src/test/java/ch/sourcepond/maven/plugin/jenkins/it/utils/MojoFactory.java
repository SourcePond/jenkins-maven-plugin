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
package ch.sourcepond.maven.plugin.jenkins.it.utils;

import static ch.sourcepond.maven.plugin.jenkins.InjectorInstance.INJECTOR;

import java.net.MalformedURLException;

import ch.sourcepond.maven.plugin.jenkins.CliMojo;
import ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilderFactory;
import ch.sourcepond.maven.plugin.jenkins.process.ProcessFacade;
import ch.sourcepond.maven.plugin.jenkins.proxy.ProxyFinder;

/**
 * @author rolandhauser
 *
 */
public final class MojoFactory {
	private final ConfigBuilderFactory configBuilderFactory;
	private final ProcessFacade process;
	private final ProxyFinder proxyFinder;

	/**
	 * 
	 */
	public MojoFactory() {
		configBuilderFactory = getInstance("ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilderFactoryImpl");
		process = getInstance("ch.sourcepond.maven.plugin.jenkins.process.ProcessFacadeImpl");
		proxyFinder = getInstance("ch.sourcepond.maven.plugin.jenkins.proxy.ProxyFinderImpl");
	}

	/**
	 * @param pClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T getInstance(final String pClass) {
		Class<T> cl;
		try {
			cl = (Class<T>) getClass().getClassLoader().loadClass(pClass);
			return INJECTOR.getInstance(cl);
		} catch (final ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * @param pPort
	 * @return
	 * @throws MalformedURLException
	 */
	public CliMojo newMojo() {
		return new CliMojo(configBuilderFactory, process, proxyFinder);
	}
}
