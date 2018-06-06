package de.fxworld.generationgap.core;

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
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fxworld.generationgap.GapWorkflow;
import de.fxworld.generationgap.GapWorkflowFactory;

public class GapWorkflowTest {

	GapWorkflow workflow;
	
	@Before
	public void setUp() throws Exception {
		workflow = GapWorkflowFactory.createGapWorkflow();
	}

	@After
	public void tearDown() throws Exception {
		workflow = null;
	}

	@Test
	public void testBuild() {
//		String       rootFolder            = getRootFolder();
//		List<String> genmodelFiles         = new ArrayList<String>();
//		List<String> srcFolders            = new ArrayList<String>();
//		List<String> srcgenPathsToClear    = new ArrayList<String>();
//		String       customSrcPath         = rootFolder + "src";
//		boolean      generateEdit          = false;
//		boolean      generateEditor        = false;
//		boolean      generateCustomClasses = false;
//		
//		IWorkflowContext context = null;
//		IProgressMonitor monitor = new NullProgressMonitor();
//		
//		genmodelFiles.add(rootFolder + "metamodel/test.genmodel");
//		srcFolders.add(rootFolder + "src");
//		srcgenPathsToClear.add(rootFolder + "src-gen");
//		
//		workflow.build(genmodelFiles, srcFolders, srcgenPathsToClear, customSrcPath, generateEdit, generateEditor, generateCustomClasses, context, monitor);
	}

	@Test
	public void testClean() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGenerateGenFile() {
		//fail("Not yet implemented");
	}

	protected String getRootFolder() {
		String result = "file://";
		File   root   = new File("testdata/simpleGeneration");
		
		result += root.getAbsolutePath() + "/";
		
		System.out.println("Root: " + result);
		
		return result;
	}
}
