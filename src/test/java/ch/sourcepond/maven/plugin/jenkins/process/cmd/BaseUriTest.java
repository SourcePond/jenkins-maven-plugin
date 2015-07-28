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
package ch.sourcepond.maven.plugin.jenkins.process.cmd;

import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class BaseUriTest extends TokenBaseTest<BaseUri> {
	private static final String ANY_BASE_URL = "http://anyurl.org";

	@Override
	@Before
	public void setup() throws Exception {
		when(config.getBaseUri()).thenReturn(new URI(ANY_BASE_URL));
		super.setup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.sourcepond.maven.plugin.jenkins.process.cmd.TokenBaseTest#createToken
	 * ()
	 */
	@Override
	protected BaseUri createToken() {
		return new BaseUri(next);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.sourcepond.maven.plugin.jenkins.process.cmd.TokenBaseTest#
	 * verifyDoVisitToken()
	 */
	@Test
	@Override
	public void verifyDoVisitToken() {
		verifyAddParameter(BaseUri.BASE_URI_SWITCH, ANY_BASE_URL);
	}

}
