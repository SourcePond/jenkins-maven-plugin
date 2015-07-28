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

import org.apache.maven.plugin.logging.Log;

/**
 *
 */
public interface LogBridge {

	/**
	 * 
	 */
	LogBridge INFO = new LogBridge() {

		@Override
		public void processLine(final Log pLog, final String pLine) {
			pLog.info(pLine);
		}

	};

	/**
	 * 
	 */
	LogBridge ERROR = new LogBridge() {

		@Override
		public void processLine(final Log pLog, final String pLine) {
			pLog.error(pLine);
		}

	};

	/**
	 * @param pLine
	 */
	void processLine(Log pLog, String pLine);
}
