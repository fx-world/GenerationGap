package de.fxworld.testpackage.impl;

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
