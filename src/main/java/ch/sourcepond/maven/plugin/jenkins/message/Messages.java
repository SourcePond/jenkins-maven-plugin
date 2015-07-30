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
package ch.sourcepond.maven.plugin.jenkins.message;

/**
 * Simple facade to get messages from a property file.
 */
public interface Messages {

	/**
	 * Determines the string with the key specified from the
	 * <em>resources.properties</em> file and formats it with the replacements
	 * specified. If no string with the key specified could be found, the key
	 * itself will be returned.
	 * 
	 * @param pKey
	 *            Message key, must not be {@code null}
	 * @param pReplacements
	 *            Replacements to format the message, must not {@code null} but
	 *            can be empty
	 * 
	 * @return Formatted message, or, key if no message could be found
	 */
	String getMessage(String pKey, Object... pReplacements);
}
