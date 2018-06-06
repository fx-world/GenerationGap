/**
 */
package de.fxworld.testpackage.impl;

/*
 * #%L
 * generationgap-maven-plugin Maven Plugin
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
