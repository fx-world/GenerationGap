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
package de.fxworld.generationgap.ui.properties;

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
