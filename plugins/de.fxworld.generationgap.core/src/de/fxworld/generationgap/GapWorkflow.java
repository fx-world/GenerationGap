package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap.core
 * %%
 * Copyright (C) 2020 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 * 
 * Contributors:
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe.utils.DirectoryCleaner;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GapWorkflow {

	@Inject
	protected Provider<IWorkflowContext> ctxProvider;
	
	public void build(
			List<URI>    genmodelURIs, // starts with "file://"
			List<String> srcFolders, // starts with "file://"
			List<String> srcgenPathsToClear,
			String  customSrcPath,
			boolean generateEdit, 
			boolean generateEditor, 
			boolean generateCustomClasses, 
			IWorkflowContext context,
			IProgressMonitor monitor) {
			
		if (context == null) {
			context = ctxProvider.get();
		}
		
		clean(srcgenPathsToClear, context);
		
		// refresh all
//		try {
//			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
//		} catch (CoreException e) {
//			// no need for errors
//		}
		
		
		// here comes the generator
		EcoreGenerator ecoreGenerator = new EcoreGenerator();		
		ecoreGenerator.setMonitor(BasicMonitor.toMonitor(monitor));
		ecoreGenerator.setGenerateEdit(generateEdit);
		ecoreGenerator.setGenerateEditor(generateEditor);
		ecoreGenerator.setGenerateCustomClasses(generateCustomClasses);
//		IFile customFilePath = project.getFile(configuration.getCustomSrcPath());	
//		ecoreGenerator.setCustomSrcPath(customFilePath.getRawLocationURI().toString());
		ecoreGenerator.setCustomSrcPath(customSrcPath);
		
		for (String sourceFolder : srcFolders) {
			ecoreGenerator.addSrcPath(sourceFolder);
		}
		
		for (URI genmodelURI : genmodelURIs) {
			//if (hasToBuild(configuration, genmodelFile)) {
				
				generateGenFile(context, ecoreGenerator, genmodelURI);		
				
			//}
		}
	}

	protected void clean(List<String> srcgenPathsToClear, IWorkflowContext context) {
		// clean src-gen directories
		DirectoryCleaner directoryCleaner = new DirectoryCleaner();
		
		for (String sourceFolder : srcgenPathsToClear) {				
			//directoryCleaner.setDirectory("file://"+sourceFolder);
			directoryCleaner.setDirectory(sourceFolder);
			directoryCleaner.preInvoke();
			directoryCleaner.invoke(context);
			directoryCleaner.postInvoke();			
		}
	}

	protected void generateGenFile(IWorkflowContext context, EcoreGenerator ecoreGenerator, URI genmodelURI) {
		ecoreGenerator.setGenModelURI(genmodelURI);
		
		ecoreGenerator.preInvoke();
		ecoreGenerator.invoke(context);
		ecoreGenerator.postInvoke();
	}

//	protected List<String> getFoldersToClear(GapConfiguration configuration) {
//		List<String> result  = new ArrayList<String>();
//		IProject     project = configuration.getJavaProject().getProject();
//		
//		for (String srcPath : configuration.getSrcPaths()) {
//			IFile file = project.getFile(srcPath);
//			result.add(file.getLocation().toString());
//		}
//		
//		return result;
//	}

//	protected boolean hasToBuild(GapConfiguration configuration, IFile genmodelFile) {
//		boolean  result  = false;
//		IProject project = configuration.getJavaProject().getProject();
//		
//		for (String genModelName : configuration.getGenModels()) {
//			IFile file = project.getFile(genModelName);
//		
//			if (file.equals(genmodelFile)) {
//				result = true;
//			}
//		}
//
//		return result;
//	}
}
