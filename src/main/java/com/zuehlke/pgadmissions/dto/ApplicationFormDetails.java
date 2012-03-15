package com.zuehlke.pgadmissions.dto;


public class ApplicationFormDetails {

	private Integer numberOfAddresses;
	private Integer numberOfContactAddresses;
	private Integer numberOfReferees;
	private PersonalDetailsDTO personalDetails;
	
	public Integer getNumberOfAddresses() {
		return numberOfAddresses;
	}
	
	public void setNumberOfAddresses(Integer numberOfAddresses) {
		this.numberOfAddresses = numberOfAddresses;
	}
	
	public Integer getNumberOfContactAddresses() {
		return numberOfContactAddresses;
	}
	
	public void setNumberOfContactAddresses(Integer numberOfContactAddresses) {
		this.numberOfContactAddresses = numberOfContactAddresses;
	}
	
	public PersonalDetailsDTO getPersonalDetails() {
		return personalDetails;
	}
	
	public void setPersonalDetails(PersonalDetailsDTO personalDetails) {
		this.personalDetails = personalDetails;
	}

	public Integer getNumberOfReferees() {
		return numberOfReferees;
	}
	
	public void setNumberOfReferees(Integer numberOfReferees) {
		this.numberOfReferees = numberOfReferees;
	}
}
