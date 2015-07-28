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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import org.junit.Before;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;
import ch.sourcepond.maven.plugin.jenkins.message.Messages;

/**
 * @author rolandhauser
 *
 */
public class ConfigBuilderImplBaseTest {
	protected final FileSystem fs = mock(FileSystem.class);
	protected final FileSystemProvider provider = mock(FileSystemProvider.class);
	protected final Path workDirectory = mock(Path.class);
	protected final Messages messages = mock(Messages.class);
	protected final Downloader downloader = mock(Downloader.class);
	protected final ConfigBuilderImpl impl = new ConfigBuilderImpl(messages,
			downloader);

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		when(fs.provider()).thenReturn(provider);
		when(workDirectory.getFileSystem()).thenReturn(fs);
	}
}
