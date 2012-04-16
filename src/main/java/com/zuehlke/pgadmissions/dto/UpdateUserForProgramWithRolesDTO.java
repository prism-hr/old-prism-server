package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class UpdateUserForProgramWithRolesDTO {
	
	private Program setProgram;
	private RegisteredUser setUser;
	private NewRolesDTO newSetRolesDTO;
	
	public Program getSetProgram() {
		return setProgram;
	}
	
	public void setSetProgram(Program setProgram) {
		this.setProgram = setProgram;
	}
	
	public RegisteredUser getSetUser() {
		return setUser;
	}
	
	public void setSetUser(RegisteredUser setUser) {
		this.setUser = setUser;
	}
	
	public NewRolesDTO getNewSetRolesDTO() {
		return newSetRolesDTO;
	}
	
	public void setNewSetRolesDTO(NewRolesDTO newSetRolesDTO) {
		this.newSetRolesDTO = newSetRolesDTO;
	}
}
