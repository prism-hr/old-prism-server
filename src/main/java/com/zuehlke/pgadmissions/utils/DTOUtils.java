package com.zuehlke.pgadmissions.utils;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;

public class DTOUtils {

	public static Funding createFunding(ApplicationForm applicationForm) {
		Funding funding = new Funding();
		funding.setFunding(applicationForm.getFunding());
		return funding;
	}

	public static Address createAddress(ApplicationForm applicationForm) {
		Address address = new Address();
		if (applicationForm.getApplicant() != null) {
			address.setAddress(applicationForm.getApplicant().getAddress());
		}
		return address;
	}

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
