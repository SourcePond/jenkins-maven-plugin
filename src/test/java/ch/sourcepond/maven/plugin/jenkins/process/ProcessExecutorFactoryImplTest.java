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
package ch.sourcepond.maven.plugin.jenkins.process;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import ch.sourcepond.maven.plugin.jenkins.config.Config;

/**
 * @author rolandhauser
 *
 */
public class ProcessExecutorFactoryImplTest {
	private final Log log = mock(Log.class);
	private final Config config = mock(Config.class);
	private final InputStream stdin = mock(InputStream.class);
	private final OutputStream stdout = mock(OutputStream.class);
	private final OutputStream stderr = mock(OutputStream.class);
	private final RedirectStreamFactory rosFactory = mock(RedirectStreamFactory.class);
	private final ProcessExecutorFactoryImpl impl = new ProcessExecutorFactoryImpl(
			rosFactory);

	/**
	 * @throws MojoExecutionException
	 * 
	 */
	@Before
	public void setup() throws MojoExecutionException {
		when(rosFactory.newErrorRedirect(log)).thenReturn(stderr);
		when(rosFactory.newOutputRedirect(log)).thenReturn(stdout);
		when(rosFactory.openStdin(config)).thenReturn(stdin);
	}

	/**
	 * 
	 */
	@Test
	public void verifyStdinProcess() throws Exception {
		final ProcessExecutor exec = impl.newExecutor(log, config,
				asList("java", "-version"));
		final StartedProcess proc = exec.start();
		final ProcessResult res = proc.getFuture().get();
		assertEquals(0, res.getExitValue());
		verify(stdout).close();
		verify(stdout).close();
		verify(stdin).close();
	}
}
