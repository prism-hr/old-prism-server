package com.zuehlke.pgadmissions.dto;

public class NewAdminUserDTO {

	private String newUserFirstName;
	private String newUserLastName;
	private String newUserEmail;
	private Integer programForNewUser;
	
	public String getNewUserEmail() {
		return newUserEmail;
	}
	
	public void setNewUserEmail(String newUserEmail) {
		this.newUserEmail = newUserEmail;
	}
	
	public String getNewUserFirstName() {
		return newUserFirstName;
	}
	
	public void setNewUserFirstName(String newUserFirstName) {
		this.newUserFirstName = newUserFirstName;
	}
	
	public String getNewUserLastName() {
		return newUserLastName;
	}
	
	public void setNewUserLastName(String newUserLastName) {
		this.newUserLastName = newUserLastName;
	}
	
	public Integer getProgramForNewUser() {
		return programForNewUser;
	}
	
	public void setProgramForNewUser(Integer programForNewUser) {
		this.programForNewUser = programForNewUser;
	}
}
