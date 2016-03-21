package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap.core
 * %%
 * Copyright (C) 2016 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
import org.eclipse.emf.mwe.utils.DirectoryCleaner;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GapWorkflow {

	@Inject
	protected Provider<IWorkflowContext> ctxProvider;
	
	public void build(
			List<String> genmodelFiles, // starts with "file://"
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
		
		for (String genmodelFile : genmodelFiles) {
			//if (hasToBuild(configuration, genmodelFile)) {
				
				generateGenFile(context, ecoreGenerator, genmodelFile);		
				
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

	protected void generateGenFile(IWorkflowContext context, EcoreGenerator ecoreGenerator, String genmodelFile) {
		ecoreGenerator.setGenModel(genmodelFile);
		
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
