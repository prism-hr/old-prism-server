package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.ProgrammeDetail;


public class ApplicationFormDetails {

	private Integer numberOfAddresses;
	private Integer numberOfContactAddresses;
	private Integer numberOfReferees;
	private PersonalDetailsDTO personalDetails;
	private ProgrammeDetail programmeDetails;
	
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
	
	public ProgrammeDetail getProgrammeDetails() {
		return programmeDetails;
	}
	
	public void setProgrammeDetails(ProgrammeDetail programmeDetail) {
		this.programmeDetails = programmeDetail;
	}
}
