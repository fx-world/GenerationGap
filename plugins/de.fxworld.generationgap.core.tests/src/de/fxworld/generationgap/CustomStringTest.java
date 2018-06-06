package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap.core.tests
 * %%
 * Copyright (C) 2016 - 2018 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */


import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fxworld.generationgap.EcoreGenerator;
import de.fxworld.generationgap.GapWorkflow;
import de.fxworld.generationgap.GapWorkflowFactory;

public class CustomStringTest {

	EcoreGenerator fixture;
	
	@Before
	public void setUp() throws Exception {
		fixture = new EcoreGenerator();
	}

	@After
	public void tearDown() throws Exception {
		fixture = null;
	}

	@Test
	public void testNormalizeClassname() {
		assertEquals("de.fxworld.testpackage.Group",
				fixture.getNormalizedClassName("de.fxworld.testpackage.impl.GroupImpl"));
		assertEquals("de.fxworld.testpackage.Group",
				fixture.getNormalizedClassName("de.fxworld.testpackage.impl.Group"));
		assertEquals("de.fxworld.testpackage.Group",
				fixture.getNormalizedClassName("de.fxworld.testpackage.GroupImpl"));
		assertEquals("de.fxworld.testpackage.Group",
				fixture.getNormalizedClassName("de.fxworld.testpackage.Group"));
	}
	
	@Test
	public void testTrimURI() {
		URI uri = URI.createURI("file:///C:/Users/fx/git/GenerationGap/jars/generationgap-maven-plugin/src/test/resources/unit/simpleGeneration/src/de/fxworld/testpackage/impl/");
		assertEquals("", uri.segment(uri.segmentCount() - 1).trim());
		URI newURI = fixture.trimURI(uri);		
		assertNotNull(newURI.segment(newURI.segmentCount() - 1));
		assertNotEquals("", newURI.segment(newURI.segmentCount() - 1).trim());
	}
	
}
