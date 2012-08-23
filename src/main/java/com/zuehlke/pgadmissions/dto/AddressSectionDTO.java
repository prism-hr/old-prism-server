package com.zuehlke.pgadmissions.dto;

import javax.validation.Valid;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.FormSectionObject;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class AddressSectionDTO implements FormSectionObject{

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String currentAddressLocation;
    
    @Valid
    private Country currentAddressCountry;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String contactAddressLocation;
    
    @Valid
    private Country contactAddressCountry;
	
    private boolean sameAddress;
	
    private ApplicationForm application;
	
    private boolean acceptedTerms;
	
	
	public String getCurrentAddressLocation() {
		return currentAddressLocation;
	}

	public void setCurrentAddressLocation(String currentAddressLocation) {
		this.currentAddressLocation = currentAddressLocation;
	}

	public Country getCurrentAddressCountry() {
		return currentAddressCountry;
	}

	public void setCurrentAddressCountry(Country currentAddressCountry) {
		this.currentAddressCountry = currentAddressCountry;
	}

	public String getContactAddressLocation() {
		return contactAddressLocation;
	}

	public void setContactAddressLocation(String contactAddressLocation) {
		this.contactAddressLocation = contactAddressLocation;
	}

	public Country getContactAddressCountry() {
		return contactAddressCountry;
	}

	public void setContactAddressCountry(Country contactAddressCountry) {
		this.contactAddressCountry = contactAddressCountry;
	}

	public boolean isSameAddress() {
		return sameAddress;
	}

	public void setSameAddress(boolean sameAddress) {
		this.sameAddress = sameAddress;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

}
