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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.PropertyPage;

import de.fxworld.generationgap.GapConfiguration;

public class GapPropertyPage extends PropertyPage {
	private Button generateEditButton;
	private Button generateEditorButton;
	private Button generateCustomClassesButton;
	private CheckboxTableViewer sourceFolderCleanTableViewer;
	private CheckboxTableViewer generatorModelTableViewer;

	private GapConfiguration configuration = null;
	private ComboViewer customClassTargetComboViewer;
	
	/**
	 * Create the property page.
	 */
	public GapPropertyPage() {
	}

	/**
	 * Create contents of the property page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);		
		
		if (configuration != null) {
			container.setLayout(new GridLayout(2, false));
			
			Label lblGenerate = new Label(container, SWT.NONE);
			lblGenerate.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
			lblGenerate.setText("Generate:");
			
			generateEditButton = new Button(container, SWT.CHECK);
			generateEditButton.setText("Generate Edit");
			new Label(container, SWT.NONE);
			
			generateEditorButton = new Button(container, SWT.CHECK);
			generateEditorButton.setText("GenerateEditor");
			new Label(container, SWT.NONE);
			
			generateCustomClassesButton = new Button(container, SWT.CHECK);
			generateCustomClassesButton.setText("Generate Custom Classes");
			
			Label lblNewLabel = new Label(container, SWT.NONE);
			lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
			lblNewLabel.setText("Target for CustomClasses:");
			
			customClassTargetComboViewer = new ComboViewer(container, SWT.READ_ONLY);
			Combo customClassTargetCombo = customClassTargetComboViewer.getCombo();
			customClassTargetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			customClassTargetComboViewer.setLabelProvider(new SourceFolderContentProvider());
			customClassTargetComboViewer.setContentProvider(new SourceFolderContentProvider());
			
			Label lblNewLabel_1 = new Label(container, SWT.NONE);
			lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
			lblNewLabel_1.setText("Source folders to clear:");
			
			sourceFolderCleanTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
			Table sourceFolderCleanTable = sourceFolderCleanTableViewer.getTable();
			sourceFolderCleanTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			sourceFolderCleanTableViewer.setLabelProvider(new SourceFolderContentProvider());
			sourceFolderCleanTableViewer.setContentProvider(new SourceFolderContentProvider());
			
			Label lblNewLabel_2 = new Label(container, SWT.NONE);
			lblNewLabel_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
			lblNewLabel_2.setText("Generator Models:");
			
			generatorModelTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
			Table generatorModelTable = generatorModelTableViewer.getTable();
			generatorModelTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			generatorModelTableViewer.setLabelProvider(new GenModelContentProvider());
			generatorModelTableViewer.setContentProvider(new GenModelContentProvider());
	
			initData();
		} else {
			container.setLayout(new GridLayout(1, false));
			
			Label label = new Label(container, SWT.NONE);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
			label.setText("The selected Project is not a Java Project.");
		}
		
		return container;
	}
	
	protected void initData() {
		IProject project = configuration.getJavaProject().getProject();
		
		customClassTargetComboViewer.setInput(configuration.getJavaProject());
		sourceFolderCleanTableViewer.setInput(configuration.getJavaProject());
		generatorModelTableViewer.setInput(configuration.getJavaProject());
		
		generateEditButton.setSelection(configuration.isGenerateEdit());
		generateEditorButton.setSelection(configuration.isGenerateEditor());
		generateCustomClassesButton.setSelection(configuration.isGenerateCustomClasses());
		
		try {
			IFile                customClassFolderFile = project.getFile(configuration.getCustomSrcPath());
			IPackageFragmentRoot root                  = configuration.getJavaProject().findPackageFragmentRoot(customClassFolderFile.getFullPath());
			if (root != null) {
				customClassTargetComboViewer.setSelection(new StructuredSelection(root));
			}
		
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		for (String clearSrcPath : configuration.getSrcPaths()) {
			try {		
				IFile                clearSrcFile = project.getFile(clearSrcPath);
				IPackageFragmentRoot root         = configuration.getJavaProject().findPackageFragmentRoot(clearSrcFile.getFullPath());
				sourceFolderCleanTableViewer.setChecked(root, true);				
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		for (String genmodelPath : configuration.getGenModels()) {
			IFile genmodelFile = project.getFile(genmodelPath);
			generatorModelTableViewer.setChecked(genmodelFile, true);
		}
		
	}
	
	protected String[] getSourceFolders(GapConfiguration configuration) { 
		List<String> result  = new ArrayList<String>();
		IJavaProject project = configuration.getJavaProject();
		
		try {
			for (IPackageFragmentRoot root : project.getAllPackageFragmentRoots()) {
				if ((root.getKind() == IPackageFragmentRoot.K_SOURCE) && (root.getJavaProject() == project)) {
					result.add(root.getElementName());
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
		return result.toArray(new String[result.size()]);
	}

	@Override
	protected void performDefaults() {
		if (configuration != null) {
			configuration.resetDefault();
		}
		
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		if (configuration != null) {
			//sourceFolderCleanTableViewer.getCheckedElements()
			configuration.setGenerateEdit(generateEditButton.getSelection());
			configuration.setGenerateEditor(generateEditorButton.getSelection());
			configuration.setGenerateCustomClasses(generateCustomClassesButton.getSelection());
						
			Object customClassTargetObject = ((IStructuredSelection) customClassTargetComboViewer.getSelection()).getFirstElement();
			if ((customClassTargetObject != null) && (customClassTargetObject instanceof IPackageFragmentRoot)) {
				IPackageFragmentRoot customClassRoot = (IPackageFragmentRoot) customClassTargetObject;
				configuration.setCustomSrcPath(customClassRoot.getResource().getProjectRelativePath().toString());
			}
			
			List<String> sourceFolders = new ArrayList<String>();
			for (Object object : sourceFolderCleanTableViewer.getCheckedElements()) {
				IPackageFragmentRoot root = (IPackageFragmentRoot) object;
				sourceFolders.add(root.getResource().getProjectRelativePath().toString());
			}
			configuration.setSrcPaths(sourceFolders);
			
			List<String> generatorModels = new ArrayList<String>();
			for (Object object : generatorModelTableViewer.getCheckedElements()) {
				IFile file = (IFile) object;
				generatorModels.add(file.getProjectRelativePath().toString());
				
				//IFile newFile = configuration.getJavaProject().getProject().getFile(file.getProjectRelativePath().toString());			
			}
			configuration.setGenModels(generatorModels);
			
			configuration.save();
		}
		
		return super.performOk();
	}
	
	@Override
	public void setElement(IAdaptable element) {
		if (element instanceof IJavaProject) {
			configuration = new GapConfiguration((IJavaProject) element);
			
		} else if (element instanceof IProject) {
			try {
				IProject project = (IProject) element;
				if (project.hasNature(JavaCore.NATURE_ID)) {
					configuration = new GapConfiguration(JavaCore.create(project));
				}
			} catch (CoreException e) {
			}
		}
		
		super.setElement(element);
	}

}
