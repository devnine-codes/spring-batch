/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.configuration.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;

/**
 * @author Dave Syer
 * 
 */
public class ClassPathXmlApplicationContextFactoryTests {

	private ClassPathXmlApplicationContextFactory factory = new ClassPathXmlApplicationContextFactory();

	@Test
	public void testCreateJob() {
		factory.setPath(new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "trivial-context.xml")));
		assertNotNull(factory.createApplicationContext());
	}

	@Test
	public void testGetJobName() {
		factory.setPath(new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "trivial-context.xml")));
		assertEquals("test-job", factory.createApplicationContext().getBeanNamesForType(Job.class)[0]);
	}

	@Test
	public void testParentConfigurationInherited() {
		factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
		factory.setPath(new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml")));
		ConfigurableApplicationContext context = factory.createApplicationContext();
		assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
		assertEquals("bar", ((Job) context.getBean("test-job", Job.class)).getName());
		assertEquals(4, ((Foo) context.getBean("foo", Foo.class)).values[1], 0.01);
	}
	
	@Test
	public void testBeanFactoryPostProcessorsNotCopied() {
		factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
		factory.setPath(new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml")));
		factory.setCopyBeanFactoryPostProcessors(false);
		ConfigurableApplicationContext context = factory.createApplicationContext();
		assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
		assertEquals("${foo}", ((Job) context.getBean("test-job", Job.class)).getName());
		assertEquals(4, ((Foo) context.getBean("foo", Foo.class)).values[1], 0.01);
	}
	
	@Test
	public void testBeanFactoryConfigurationNotCopied() {
		factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
		factory.setPath(new ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml")));
		factory.setCopyConfiguration(false);
		ConfigurableApplicationContext context = factory.createApplicationContext();
		assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
		assertEquals("bar", ((Job) context.getBean("test-job", Job.class)).getName());
		// The CustomEditorConfigurer is a BeanFactoryPostProcessor so the editor gets copied anyway!
		assertEquals(4, ((Foo) context.getBean("foo", Foo.class)).values[1], 0.01);
	}
	
	public static class Foo {
		private double[] values;
		public void setValues(double[] values) {
			this.values = values;
		}
	}

}
