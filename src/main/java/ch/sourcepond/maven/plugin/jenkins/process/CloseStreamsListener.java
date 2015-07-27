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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.logging.Log;
import org.zeroturnaround.exec.listener.ProcessListener;

/**
 * @author rolandhauser
 *
 */
final class CloseStreamsListener extends ProcessListener {
	private final Log log;
	private final OutputStream stdout;
	private final OutputStream stderr;
	private final InputStream stdin;

	/**
	 * @param pLog
	 * @param pStdout
	 * @param pStderr
	 * @param pStdin
	 */
	CloseStreamsListener(final Log pLog, final OutputStream pStdout,
			final OutputStream pStderr, final InputStream pStdin) {
		log = pLog;
		stdout = pStdout;
		stderr = pStderr;
		stdin = pStdin;
	}

	/**
	 * @param pStream
	 */
	private void closeStream(final Closeable pStream) {
		try {
			pStream.close();
		} catch (final IOException e) {
			log.warn(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zeroturnaround.exec.listener.ProcessListener#afterStop(java.lang.
	 * Process)
	 */
	@Override
	public void afterStop(final Process process) {
		closeStream(stdout);
		closeStream(stderr);
		closeStream(stdin);
	}
}
