package de.fxworld.generationgap.ui.commands;

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

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import de.fxworld.generationgap.GapConfiguration;

@SuppressWarnings("restriction")
public class CreateCustomJavaClassHandler extends AbstractRessourceHandler implements IHandler {

	public CreateCustomJavaClassHandler() {
		super("Create Custom Java Class");
	}

	@Override
	protected void executeOnCompilationUnit(List<ICompilationUnit> compilationUnits, IProgressMonitor monitor) {
		
		for (ICompilationUnit compilationUnit :  compilationUnits) {
			NewClassCreationWizardRunnable runnable        = new NewClassCreationWizardRunnable();
			GapConfiguration               configuration   = new GapConfiguration(compilationUnit.getJavaProject());		
			String                         packageName     = null;
			String                         baseClassName   = null;
			String                         customClassName = null;
			IResource                      srcRootResource = null;
			
			try {
				if (compilationUnit.getPackageDeclarations().length > 0) {
					packageName = compilationUnit.getPackageDeclarations()[0].getElementName();
				}
				
				baseClassName   = compilationUnit.getElementName().replace(".java", "");
				customClassName = baseClassName + "Custom";
				srcRootResource = compilationUnit.getJavaProject().getProject().getFolder(configuration.getCustomSrcPath());
		
				runnable.typeName       = customClassName;
				runnable.superClassName = baseClassName;
				runnable.pack           = (IPackageFragment) compilationUnit.getParent();
				runnable.root           = compilationUnit.getJavaProject().getPackageFragmentRoot(srcRootResource);

				if (packageName != null) {
					runnable.superClassName = packageName + "." + baseClassName;
				} else {
					runnable.superClassName = baseClassName;
				}
				
				Display.getDefault().syncExec(runnable);
				
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
		}							
	}
	
	class NewClassCreationWizardRunnable implements Runnable {

		int                    result          = 0;
		WizardDialog           wizardDialog    = null;
		
		String                 typeName        = null;
		String                 superClassName  = null;
		IPackageFragmentRoot   root            = null;
		IPackageFragment       pack            = null;
		
		@Override
		public void run() {
			NewClassCreationWizard wizard = null;
			NewClassWizardPage     page   = null;
			
			page = new NewClassWizardPage();			
			page.init(null);
			page.setSuperClass(superClassName, true);
			page.setPackageFragmentRoot(root, true);
			page.setPackageFragment(pack, true);
			page.setTypeName(typeName, true);
				
			wizard = new NewClassCreationWizard(page, true);
			wizard.init(PlatformUI.getWorkbench(), null);
			
			wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);		    
		    
		    result = wizardDialog.open();			
		}
		
	}


}
