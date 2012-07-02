package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.FormSectionObject;

public class AddressSectionDTO implements FormSectionObject{

	private String currentAddressLocation;
	private Country currentAddressCountry;
	private String contactAddressLocation;
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
