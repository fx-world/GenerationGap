package de.fxworld.generationgap.ui.actions;

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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import de.fxworld.generationgap.ui.commands.BuilderHelper;

public class AbstractAddNatureAction extends AbstractProjectAction {

	private String natureId  = null;
	private String builderId = null;
	
	public AbstractAddNatureAction(String jobName, String natureId, String builderId) {
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
