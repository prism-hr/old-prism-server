package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Role;

public class NewRolesDTO {



	private List<Role> newRoles = new ArrayList<Role>();

	public List<Role> getNewRoles() {
		return newRoles;
	}

	public void setNewRoles(List<Role> roles) {
		this.newRoles.clear();
		for (Role role : roles) {
			if (role != null) {
				this.newRoles.add(role);
			}
		}

	}

}
