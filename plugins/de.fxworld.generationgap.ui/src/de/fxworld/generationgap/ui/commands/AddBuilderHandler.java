package de.fxworld.generationgap.ui.commands;

/*
 * #%L
 * de.fxworld.generationgap.ui
 * %%
 * Copyright (C) 2016 - 2018 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 *
 * Contributors:
 *    fx-world Softwareentwicklung - initial implementation
 */


import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;

import de.fxworld.generationgap.Activator;

public class AddBuilderHandler extends AbstractJavaProjectHandler implements IHandler {

	public AddBuilderHandler() {
		super("Add Builder");
	}

	@Override
	protected void executeOnJavaProjects(List<IJavaProject> javaProjects, IProgressMonitor monitor) {
		monitor.beginTask("Adding Builder", javaProjects.size());
		
		for (IJavaProject javaProject : javaProjects) {
			try {
				BuilderHelper.addBuilder(javaProject.getProject(), Activator.BUILDER_ID);
				monitor.worked(1);
				
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		monitor.done();
	}

}
