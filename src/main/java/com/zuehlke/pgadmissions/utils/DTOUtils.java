package com.zuehlke.pgadmissions.utils;

import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.ProgrammeDetails;

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
			if (detail.getResidenceStatus() != null) {
				personalDetails.setResidenceStatus(detail.getResidenceStatus().displayValue());
			}
			if (detail.getGender()!= null) {
				personalDetails.setGender(detail.getGender().displayValue());
			}
		}
		return personalDetails;
	}

	public static ProgrammeDetails createProgrammeDetails(ProgrammeDetail programmeDetailWithApplication) {
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		if (programmeDetailWithApplication != null) {
			programmeDetails.setProgrammeDetailsProgrammeName(programmeDetailWithApplication.getProgrammeName());
			programmeDetails.setProgrammeDetailsProjectName(programmeDetailWithApplication.getProjectName());
			programmeDetails.setProgrammeDetailsReferrer(programmeDetailWithApplication.getReferrer().displayValue());
			programmeDetails.setProgrammeDetailsStartDate(programmeDetailWithApplication.getStartDate());
			programmeDetails.setProgrammeDetailsStudyOption(programmeDetailWithApplication.getStudyOption().displayValue());
		}
		return programmeDetails;
	}
}
