package com.zuehlke.pgadmissions.utils;

import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.dto.PersonalDetailsDTO;
import com.zuehlke.pgadmissions.dto.ProgrammeDetails;

public class DTOUtils {

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
