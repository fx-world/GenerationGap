/*
 * COPYRIGHT_START
 * 
 * Copyright (C) 2015 Pascal Weyprecht
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * COPYRIGHT_END
 *
 * Contributors:
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */
package de.fxworld.generationgap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.mwe.utils.DirectoryCleaner;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GapWorkflow {

	@Inject
	private Provider<IWorkflowContext> ctxProvider;
	
	public void build(GapConfiguration configuration, List<IFile> genmodelFiles, IProgressMonitor monitor) {
			
		IWorkflowContext context       = ctxProvider.get();
		IProject         project       = configuration.getJavaProject().getProject();
		
		// clean src-gen directories
		DirectoryCleaner directoryCleaner = new DirectoryCleaner();
		
		for (String sourceFolder : getFoldersToClear(configuration)) {
				
			//directoryCleaner.setDirectory("file://"+sourceFolder);
			directoryCleaner.setDirectory(sourceFolder);
			directoryCleaner.preInvoke();
			directoryCleaner.invoke(context);
			directoryCleaner.postInvoke();			
		}
		
		// refresh all
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			// no need for errors
		}
		
		
		// here comes the generator
		EcoreGenerator ecoreGenerator = new EcoreGenerator();		
		ecoreGenerator.setMonitor(BasicMonitor.toMonitor(monitor));
		ecoreGenerator.setGenerateEdit(configuration.isGenerateEdit());
		ecoreGenerator.setGenerateEditor(configuration.isGenerateEditor());
		ecoreGenerator.setGenerateCustomClasses(configuration.isGenerateCustomClasses());
		IFile customFilePath = project.getFile(configuration.getCustomSrcPath());
		customFilePath.getRawLocationURI();		
		ecoreGenerator.setCustomSrcPath(customFilePath.getRawLocationURI().toString());
		
		for (String sourceFolder : getSourceFolders(configuration.getJavaProject().getProject())) {
			ecoreGenerator.addSrcPath("file://"+sourceFolder);
		}
		
		for (IFile genmodelFile : genmodelFiles) {
			if (hasToBuild(configuration, genmodelFile)) {			
				ecoreGenerator.setGenModel("file://"+genmodelFile.getLocation().toString());
				
				ecoreGenerator.preInvoke();
				ecoreGenerator.invoke(context);
				ecoreGenerator.postInvoke();
			}
		}
		
		// refresh all again
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			// no need for errors
		}
	}

	protected List<String> getFoldersToClear(GapConfiguration configuration) {
		List<String> result  = new ArrayList<String>();
		IProject     project = configuration.getJavaProject().getProject();
		
		for (String srcPath : configuration.getSrcPaths()) {
			IFile file = project.getFile(srcPath);
			result.add(file.getLocation().toString());
		}
		
		return result;
	}

	protected boolean hasToBuild(GapConfiguration configuration, IFile genmodelFile) {
		boolean  result  = false;
		IProject project = configuration.getJavaProject().getProject();
		
		for (String genModelName : configuration.getGenModels()) {
			IFile file = project.getFile(genModelName);
		
			if (file.equals(genmodelFile)) {
				result = true;
			}
		}

		return result;
	}
	
	protected List<String> getSourceFolders(IProject project) {
		List<String>     result      = new ArrayList<String>();
		IJavaProject     javaProject = JavaCore.create(project);	
		IWorkspaceRoot   root        = ResourcesPlugin.getWorkspace().getRoot();
		
		try {
			for (IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
					IPath   path     = packageFragmentRoot.getPath();
					IFolder folder   = root.getFolder(path);
					String  location = folder.getLocation().toString();
	
					if (!location.contains("src-gen")) {
						result.add(location);
					}
				}
			}
			
			for (IProject referencedProject : javaProject.getProject().getReferencedProjects()) {
				if (referencedProject.isAccessible() && referencedProject.hasNature(JavaCore.NATURE_ID)) {
					result.addAll(getSourceFolders(referencedProject));
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
}
