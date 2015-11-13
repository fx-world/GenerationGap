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
package de.fxworld.generationgap.ui.commands;

import org.eclipse.core.commands.IHandler;

import de.fxworld.generationgap.Activator;

public class AddNatureHandler extends AbstractAddNatureHandler implements IHandler {

	public AddNatureHandler() {
		super("Add Nature", Activator.NATURE_ID, Activator.BUILDER_ID);
	}

	

}
