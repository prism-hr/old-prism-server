package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class UpdateUserRolesDTO {
	private RegisteredUser selectedUser;
	private Program selectedProgram;
	private Authority[] selectedAuthorities;
	
	public RegisteredUser getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(RegisteredUser selectedUser) {
		this.selectedUser = selectedUser;
	}
	public Program getSelectedProgram() {
		return selectedProgram;
	}
	public void setSelectedProgram(Program selectedProgram) {
		this.selectedProgram = selectedProgram;
	}
	public Authority[] getSelectedAuthorities() {
		if(selectedAuthorities == null){
			return new Authority[]{};
		}
		return selectedAuthorities;
	}
	public void setSelectedAuthorities(Authority... selectedAuthorites) {
		this.selectedAuthorities = selectedAuthorites;
	}
}
