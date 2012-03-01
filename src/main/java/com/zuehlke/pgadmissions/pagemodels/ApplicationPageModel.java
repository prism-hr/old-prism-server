package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.PersonalDetails;

public class ApplicationPageModel extends PageModel {
	private PersonalDetails personalDetails;
	
	private Address address;

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
}
