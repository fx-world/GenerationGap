package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap
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
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

public class GapProjectBuilder extends IncrementalProjectBuilder {

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {

		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);

		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}

		return new IProject[] {getProject()};
	}
	
	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		final List<IFile> genmodelFiles = new ArrayList<IFile>();
		final List<IFile> customFiles   = new ArrayList<IFile>();
		final List<IFile> ecoreFiles   = new ArrayList<IFile>();
		
		try {
			delta.accept(new IResourceDeltaVisitor() {
				@Override
				public boolean visit(IResourceDelta delta) {
					
					IResource resource = delta.getResource();
					
					if ((resource != null) && (resource instanceof IFile)) {
						IFile file = (IFile) resource;
						
						if ((file.exists()) && ("genmodel".equals(file.getFileExtension()))) {
							genmodelFiles.add(file);
						}
						
						if ((file.exists()) && ("ecore".equals(file.getFileExtension()))) {
							ecoreFiles.add(file);
						}
						
						if ((delta.getKind() == IResourceDelta.ADDED) && (file.exists()) && (file.getName().contains("Custom.java"))) {
							customFiles.add(file);
						}
					}
					
					return true; // visit children too
				}
			});
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		if (customFiles.isEmpty() && ecoreFiles.isEmpty()) {
			buildGenmodelFiles(genmodelFiles, monitor);
		} else {
			fullBuild(monitor);
		}
	}

	private void fullBuild(IProgressMonitor monitor) {
		final List<IFile> genmodelFiles = new ArrayList<IFile>(); 
		
		IProject project = getProject();

		try {
			project.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if ((resource != null) && (resource instanceof IFile)) {
						IFile file = (IFile) resource;
						
						if ((file.exists()) && ("genmodel".equals(file.getFileExtension()))) {
							genmodelFiles.add(file);
						}
					}
					
					return true; // visit children too
				}
			});
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		buildGenmodelFiles(genmodelFiles, monitor);
	}
	
	protected void buildGenmodelFiles(List<IFile> genmodelFiles, IProgressMonitor monitor) {		
		GapEclipseWorkflow gapWorkflow = GapEclipseWorkflowFactory.createGapWorkflow();		
	 
		
		if (!genmodelFiles.isEmpty()) {
			
			IProject project = genmodelFiles.get(0).getProject();
			gapWorkflow.build(new GapConfiguration(JavaCore.create(project)), genmodelFiles, monitor);
		}
	}
}
