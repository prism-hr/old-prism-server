package com.zuehlke.pgadmissions.dto;


public class ApplicationFormDetails {

	private Integer numberOfAddresses;
	private Integer numberOfContactAddresses;
	
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
}
