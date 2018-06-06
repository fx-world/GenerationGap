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


import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

public class BuilderHelper {

	public static void addBuilder(IProject project, String builderId) throws CoreException {
		IProjectDescription desc        = null;
		ICommand[]          commands    = null;
		ICommand            command     = null;
		ICommand[]          newCommands = null;
		
		if (!hasBuilder(project, builderId)) {
			desc     = project.getDescription();
			commands = desc.getBuildSpec();
			
			// add builder to project
			command  = desc.newCommand();				
			command.setBuilderName(builderId);
			
			newCommands = new ICommand[commands.length + 1];
			System.arraycopy(commands, 0, newCommands, 0, commands.length);
			newCommands[newCommands.length - 1] = command;
			desc.setBuildSpec(newCommands);
			
			project.setDescription(desc, null);
		}
	}
	
	public static void removeBuilder(IProject project, String builderId) throws CoreException {
		IProjectDescription desc        = null;
		ICommand[]          commands    = null;
		ICommand[]          newCommands = null;
		int                 index       = 0;
		
		if (hasBuilder(project, builderId)) {
			desc     = project.getDescription();
			commands = desc.getBuildSpec();
			
			newCommands = new ICommand[commands.length - 1];
			
			for (ICommand command : commands) {
				if (!command.getBuilderName().equals(builderId)) {
					newCommands[index] = command;
					index++;
				}
			}
			
			desc.setBuildSpec(newCommands);			
			project.setDescription(desc, null);
		}
	}
	
	public static boolean hasBuilder(IProject project, String builderId) throws CoreException {
		boolean             result   = false;
		
		if (getBuilderCommand(project, builderId) != null) {
			result = true;
		}
		
		return result;
	}
	
	public static ICommand getBuilderCommand(IProject project, String builderId) throws CoreException {
		ICommand            result   = null;
		IProjectDescription desc     = project.getDescription();
		ICommand[]          commands = desc.getBuildSpec();
		
		for (ICommand command : commands) {
			if (command.getBuilderName().equals(builderId)) {
				result = command;
				break;
			}
		}
		
		return result;
	}
}
