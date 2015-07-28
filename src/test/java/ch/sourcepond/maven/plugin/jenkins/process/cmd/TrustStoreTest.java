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

import static ch.sourcepond.maven.plugin.jenkins.process.cmd.TrustStore.TRUST_STORE_PARAMETER;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;

/**
 *
 */
public class TrustStoreTest extends TokenBaseTest<TrustStore> {
	private static final String ANY_TRUST_STORE_PATH = "/anytruststore";
	private final File trustStore = new File(ANY_TRUST_STORE_PATH);

	/**
	 * @throws Exception
	 */
	@Override
	public void setup() throws Exception {
		when(config.isSecure()).thenReturn(true);
		when(config.getTrustStoreOrNull()).thenReturn(trustStore);
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
	protected TrustStore createToken() {
		return new TrustStore(next);
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
		verifyAddSingleParameter(TRUST_STORE_PARAMETER, ANY_TRUST_STORE_PATH);
	}
}
