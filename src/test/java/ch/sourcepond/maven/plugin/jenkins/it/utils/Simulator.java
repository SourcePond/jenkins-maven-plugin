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
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

import org.apache.maven.plugin.logging.Log;

import ch.sourcepond.maven.plugin.jenkins.CliMojo;

/**
 *
 */
public abstract class Simulator implements Closeable {
	public static final Path TARGET = getDefault().getPath(USER_DIR, "target");
	public static final String BASE_PATH = "";
	public static final String CLI_JAR_PATH = "/jnlpJars/jenkins-cli.jar";
	public static final String CLI_SITE_PATH = "/cli";
	private final OutputVerificationLog log = new OutputVerificationLog();
	private int port;

	/**
	 * @throws IOException
	 */
	public Simulator() throws IOException {
		try (ServerSocket s = new ServerSocket(0)) {
			port = s.getLocalPort();
		}
	}

	public OutputVerificationLog getLog() {
		return log;
	}

	/**
	 * @return
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @throws Exception
	 */
	public abstract void setup(Log pLog, CliMojo mojo) throws Exception;
}
