package de.fxworld.generationgap.ui.properties;

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
