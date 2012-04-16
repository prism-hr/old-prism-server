package com.zuehlke.pgadmissions.dto;


public class UpdateUserForProgramWithRolesDTO {
	
	private Integer selectedProgramId;
	private Integer selectedUserId;
	
	public Integer getSelectedProgramId() {
		return selectedProgramId;
	}
	
	public void setSelectedProgramId(Integer selectedProgramId) {
		this.selectedProgramId = selectedProgramId;
	}
	
	public Integer getSelectedUserId() {
		return selectedUserId;
	}
	
	public void setSelectedUserId(Integer selectedUserId) {
		this.selectedUserId = selectedUserId;
	}
	
}
