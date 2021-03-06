package de.fxworld.generationgap.ui.commands;

/*
 * #%L
 * de.fxworld.generationgap.ui
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
 *    fx-world Softwareentwicklung - initial implementation
 */


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;

import de.fxworld.generationgap.GapConfiguration;
import de.fxworld.generationgap.GapEclipseWorkflow;
import de.fxworld.generationgap.GapEclipseWorkflowFactory;


public class BuildNowCommand extends AbstractJavaProjectHandler implements IHandler {
	
	public BuildNowCommand() {
		super("Building Generation Gap Project");
	}

	@Override
	protected void executeOnJavaProjects(List<IJavaProject> javaProjects, IProgressMonitor monitor) {
	
		for (IJavaProject javaProject : javaProjects) {
			executeOnJavaProject(javaProject, monitor);
		}
	}
	
	protected void executeOnJavaProject(IJavaProject javaProject, IProgressMonitor monitor) {
		GapEclipseWorkflow workflow = GapEclipseWorkflowFactory.createGapWorkflow();
		
		final List<IFile> genmodelFiles = new ArrayList<IFile>(); 
		
		try {
			javaProject.getProject().accept(new IResourceVisitor() {
				
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		workflow.build(new GapConfiguration(javaProject), genmodelFiles, monitor);		
	}


}
