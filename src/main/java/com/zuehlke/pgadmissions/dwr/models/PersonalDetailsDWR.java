package com.zuehlke.pgadmissions.dwr.models;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;

@DataTransferObject
public class PersonalDetailsDWR implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String lastName;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
