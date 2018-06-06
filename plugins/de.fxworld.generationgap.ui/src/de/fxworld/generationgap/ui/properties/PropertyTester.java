package de.fxworld.generationgap.ui.properties;

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


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import de.fxworld.generationgap.Activator;
import de.fxworld.generationgap.ui.commands.BuilderHelper;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {

	/**
	 * 
	 */
	public PropertyTester() {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean result = false;
		
		if ("hasBuilder".equals(property)) {
			if (receiver instanceof IJavaProject) {
				IJavaProject project = (IJavaProject) receiver;
				
				try {
					result = BuilderHelper.hasBuilder(project.getProject(), Activator.BUILDER_ID);
				} catch (CoreException e) {
					// ok then it should not be visible
				}
			}			
		}
		
		return result;
	}

}
