package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap.core
 * %%
 * Copyright (C) 2016 - 2018 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */


import org.eclipse.emf.mwe2.language.Mwe2StandaloneSetup;

import com.google.inject.Injector;

public class GapWorkflowFactory {

	public static GapWorkflow createGapWorkflow() {
		GapWorkflow result   = null;
		Injector    injector = new Mwe2StandaloneSetup().createInjectorAndDoEMFRegistration();
		
		result = injector.getInstance(GapWorkflow.class);		
		
		return result;
	}

}
