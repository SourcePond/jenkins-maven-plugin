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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

/**
 *
 */
public class CloseStreamsListenerTest {
	private final Log log = mock(Log.class);
	private final OutputStream stdout = mock(OutputStream.class);
	private final OutputStream stderr = mock(OutputStream.class);
	private final InputStream stdin = mock(InputStream.class);
	private final CloseStreamsListener impl = new CloseStreamsListener(log,
			stdout, stderr, stdin);

	/**
	 * 
	 */
	@Test
	public void afterStop() throws Exception {
		impl.afterStop(null);
		verify(stderr).close();
		verify(stdin).close();
		verify(stdout).close();
		verifyNoMoreInteractions(stderr, stdin, stdout);
	}

	/**
	 * 
	 */
	@Test
	public void afterStopExceptionWhileClosing() throws Exception {
		final IOException expected = new IOException();
		doThrow(expected).when(stderr).close();

		// This should not cause an exception
		impl.afterStop(null);
		verify(log).warn(expected);
	}

}
