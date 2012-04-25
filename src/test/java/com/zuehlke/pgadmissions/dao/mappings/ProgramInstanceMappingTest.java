package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgramInstanceMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadProgramInstance(){
		Date applicationDeadline = new Date();
		ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(applicationDeadline).studyOption(StudyOption.FULL_TIME).sequence(7).toProgramInstance();
		sessionFactory.getCurrentSession().saveOrUpdate(programInstance);
		assertNotNull(programInstance.getId());
		ProgramInstance reloadedProgramInstance = (ProgramInstance) sessionFactory.getCurrentSession().get(ProgramInstance.class, programInstance.getId());
		assertSame(programInstance, reloadedProgramInstance);
		
		flushAndClearSession();
		reloadedProgramInstance = (ProgramInstance) sessionFactory.getCurrentSession().get(ProgramInstance.class, programInstance.getId());
		assertNotSame(programInstance, reloadedProgramInstance);
		assertEquals(programInstance, reloadedProgramInstance);
		
		assertEquals(DateUtils.truncate(applicationDeadline, Calendar.DATE), reloadedProgramInstance.getApplicationDeadline());
		assertEquals(StudyOption.FULL_TIME, reloadedProgramInstance.getStudyOption());
		assertEquals(7, reloadedProgramInstance.getSequence());
	}
}
