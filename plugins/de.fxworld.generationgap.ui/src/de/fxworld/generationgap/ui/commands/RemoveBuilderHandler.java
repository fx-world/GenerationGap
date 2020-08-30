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
