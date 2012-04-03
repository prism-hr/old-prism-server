package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;


public class ApplicationFormDetails {

	private Integer numberOfAddresses;
	private Integer numberOfContactAddresses;
	private Integer numberOfReferees;
	private PersonalDetail personalDetails;
	private ProgrammeDetail programmeDetails;
	private List<Document> supportingDocuments= new ArrayList<Document>();

	
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
	
	public List<Document> getSupportingDocuments() {
		return supportingDocuments;
	}
	
	public void setSupportingDocuments(List<Document> supportingDocuments) {
		this.supportingDocuments = supportingDocuments;
	}
}
