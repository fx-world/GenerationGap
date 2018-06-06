package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap
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
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class GapProjectNature implements IProjectNature {

	private IProject project;
	
	@Override
	public void configure() throws CoreException {
		// nothing to do here
	}

	@Override
	public void deconfigure() throws CoreException {
		// nothing to do here
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;

	}
}
