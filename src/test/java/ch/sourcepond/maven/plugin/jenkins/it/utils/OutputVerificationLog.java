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

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 *
 */
public final class OutputVerificationLog extends SystemStreamLog {
	private final Set<CharSequence> contents = new LinkedHashSet<CharSequence>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.maven.plugin.logging.SystemStreamLog#error(java.lang.CharSequence
	 * )
	 */
	@Override
	public void info(final CharSequence content) {
		contents.add(content);
	}

	/**
	 * @param pExpectedLines
	 */
	public void verifyContent(final List<String> pExpectedLines) {
		assertEquals(pExpectedLines.size(), contents.size());

		final Iterator<String> expected = pExpectedLines.iterator();
		for (final Iterator<CharSequence> it = contents.iterator(); it
				.hasNext();) {
			assertEquals(expected.next(), it.next());
		}
	}
}
