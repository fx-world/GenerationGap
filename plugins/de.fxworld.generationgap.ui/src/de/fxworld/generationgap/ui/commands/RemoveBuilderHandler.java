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
 *    fx-world Softwareentwicklung - initial implementation
 */
package de.fxworld.generationgap.ui.commands;

import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;

import de.fxworld.generationgap.Activator;

public class RemoveBuilderHandler extends AbstractJavaProjectHandler implements IHandler {

	public RemoveBuilderHandler() {
		super("Removing Builder");
	}

	
	@Override
	protected void executeOnJavaProjects(List<IJavaProject> javaProjects, IProgressMonitor monitor) {
		
		monitor.beginTask("Removeing Builder", javaProjects.size());
		
		for (IJavaProject javaProject : javaProjects) {
			try {
				BuilderHelper.removeBuilder(javaProject.getProject(), Activator.BUILDER_ID);
				monitor.worked(1);
				
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		monitor.done();		
	}

}
