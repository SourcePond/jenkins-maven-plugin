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
package ch.sourcepond.maven.plugin.jenkins;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.maven.plugins.annotations.Parameter;
import org.junit.Test;

/**
 *
 */
public class VerifyPropertiesOnParameterAnnotationsTest {
	private static final List<String> EXCLUDED_FIELDS = asList("settings");
	private static final List<Field> INSTANCE_FIELDS;
	private static final CtClass CLI_MOJO_CT_CLASS;

	static {
		final List<Field> instanceFields = new LinkedList<>();
		for (final Field f : CliMojo.class.getDeclaredFields()) {
			if (!isStatic(f.getModifiers()) && !isFinal(f.getModifiers())) {
				instanceFields.add(f);
			}
		}
		INSTANCE_FIELDS = unmodifiableList(instanceFields);

		final ClassPool pool = ClassPool.getDefault();
		try {
			CLI_MOJO_CT_CLASS = pool.getCtClass(CliMojo.class.getName());
		} catch (final NotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * @param pFieldName
	 * @param pPropertyName
	 * @throws Exception
	 */
	private void verifyPropertyInjection(final String pFieldName,
			final String pPropertyName) throws Exception {
		Field propertyField = null;
		for (final Field current : INSTANCE_FIELDS) {
			if (!isExcluded(current)) {
				final Parameter param = getAnnotation(current);
				if (pPropertyName.equals(param.property())) {
					propertyField = current;
					break;
				}
			}
		}
		assertNotNull("No field found with property " + pPropertyName,
				propertyField);
		final Field field = CliMojo.class.getDeclaredField(pFieldName);
		assertEquals(propertyField, field);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void verifyPropertyToFieldMapping() throws Exception {
		verifyPropertyInjection("jenkinscliDirectory", "jenkins.cliDirectory");
		verifyPropertyInjection("baseUrl", "jenkins.baseURL");
		verifyPropertyInjection("cliJar", "jenkins.cliJar");
		verifyPropertyInjection("command", "jenkins.command");
		verifyPropertyInjection("customJenkinsCliJar", "jenkins.customCliJar");
		verifyPropertyInjection("stdin", "jenkins.stdin");
		verifyPropertyInjection("stdout", "jenkins.stdout");
		verifyPropertyInjection("append", "jenkins.append");
		verifyPropertyInjection("proxyId", "jenkins.proxyId");
		verifyPropertyInjection("noKeyAuth", "jenkins.noKeyAuth");
		verifyPropertyInjection("privateKey", "jenkins.privateKey");
		verifyPropertyInjection("noCertificateCheck",
				"jenkins.noCertificateCheck");
		verifyPropertyInjection("trustStore", "jenkins.trustStore");
		verifyPropertyInjection("trustStorePassword",
				"jenkins.trustStorePassword");
	}

	/**
	 * @param pField
	 * @return
	 */
	private boolean isExcluded(final Field pField) {
		for (final String name : EXCLUDED_FIELDS) {
			if (name.equals(pField.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param pField
	 * @return
	 * @throws NotFoundException
	 */
	private Parameter getAnnotation(final Field pField) throws Exception {
		return (Parameter) CLI_MOJO_CT_CLASS.getDeclaredField(pField.getName())
				.getAnnotation(Parameter.class);
	}

	/**
	 * 
	 */
	@Test
	public void verifyNoFieldsWithoutProperty() throws Exception {
		final Set<String> properties = new HashSet<String>();
		for (final Field field : INSTANCE_FIELDS) {
			if (!isExcluded(field)) {
				final Parameter param = getAnnotation(field);
				assertNotNull(field.getName() + " is not annotated with "
						+ Parameter.class, param);
				assertTrue(field.getName() + " has no property!",
						isNotBlank(param.property()));
				assertTrue("Property of " + field.getName() + " is not unique",
						properties.add(param.property()));
			}
		}
	}
}
