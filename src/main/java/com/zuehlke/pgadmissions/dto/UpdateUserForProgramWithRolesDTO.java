package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;


public class UpdateUserForProgramWithRolesDTO {
	
	private Program selectedProgram;
	private User selectedUser;
	
	public Program getSelectedProgram() {
		return selectedProgram;
	}
	
	public void setSelectedProgram(Program selectedProgram) {
		this.selectedProgram = selectedProgram;
	}
	
	public User getSelectedUser() {
		return selectedUser;
	}
	
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
}
