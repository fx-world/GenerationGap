package de.fxworld.generationgap.ui.commands;

/*
 * #%L
 * de.fxworld.generationgap.ui
 * %%
 * Copyright (C) 2016 - 2018 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 *
 * Contributors:
 *    fx-world Softwareentwicklung - initial implementation
 */


import org.eclipse.core.commands.IHandler;

import de.fxworld.generationgap.Activator;

public class AddNatureHandler extends AbstractAddNatureHandler implements IHandler {

	public AddNatureHandler() {
		super("Add Nature", Activator.NATURE_ID, Activator.BUILDER_ID);
	}

	

}
