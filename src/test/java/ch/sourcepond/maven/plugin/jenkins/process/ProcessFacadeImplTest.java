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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import ch.sourcepond.maven.plugin.jenkins.config.Config;
import ch.sourcepond.maven.plugin.jenkins.process.cmd.CommandFactory;

/**
 * @author rolandhauser
 *
 */
public class ProcessFacadeImplTest {
	private static final String ANY_COMMAND = "anyCommand";
	private static final int ERROR_CODE = 221038;
	private final Log log = mock(Log.class);
	private final ProcessExecutor executor = mock(ProcessExecutor.class);
	private final ProcessResult result = mock(ProcessResult.class);
	private final ProcessExecutorFactory procExecFactory = mock(ProcessExecutorFactory.class);
	private final Config config = mock(Config.class);
	private final CommandFactory cmdFactory = mock(CommandFactory.class);
	private final List<String> command = asList(ANY_COMMAND);
	private final ProcessFacadeImpl impl = new ProcessFacadeImpl(cmdFactory,
			procExecFactory);

	/**
	 * @throws URISyntaxException
	 * 
	 */
	@Before
	public void setup() throws Exception {
		when(procExecFactory.newExecutor(log, config, command)).thenReturn(
				executor);
		when(executor.execute()).thenReturn(result);
		when(cmdFactory.newCommand(config)).thenReturn(command);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifySuccessfulExecution() throws Exception {
		when(config.isSecure()).thenReturn(false);
		impl.execute(log, config);
		verify(result).getExitValue();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyExecutionFailedWithException() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(executor).execute();
		try {
			impl.execute(log, config);
			fail("Exception expected");
		} catch (final MojoExecutionException e) {
			assertSame(expected, e.getCause());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyErrorReturnCode() throws Exception {
		when(result.getExitValue()).thenReturn(ERROR_CODE);
		try {
			impl.execute(log, config);
			fail("Exception expected");
		} catch (final MojoExecutionException e) {
			assertTrue(e.getMessage().contains(ANY_COMMAND));
			assertTrue(e.getMessage().contains(String.valueOf(ERROR_CODE)));
		}
	}
}
