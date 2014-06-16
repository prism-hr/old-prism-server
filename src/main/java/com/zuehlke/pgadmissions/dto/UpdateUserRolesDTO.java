package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;

public class UpdateUserRolesDTO {
	private User selectedUser;
	private Program selectedProgram;
	private PrismRole[] selectedAuthorities;
	
	public User getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
	public Program getSelectedProgram() {
		return selectedProgram;
	}
	public void setSelectedProgram(Program selectedProgram) {
		this.selectedProgram = selectedProgram;
	}
	public PrismRole[] getSelectedAuthorities() {
		if(selectedAuthorities == null){
			return new PrismRole[]{};
		}
		return selectedAuthorities;
	}
	public void setSelectedAuthorities(PrismRole... selectedAuthorites) {
		this.selectedAuthorities = selectedAuthorites;
	}
}
