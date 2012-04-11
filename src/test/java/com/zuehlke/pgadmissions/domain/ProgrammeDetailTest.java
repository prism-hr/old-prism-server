package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgrammeDetailTest {

	@Test
	public void shouldCreateProgrammeDetail(){
		ProgrammeDetails programmeDetail = new ProgrammeDetailsBuilder().id(1)
				.applicationForm(new ApplicationForm()).programmeName("programme")
				.referrer(Referrer.OPTION_1).startDate(new Date()).studyOption(StudyOption.FULL_TIME)
				.supervisors(new Supervisor()).projectName("projectName").toProgrammeDetails();
		Assert.assertNotNull(programmeDetail.getProgrammeName());
		Assert.assertNotNull(programmeDetail.getProjectName());
		Assert.assertNotNull(programmeDetail.getApplication());
		Assert.assertNotNull(programmeDetail.getId());
		Assert.assertNotNull(programmeDetail.getReferrer());
		Assert.assertNotNull(programmeDetail.getStartDate());
		Assert.assertNotNull(programmeDetail.getStudyOption());
		Assert.assertNotNull(programmeDetail.getSupervisors());
	}
}
