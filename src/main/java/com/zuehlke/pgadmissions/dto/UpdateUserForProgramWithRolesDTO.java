package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;


public class UpdateUserForProgramWithRolesDTO {
	
	private Program selectedProgram;
	private RegisteredUser selectedUser;
	
	public Program getSelectedProgram() {
		return selectedProgram;
	}
	
	public void setSelectedProgram(Program selectedProgram) {
		this.selectedProgram = selectedProgram;
	}
	
	public RegisteredUser getSelectedUser() {
		return selectedUser;
	}
	
	public void setSelectedUser(RegisteredUser selectedUser) {
		this.selectedUser = selectedUser;
	}
}
