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
package ch.sourcepond.maven.plugin.jenkins.it;

import static ch.sourcepond.maven.plugin.jenkins.it.utils.Simulator.TARGET;
import static java.nio.file.Files.newInputStream;
import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.io.IOUtils.write;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import ch.sourcepond.maven.plugin.jenkins.it.utils.HttpJenkinsSimulator;
import ch.sourcepond.maven.plugin.jenkins.it.utils.Simulator;

/**
 *
 */
public class StdoutITCase extends ITCase {
	protected static final String ANY_LINE = "anyLine";
	protected final Path stdout = TARGET.resolve(UUID.randomUUID().toString());

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.it.ITCase#newSimulator()
	 */
	@Override
	protected Simulator newSimulator() throws Exception {
		return new HttpJenkinsSimulator();
	}

	/**
	 * @throws IOException
	 */
	@Before
	public void setupStdout() throws IOException {
		mojo.setStdout(stdout.toFile());
		try (final OutputStream out = Files.newOutputStream(stdout)) {
			write(ANY_LINE + "\n", out);
		}
	}

	/**
	 * @param pLines
	 */
	protected void verifyWrittenLines(final Iterator<String> pLines)
			throws IOException {
		try (final InputStream in = newInputStream(stdout)) {
			assertEquals("java -jar jenkins-cli.jar create-job NAME",
					pLines.next());
			assertEquals(
					"Creates a new job by reading stdin as a configuration XML file.",
					pLines.next());
			assertEquals(" NAME : Name of the job to create", pLines.next());
			assertFalse(pLines.hasNext());
		}
	}

	/**
	 * @throws Exception
	 */
	@Override
	@Test
	public void verifyHttpRequest() throws Exception {
		super.verifyHttpRequest();
		try (final InputStream in = Files.newInputStream(stdout)) {
			@SuppressWarnings("unchecked")
			final Iterator<String> lines = readLines(in).iterator();
			verifyWrittenLines(lines);
		}
	}
}
