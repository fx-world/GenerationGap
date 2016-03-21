package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap.core
 * %%
 * Copyright (C) 2016 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
