package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.dto.PersonalDetails;

public class ApplicationPageModel extends PageModel {
	private PersonalDetails personalDetails;

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}
	
	
}
