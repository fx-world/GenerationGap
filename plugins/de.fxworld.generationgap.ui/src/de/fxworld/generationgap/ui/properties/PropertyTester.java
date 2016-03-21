package de.fxworld.generationgap.ui.properties;

/*
 * #%L
 * de.fxworld.generationgap.ui
 * %%
 * Copyright (C) 2016 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
