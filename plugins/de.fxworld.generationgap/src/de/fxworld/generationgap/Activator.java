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
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */
package de.fxworld.generationgap;

import org.eclipse.emf.mwe2.language.Mwe2StandaloneSetup;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.fxworld.generationgap"; //$NON-NLS-1$
	
	public static final String BUILDER_ID = "de.fxworld.generationgap.builder";
	
	public static final String NATURE_ID = "de.fxworld.generationgap.nature";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static GapWorkflow createGapWorkflow() {
		GapWorkflow result   = null;
		Injector    injector = new Mwe2StandaloneSetup().createInjectorAndDoEMFRegistration();
		
		result = injector.getInstance(GapWorkflow.class);		
		
		return result;
	}

}
