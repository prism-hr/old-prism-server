package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.PersonalDetail;


public class ApplicationFormDetails {

	private Integer numberOfAddresses;
	private Integer numberOfContactAddresses;
	private Integer numberOfReferees;
	private PersonalDetail personalDetails;
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
	
	public PersonalDetail getPersonalDetails() {
		return personalDetails;
	}
	
	public void setPersonalDetails(PersonalDetail personalDetails) {
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
	
	public void setProgrammeDetails(ProgrammeDetail programmeDetails) {
		this.programmeDetails = programmeDetails;
	}
}
