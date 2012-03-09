package com.zuehlke.pgadmissions.utils;

import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.dto.PersonalDetails;

public class DTOUtils {

	public static PersonalDetails createPersonalDetails(PersonalDetail detail) {
		PersonalDetails personalDetails = new PersonalDetails();
		if (detail != null) {
			personalDetails.setFirstName(detail.getFirstName());
			personalDetails.setLastName(detail.getLastName());
			personalDetails.setEmail(detail.getEmail());
			personalDetails.setDateOfBirth(detail.getDateOfBirth());
			if (detail.getCountry()!= null) {
				personalDetails.setCountry(detail.getCountry().getName());
			}
			if (detail.getResidenceCountry() != null) {
				personalDetails.setResidenceCountry(detail.getResidenceCountry().getName());
			}
			personalDetails.setResidenceStatus(detail.getResidenceStatus());
			if (detail.getGender()!= null) {
				personalDetails.setGender(detail.getGender().displayValue());
			}
		}
		return personalDetails;
	}
}
