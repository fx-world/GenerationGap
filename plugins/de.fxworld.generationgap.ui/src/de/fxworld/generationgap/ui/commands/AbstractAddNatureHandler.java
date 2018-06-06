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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractAddNatureHandler extends AbstractProjectHandler implements IHandler {

	private String natureId  = null;
	private String builderId = null;
	
	public AbstractAddNatureHandler(String jobName, String natureId, String builderId) {
		super(jobName);
		
		this.natureId  = natureId;
		this.builderId = builderId;
	}

	@Override
	protected void executeOnProjects(List<IProject> projects, IProgressMonitor monitor) {		
		monitor.beginTask("Adding Nature", projects.size());
		
		for (IProject project : projects) {
			
			try {
				IProjectDescription projectDescription = project.getDescription();
				
				if (projectDescription != null) {
					String[] naturIDs     = projectDescription.getNatureIds();					
					
					if (!contains(naturIDs, natureId)) {
						String[] newNatureIDs = new String[naturIDs.length + 1];
						System.arraycopy(naturIDs, 0, newNatureIDs, 0, naturIDs.length);
						newNatureIDs[newNatureIDs.length - 1] = natureId;
						
						projectDescription.setNatureIds(newNatureIDs);
						project.setDescription(projectDescription, monitor);
					}
				}
				
				if (builderId != null) {
					BuilderHelper.addBuilder(project.getProject(), builderId);
				}
				
				monitor.worked(1);
				
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	protected boolean contains(String[] ids, String id) {
		boolean result = false;
		
		for (String i : ids) {
			if (id.equals(i)) {
				result = true;
				break;
			}
		}
		
		return result;
	}

}
