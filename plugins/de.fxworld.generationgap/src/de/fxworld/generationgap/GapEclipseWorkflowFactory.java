package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap
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
 */


import org.eclipse.emf.mwe2.language.Mwe2StandaloneSetup;

import com.google.inject.Injector;

public class GapEclipseWorkflowFactory {

	public static GapEclipseWorkflow createGapWorkflow() {
		GapEclipseWorkflow result   = null;
		Injector           injector = new Mwe2StandaloneSetup().createInjectorAndDoEMFRegistration();
		
		result = injector.getInstance(GapEclipseWorkflow.class);		
		
		return result;
	}

}
