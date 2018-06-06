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


import de.fxworld.testpackage.User;

public class GroupImplCustom extends GroupImpl {

	@Override
	public boolean isMember(User user) {
		boolean result = false;

		if (user != null) {
			for (User member : getMembers()) {
				if (user.equals(member)) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}

}
