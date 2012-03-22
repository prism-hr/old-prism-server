package com.zuehlke.pgadmissions.pagemodels;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ManageUsersModel extends PageModel{

	private List<Program> programs = new ArrayList<Program>();
	private List<RegisteredUser> usersInRoles = new ArrayList<RegisteredUser>();
	
	public List<Program> getPrograms() {
		return programs;
	}
	
	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}
	
	public List<RegisteredUser> getUsersInRoles() {
		return usersInRoles;
	}
	
	public void setUsersInRoles(List<RegisteredUser> usersInRoles) {
		this.usersInRoles = usersInRoles;
	}
}
