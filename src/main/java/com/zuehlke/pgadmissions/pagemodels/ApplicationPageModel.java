package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;

public class ApplicationPageModel extends PageModel {
	
	private PersonalDetails personalDetails;
	private Address address;
	private Funding funding;
	private String message;
	private List<Qualification> qualifications;
	private ApplicationForm applicationForm;
	private QualificationDTO qualificationDto;

	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Funding getFunding() {
		return funding;
	}
	
	public void setFunding(Funding funding) {
		this.funding = funding;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public void setQualifications(List<Qualification> qualifications) {
		this.qualifications = qualifications;
	}
	
	public List<Qualification> getQualifications() {
		return qualifications;
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
		
	}

	public QualificationDTO getQualification() {
		return qualificationDto;
	}
	public void setQualificationDto(QualificationDTO qualificationDto) {
		this.qualificationDto = qualificationDto;
	}
}
