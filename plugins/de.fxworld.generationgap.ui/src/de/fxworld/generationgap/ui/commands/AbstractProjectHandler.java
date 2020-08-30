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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractProjectHandler extends AbstractHandler {

	private String jobName = "Job";
	
	public AbstractProjectHandler(String jobName) {
		this.jobName = jobName;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {			
		Object     result    = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		final List<IProject> projects = new ArrayList<IProject>(); 

		if (selection instanceof IProject) {
			projects.add((IProject) selection);
		
		} else if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext(); ) {
				Object element = it.next();
				
				if (element instanceof IProject) {
					projects.add((IProject) element);
				} else if (element instanceof IJavaProject) {
					IJavaProject javaProject = (IJavaProject) element;
					projects.add(javaProject.getProject());
				}
			}
		}
		
		Job job = new Job(jobName) {
			@Override
            protected IStatus run(IProgressMonitor monitor) {
				executeOnProjects(projects, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule(); // start as soon as possible
		
		return result;	
	}

	abstract protected void executeOnProjects(List<IProject> projects,	IProgressMonitor monitor);
}
