package com.zuehlke.pgadmissions.utils;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.dto.PersonalDetails;

public class DTOUtils {

	public static PersonalDetails createPersonalDetails(ApplicationForm applicationForm) {
		PersonalDetails personalDetails = new PersonalDetails();
		if(applicationForm.getApplicant() != null){
			personalDetails.setFirstName(applicationForm.getApplicant().getFirstName());
			personalDetails.setLastName(applicationForm.getApplicant().getLastName());
			personalDetails.setEmail(applicationForm.getApplicant().getEmail());
		}
		return personalDetails;
	}
}
