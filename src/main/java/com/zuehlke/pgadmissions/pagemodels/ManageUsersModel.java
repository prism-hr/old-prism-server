package com.zuehlke.pgadmissions.pagemodels;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ManageUsersModel extends PageModel{

	private List<Program> programs = new ArrayList<Program>();
	private List<RegisteredUser> usersInRoles = new ArrayList<RegisteredUser>();
	private List<RegisteredUser> availableUsers = new ArrayList<RegisteredUser>();
	private Program selectedProgram;
	private RegisteredUser	 selectedUser;
	private List<Authority> roles = new ArrayList<Authority>();
	
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

	public void setSelectedProgram(Program selectedProgram) {
		this.selectedProgram = selectedProgram;
	}
	
	public Program getSelectedProgram() {
		return selectedProgram;
	}

	public void setRoles(List<Authority> roles) {
		this.roles.addAll(roles);
	}
	
	public List<Authority> getRoles() {
		return roles;
	}

	public RegisteredUser getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(RegisteredUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	public List<RegisteredUser> getAvailableUsers() {
		return availableUsers;
	}

	public void setAvailableUsers(List<RegisteredUser> availableUsers) {
		this.availableUsers = availableUsers;
	}
}
