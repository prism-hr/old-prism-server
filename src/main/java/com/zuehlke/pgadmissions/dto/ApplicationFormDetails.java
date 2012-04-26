package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;


public class ApplicationFormDetails {

	private Address currentAddress;
	private Address contactAddress;
	
	private Integer numberOfReferees;
	private PersonalDetails personalDetails;
	private ProgrammeDetails programmeDetails;
	private List<Document> supportingDocuments= new ArrayList<Document>();

	
	
	
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
	
	public ProgrammeDetails getProgrammeDetails() {
		return programmeDetails;
	}
	
	public void setProgrammeDetails(ProgrammeDetails programmeDetails) {
		this.programmeDetails = programmeDetails;
	}
	
	public List<Document> getSupportingDocuments() {
		return supportingDocuments;
	}
	
	public void setSupportingDocuments(List<Document> supportingDocuments) {
		this.supportingDocuments = supportingDocuments;
	}

	public Address getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(Address currentAddress) {
		this.currentAddress = currentAddress;
	}

	public Address getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(Address contactAddress) {
		this.contactAddress = contactAddress;
	}
}
