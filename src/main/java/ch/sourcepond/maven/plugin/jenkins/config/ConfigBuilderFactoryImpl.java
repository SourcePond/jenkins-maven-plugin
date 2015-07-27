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
package ch.sourcepond.maven.plugin.jenkins.config;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * @author rolandhauser
 *
 */
@Named
@Singleton
final class ConfigBuilderFactoryImpl implements ConfigBuilderFactory {
	private final Messages messages;
	private final Downloader dowloader;

	/**
	 * @param pDownloader
	 */
	@Inject
	ConfigBuilderFactoryImpl(final Messages pMessages,
			final Downloader pDownloader) {
		messages = pMessages;
		dowloader = pDownloader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.config.ConfigBuilderFactory#newBuilder
	 * ()
	 */
	@Override
	public ConfigBuilder newBuilder() {
		return new ConfigBuilderImpl(messages, dowloader);
	}
}
