/**
 */
package de.fxworld.testpackage.impl;

/*
 * #%L
 * generationgap-maven-plugin Maven Plugin
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


import de.fxworld.testpackage.NamedElement;
import de.fxworld.testpackage.TestpackagePackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

public abstract class NamedElementImplCustom extends NamedElementImpl {
	
	public String getName() {
		String result = super.getName();
		
		if (name == null) {
			name = "";
		}
		
		return result;
	}
} 
