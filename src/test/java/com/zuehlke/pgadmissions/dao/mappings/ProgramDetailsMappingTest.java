package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;

public class ProgramDetailsMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadProgrammeDetails() throws Exception {

	    SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).name("ZZ").code("ZZ").build();
		SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisorBuilder().id(1).firstname("first").lastname("last").email("email").build();

		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().programmeName("test1").projectName("project")
				.startDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).studyOption("1", "Full-time").sourcesOfInterest(interest)
				.suggestedSupervisors(suggestedSupervisor).build();

		sessionFactory.getCurrentSession().save(programmeDetails);
		assertNotNull(programmeDetails.getId());
		Integer id = programmeDetails.getId();
		ProgrammeDetails reloadedDetails = (ProgrammeDetails) sessionFactory.getCurrentSession().get(ProgrammeDetails.class, id);

		assertSame(programmeDetails, reloadedDetails);

		reloadedDetails = (ProgrammeDetails) sessionFactory.getCurrentSession().get(ProgrammeDetails.class, id);

		assertEquals(programmeDetails.getId(), reloadedDetails.getId());
		assertEquals(programmeDetails.getProgrammeName(), reloadedDetails.getProgrammeName());
	}

	@Test
	public void shouldSaveAndLoadProgrammeDetailsWithSuggestedSupervisor() throws Exception {

		SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisorBuilder().firstname("first").lastname("last").email("email").build();
		SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).name("ZZ").code("ZZ").build();
		
		sessionFactory.getCurrentSession().saveOrUpdate(suggestedSupervisor);

		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().programmeName("test2").projectName("project")
				.startDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).studyOption("1", "Full-time").sourcesOfInterest(interest)
				.suggestedSupervisors(suggestedSupervisor).build();

		sessionFactory.getCurrentSession().save(programmeDetails);
		flushAndClearSession();
		assertNotNull(programmeDetails.getId());
		Integer id = programmeDetails.getId();
		ProgrammeDetails reloadedDetails = (ProgrammeDetails) sessionFactory.getCurrentSession().get(ProgrammeDetails.class, id);

		reloadedDetails = (ProgrammeDetails) sessionFactory.getCurrentSession().get(ProgrammeDetails.class, id);
		assertEquals(programmeDetails.getId(), reloadedDetails.getId());
		assertEquals(programmeDetails.getProgrammeName(), reloadedDetails.getProgrammeName());
	}
}
