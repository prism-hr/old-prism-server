package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgramDetailsMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadProgrammeDetails() throws Exception {

		Supervisor supervisor = new SupervisorBuilder().id(1).firstname("first").lastname("last").email("email").toSupervisor();

		ProgrammeDetail programmeDetails = new ProgrammeDetailsBuilder().programmeName("test").projectName("project")
				.startDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).studyOption(StudyOption.FULL_TIME).referrer(Referrer.OPTION_1)
				.supervisors(supervisor).toProgrammeDetails();

		sessionFactory.getCurrentSession().save(programmeDetails);
		assertNotNull(programmeDetails.getId());
		Integer id = programmeDetails.getId();
		ProgrammeDetail reloadedDetails = (ProgrammeDetail) sessionFactory.getCurrentSession().get(ProgrammeDetail.class, id);

		assertSame(programmeDetails, reloadedDetails);

		reloadedDetails = (ProgrammeDetail) sessionFactory.getCurrentSession().get(ProgrammeDetail.class, id);

		assertEquals(programmeDetails, reloadedDetails);

		assertEquals(programmeDetails.getApplication(), reloadedDetails.getApplication());

	}

	@Test
	public void shouldSaveAndLoadProgrammeDetailsWithPrimarySupervisor() throws Exception {

		Supervisor supervisor = new SupervisorBuilder().firstname("first").lastname("last").email("email").toSupervisor();
		sessionFactory.getCurrentSession().saveOrUpdate(supervisor);

		ProgrammeDetail programmeDetails = new ProgrammeDetailsBuilder().programmeName("test").projectName("project")
				.startDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).studyOption(StudyOption.FULL_TIME).referrer(Referrer.OPTION_1)
				.primarySupervisor(supervisor).supervisors(supervisor).toProgrammeDetails();

		sessionFactory.getCurrentSession().save(programmeDetails);
		flushAndClearSession();
		assertNotNull(programmeDetails.getId());
		Integer id = programmeDetails.getId();
		ProgrammeDetail reloadedDetails = (ProgrammeDetail) sessionFactory.getCurrentSession().get(ProgrammeDetail.class, id);

		reloadedDetails = (ProgrammeDetail) sessionFactory.getCurrentSession().get(ProgrammeDetail.class, id);

		assertEquals(programmeDetails, reloadedDetails);

		assertEquals(programmeDetails.getApplication(), reloadedDetails.getApplication());
		assertNotNull(reloadedDetails.getPrimarySupervisor());

	}

}
