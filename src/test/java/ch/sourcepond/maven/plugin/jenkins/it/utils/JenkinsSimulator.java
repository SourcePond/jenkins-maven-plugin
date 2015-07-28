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

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.Files.createDirectories;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.apache.commons.lang3.Validate.isTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.maven.settings.Settings;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;

import ch.sourcepond.maven.plugin.jenkins.CliMojo;

/**
 *
 */
public abstract class JenkinsSimulator extends Simulator {
	private static final String LOCALHOST = "localhost";
	private final Path target = getDefault().getPath(USER_DIR, "target");
	private final Path war = target.resolve("jenkins.war");
	private final Path jenkinsHome = target.resolve(randomUUID().toString());
	private StartedProcess proc;

	/**
	 * @throws IOException
	 */
	public JenkinsSimulator() throws IOException {
		super();
	}

	/**
	 * @return
	 */
	protected abstract String getProcotol();

	/**
	 * @return
	 */
	protected abstract void specializeProcessCommand(
			List<String> pProcessCommand);

	/**
	 * @return
	 */
	protected Path getJenkinsHome() {
		return jenkinsHome;
	}

	protected Path getWar() {
		return war;
	}

	/**
	 * Starts the embedded Jetty server.
	 */
	@Override
	public void setup(final CliMojo mojo) throws Exception {
		isTrue(Files.isRegularFile(war),
				war
						+ " does not exist! Run this project with Maven to downlad jenkins.war.");
		createDirectories(jenkinsHome);

		final List<String> processCommand = new LinkedList<>();
		processCommand.add("java");
		processCommand.add("-DJENKINS_HOME=" + getJenkinsHome());
		processCommand.add("-Dhudson.diyChunking=false");
		processCommand.add("-jar");
		processCommand.add(getWar().toString());
		specializeProcessCommand(processCommand);

		proc = new ProcessExecutor(processCommand).start();
		final ServerStartupBarrier barrier = createBarrier(new URI(
				getProcotol() + "://" + LOCALHOST + ":" + getPort()));
		barrier.waitForServerStart();

		// Setup mojo
		mojo.setSettings(new Settings());
		mojo.setBaseUrl(new URL(getProcotol(), LOCALHOST, getPort(), BASE_PATH));
	}

	/**
	 * @return
	 */
	protected abstract ServerStartupBarrier createBarrier(URI pUri);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			if (proc != null) {
				proc.getProcess().destroy();
				proc.getFuture().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
