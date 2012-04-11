package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;


public class ApplicationFormDetails {

	private Integer numberOfAddresses;
	private Integer numberOfReferees;
	private PersonalDetails personalDetails;
	private ProgrammeDetail programmeDetails;
	private List<Document> supportingDocuments= new ArrayList<Document>();

	
	public Integer getNumberOfAddresses() {
		return numberOfAddresses;
	}
	
	public void setNumberOfAddresses(Integer numberOfAddresses) {
		this.numberOfAddresses = numberOfAddresses;
	}
	
	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}
	
	public void setPersonalDetails(PersonalDetails personalDetails) {
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
	
	public List<Document> getSupportingDocuments() {
		return supportingDocuments;
	}
	
	public void setSupportingDocuments(List<Document> supportingDocuments) {
		this.supportingDocuments = supportingDocuments;
	}
}
