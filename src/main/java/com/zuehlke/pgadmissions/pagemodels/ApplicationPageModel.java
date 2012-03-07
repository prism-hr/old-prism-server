package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Countries;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;

public class ApplicationPageModel extends PageModel {

	private PersonalDetails personalDetails;
	private Address address;
	private Funding funding;
	private String message;
	private ApplicationForm applicationForm;
	private QualificationDTO qualification;
	private List<Countries> countries;
	private EmploymentPosition employmentPosition;
	private List<AddressPurpose> addressPurposes;


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

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;

	}

	public QualificationDTO getQualification() {
		return qualification;
	}
	public void setQualification(QualificationDTO qualificationDto) {
		this.qualification = qualificationDto;
	}

	public void setCountries(List<Countries> countries) {
		this.countries = countries;
	}

	public List<Countries> getCountries() {
		return countries;
	}

	public void setEmploymentPosition(EmploymentPosition employmentPosition) {
		this.employmentPosition = employmentPosition;

	}

	public void setAddressPurposes(List<AddressPurpose> addressPurposes) {
		this.addressPurposes = addressPurposes;
	}

	public EmploymentPosition getEmploymentPosition() {
		return employmentPosition;
	}

	public List<AddressPurpose> getAddressPurposes() {
		return addressPurposes;
	}


}
