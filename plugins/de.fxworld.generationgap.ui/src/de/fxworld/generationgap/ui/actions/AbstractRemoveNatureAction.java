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
package de.fxworld.generationgap.ui.actions;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import de.fxworld.generationgap.ui.commands.BuilderHelper;

public class AbstractRemoveNatureAction extends AbstractProjectAction {

	private String natureId  = null;
	private String builderId = null;
	
	public AbstractRemoveNatureAction(String jobName, String natureId, String builderId) {
		super(jobName);
		
		this.natureId  = natureId;
		this.builderId = builderId;
	}

	@Override
	protected void executeOnProjects(List<IProject> projects, IProgressMonitor monitor) {
		monitor.beginTask("Removing Builder", projects.size());
		
		for (IProject project : projects) {
			try {
				IProjectDescription projectDescription = project.getDescription();
				
				if (projectDescription != null) {
					String[] naturIDs     = projectDescription.getNatureIds();					
					
					if (contains(naturIDs, natureId)) {
						String[] newNatureIDs = new String[naturIDs.length - 1];
						int      index        = 0;
						
						for (String aNatureID : naturIDs) {
							if (!aNatureID.equals(natureId)) {
								newNatureIDs[index] = aNatureID;
							}
						}
						
						projectDescription.setNatureIds(newNatureIDs);
						project.setDescription(projectDescription, monitor);
					}
				}
				
				if (builderId != null) {
					BuilderHelper.removeBuilder(project, builderId);
				}
				
				monitor.worked(1);
				
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		
		monitor.done();
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
