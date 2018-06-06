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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractJavaProjectHandler extends AbstractHandler {

	private String jobName = "Job";
	
	public AbstractJavaProjectHandler(String jobName) {
		this.jobName = jobName;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {			
		Object     result    = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		final List<IJavaProject> javaProjects = new ArrayList<IJavaProject>(); 

		if (selection instanceof IJavaProject) {
			javaProjects.add((IJavaProject) selection);
			//result = executeOnJavaProject((IJavaProject) selection);
		
		} else if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext(); ) {
				Object element = it.next();
				
				if (element instanceof IJavaProject) {
					javaProjects.add((IJavaProject) element);
					//result = executeOnJavaProject((IJavaProject) element);
				}
			}
		}
		
		Job job = new Job(jobName) {
			@Override
            protected IStatus run(IProgressMonitor monitor) {
				executeOnJavaProjects(javaProjects, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule(); // start as soon as possible
		
		return result;	
	}

	abstract protected void executeOnJavaProjects(List<IJavaProject> javaProjects,	IProgressMonitor monitor);
}
