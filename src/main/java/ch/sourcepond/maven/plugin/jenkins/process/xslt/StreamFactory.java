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
package ch.sourcepond.maven.plugin.jenkins.process.xslt;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 */
interface StreamFactory {

	/**
	 * @return
	 */
	ByteArrayOutputStream newByteArrayOutputStream();

	/**
	 * @param pStdin
	 * @param pTransformer
	 * @param pStreamFactory
	 * @return
	 */
	InputStream newTransformerInputStream(InputStream pStdin,
			Transformer pTransformer);

	/**
	 * @param pStdout
	 * @param pTransformer
	 * @param pStreamFactory
	 * @return
	 */
	OutputStream newTransformerOutputStream(OutputStream pStdout,
			Transformer pTransformer);

	/**
	 * @param pOut
	 * @return
	 */
	Result newResult(OutputStream pOut);

	/**
	 * @param pIn
	 * @return
	 */
	Source newSource(InputStream pIn);

	/**
	 * @param pBuffer
	 * @return
	 */
	Source newSource(byte[] pBuffer);

	/**
	 * @param pFile
	 * @return
	 */
	Source newSource(File pFile);
}
