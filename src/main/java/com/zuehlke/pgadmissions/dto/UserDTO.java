package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Role;

public class UserDTO {
	
	private Integer userId;
	
	private List<Role> roles = new ArrayList<Role>();

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {		
		this.roles.clear();
		for (Role role : roles) {
			if(role!=null){
				this.roles.add(role);
			}
		}
		
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
}
