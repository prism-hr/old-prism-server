package com.zuehlke.pgadmissions.dto;

public class ApplicationFormDetails {

	private PersonalDetails personalDetails;
	private Address address;
	private Funding funding;
	
	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}
	
	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setFunding(Funding funding) {
		this.funding = funding;
	}
	
	public Funding getFunding() {
		return funding;
	}
	
}
