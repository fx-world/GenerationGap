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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

public class SourceFolderContentProvider extends LabelProvider implements IStructuredContentProvider, ILabelProvider {
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Object>  result      = new ArrayList<Object>();	
		IJavaProject javaProject = (IJavaProject) inputElement;
		
		try {
			for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
				if ((root.getKind() == IPackageFragmentRoot.K_SOURCE) && (root.getJavaProject() == javaProject)) {
					result.add(root);
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result.toArray();
	}

	@Override
	public String getText(Object element) {
		String result = "";
		
		if ((element != null) && (element instanceof IPackageFragmentRoot)) {
			IPackageFragmentRoot root = (IPackageFragmentRoot) element;
			result = root.getElementName();
		}
		
		return result;
	}

	
}
