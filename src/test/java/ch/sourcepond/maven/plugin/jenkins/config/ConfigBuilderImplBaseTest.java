package ch.sourcepond.maven.plugin.jenkins.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import org.junit.Before;

import ch.sourcepond.maven.plugin.jenkins.config.download.Downloader;

/**
 * @author rolandhauser
 *
 */
public class ConfigBuilderImplBaseTest {
	protected final FileSystem fs = mock(FileSystem.class);
	protected final FileSystemProvider provider = mock(FileSystemProvider.class);
	protected final Path workDirectory = mock(Path.class);
	protected final Downloader downloader = mock(Downloader.class);
	protected final ConfigBuilderImpl impl = new ConfigBuilderImpl(downloader);

	/**
	 * 
	 */
	@Before
	public void setup() throws Exception {
		when(fs.provider()).thenReturn(provider);
		when(workDirectory.getFileSystem()).thenReturn(fs);
	}
}
