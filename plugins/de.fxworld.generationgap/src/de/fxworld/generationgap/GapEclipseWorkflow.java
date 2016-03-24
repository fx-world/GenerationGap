package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap
 * %%
 * Copyright (C) 2016 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.fxworld.generationgap.GapEclipseWorkflow.MarkerErrorHandler;

public class GapEclipseWorkflow extends GapWorkflow {

	protected static final String PROJECT = "project";

	public void build(GapConfiguration gapConfiguration, List<IFile> genmodelFiles, IProgressMonitor monitor) {
		//List<String> genmodelFileStrings   = getGenmodelFilesStrings(genmodelFiles); 
		List<URI>    genmodelURIs          = getGenmodelURIs(genmodelFiles);
		List<String> srcgenPathsToClear    = gapConfiguration.getSrcPaths();
		String       customSrcPath         = gapConfiguration.getCustomSrcPath();
		boolean      generateEdit          = gapConfiguration.isGenerateEdit();
		boolean      generateEditor        = gapConfiguration.isGenerateEditor();
		boolean      generateCustomClasses = gapConfiguration.isGenerateCustomClasses();
		
		List<String>     srcFolders        = getSourceFolders(gapConfiguration.getJavaProject().getProject());
		IWorkflowContext context           = ctxProvider.get();
		IProject         project           = gapConfiguration.getJavaProject().getProject(); 
		context.put(PROJECT, project);
		
		deleteMarkers(genmodelFiles);
		
		build(genmodelURIs, srcFolders, srcgenPathsToClear, customSrcPath, generateEdit, generateEditor, generateCustomClasses, context, monitor);
		
		// refresh all again
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			// no need for errors
		}
	}
	
	@Override
	protected void generateGenFile(IWorkflowContext context, EcoreGenerator ecoreGenerator, URI genmodelURI) {
		//IProject           project      = (IProject) context.get(PROJECT);
		//IFile              genmodelFile = project.getRoot().getFile(genmodelFileString);
		IWorkspaceRoot     root         = ResourcesPlugin.getWorkspace().getRoot();				
		IFile              genmodelFile = root.getFile(new Path(genmodelURI.toPlatformString(true)));
		MarkerErrorHandler errorHandler = new MarkerErrorHandler(genmodelFile);
		
		deleteMarkers(genmodelFile);
		
		ecoreGenerator.addErrorHandler(errorHandler);
		
		super.generateGenFile(context, ecoreGenerator, genmodelURI);
		
		ecoreGenerator.removeErrorHandler(errorHandler);
	}

	@Deprecated
	protected List<String> getGenmodelFilesStrings(List<IFile> genmodelFiles) {
		List<String> result = new ArrayList<String>();
		
		for (IFile genmodelFile: genmodelFiles) {
			//result.add("file://"+genmodelFile.getLocation().toString());			
			result.add(genmodelFile.getLocation().toString());
		}
		
		return result;
	}
	
	protected List<URI> getGenmodelURIs(List<IFile> genmodelFiles) {
		List<URI> result = new ArrayList<URI>();
		
		for (IFile genmodelFile: genmodelFiles) {
			String fullPath = genmodelFile.getFullPath().toString();
			URI uri = URI.createPlatformResourceURI(fullPath, true);
			
			result.add(uri);
		}
		
		return result;
	}	
	
	protected void deleteMarkers(List<IFile> genmodelFiles) {
		for (IFile genFile : genmodelFiles) {
			deleteMarkers(genFile);
		}
	}
	
	protected void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ONE);
		} catch (CoreException e) {
			// an error while marking an error, lets throw it
		}
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
	
	class MarkerErrorHandler implements IErrorHandler {

		protected IFile file;
		
		public MarkerErrorHandler(IFile file) {
			this.file = file;
		}
		
		@Override
		public void handleError(IStatus status) {
			try {
				IMarker m = file.createMarker(IMarker.PROBLEM);
				   //m.setAttribute(IMarker.LINE_NUMBER, line);
				   m.setAttribute(IMarker.MESSAGE, status.toString());
				   m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				   m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			} catch (CoreException e) {
				// an error while marking an error, lets throw it
				throw new RuntimeException(e);
			}
		}
		
	}
}
