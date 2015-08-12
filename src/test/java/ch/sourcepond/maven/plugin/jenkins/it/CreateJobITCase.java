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
import static java.nio.file.FileSystems.getDefault;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import ch.sourcepond.maven.plugin.jenkins.it.utils.HttpJenkinsSimulator;
import ch.sourcepond.maven.plugin.jenkins.it.utils.Simulator;

/**
 *
 */
public class CreateJobITCase extends ITCase {
	private static final Path RESOURCES = getDefault().getPath(USER_DIR, "src",
			"test", "resources");
	private static final String JOB_NAME = "testJob";
	private static final String DESCRIPTION_KEY = "description";
	private static final String GITURL_KEY = "giturl";
	private static final String CREDENTIALS_ID_KEY = "credentialsId";
	private static final String GROUP_ID_KEY = "groupId";
	private static final String ARTIFACT_ID_KEY = "artifactId";
	private static final String DESCRIPTION_VALUE = "descriptionValue";
	private static final String GITURL_VALUE = "giturlValue";
	private static final String CREDENTIALS_ID_VALUE = "credentialsIdValue";
	private static final String GROUP_ID_VALUE = "groupIdValue";
	private static final String ARTIFACT_ID_VALUE = "artifactIdValue";
	private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();
	private final XPath xpath = XPathFactory.newInstance().newXPath();

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
	@Override
	@Before
	public void setup() throws Exception {
		super.setup();
		mojo.setStdin(RESOURCES.resolve("maven-job.xml").toFile());
		mojo.setStdinXslt(RESOURCES.resolve("maven-job-template.xslt").toFile());

		final Map<String, String> params = new HashMap<>();
		params.put(DESCRIPTION_KEY, DESCRIPTION_VALUE);
		params.put(GITURL_KEY, GITURL_VALUE);
		params.put(CREDENTIALS_ID_KEY, CREDENTIALS_ID_VALUE);
		params.put(GROUP_ID_KEY, GROUP_ID_VALUE);
		params.put(ARTIFACT_ID_KEY, ARTIFACT_ID_VALUE);
		mojo.setStdinXsltParams(params);
		mojo.setCommand("create-job " + JOB_NAME);
	}

	/**
	 * @throws Exception
	 */
	@Override
	@Test
	public void verifyHttpRequest() throws Exception {
		mojo.execute();

		final Path stdout = TARGET.resolve(UUID.randomUUID().toString());
		mojo.setStdout(stdout.toFile());
		mojo.setStdin(null);
		mojo.setStdinXslt(null);
		mojo.setStdoutXsltParams(null);
		mojo.setCommand("get-job " + JOB_NAME);
		mojo.execute();

		final Document job = documentBuilderFactory.newDocumentBuilder().parse(
				stdout.toFile());
		assertEquals(DESCRIPTION_VALUE,
				xpath.evaluate("/maven2-moduleset/description/text()", job));
		assertEquals(
				GITURL_VALUE,
				xpath.evaluate(
						"/maven2-moduleset/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/url/text()",
						job));
		assertEquals(
				CREDENTIALS_ID_VALUE,
				xpath.evaluate(
						"/maven2-moduleset/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/credentialsId/text()",
						job));
		assertEquals(GROUP_ID_VALUE, xpath.evaluate(
				"/maven2-moduleset/rootModule/groupId/text()", job));
		assertEquals(ARTIFACT_ID_VALUE, xpath.evaluate(
				"/maven2-moduleset/rootModule/artifactId/text()", job));
	}
}