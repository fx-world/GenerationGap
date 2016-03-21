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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

public class GenModelContentProvider extends LabelProvider implements IStructuredContentProvider, ILabelProvider {
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		final List<Object> result  = new ArrayList<Object>();			
		final IProject     project = ((IJavaProject) inputElement).getProject();
		
		try {
			project.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if ((resource != null) && (resource instanceof IFile)) {
						IFile file = (IFile) resource;
						
						if ((file.exists()) && ("genmodel".equals(file.getFileExtension()))) {
							result.add(file);
						}
					}
					
					return true; // visit children too
				}
			});
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return result.toArray();
	}

	@Override
	public String getText(Object element) {
		String result = "";
		
		if ((element != null) && (element instanceof IFile)) {
			IFile file = (IFile) element;
			result = file.getName();
		}
		
		return result;
	}

	
}
