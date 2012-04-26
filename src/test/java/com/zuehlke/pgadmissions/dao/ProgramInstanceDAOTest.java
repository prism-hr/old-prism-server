package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgramInstanceDAOTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldReturnProgramInstanceWithDeadlineInTheFuture(){
		Date now = Calendar.getInstance().getTime();
		Date oneYearInFuture = DateUtils.addYears(now, 1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(oneYearInFuture).sequence(1).studyOption(StudyOption.PART_TIME).toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances();
		assertTrue(activeInstances.contains(programInstance));
	}
	@Test
	public void shouldReturnProgramInstanceWithDeadlineToday(){
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(today).sequence(1).studyOption(StudyOption.PART_TIME).toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances();
		assertTrue(activeInstances.contains(programInstance));
	}
	@Test
	public void shouldNotReturnProgramInstanceWithDeadlineInThePast(){
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(oneYearAgo).sequence(1).studyOption(StudyOption.PART_TIME).toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances();
		assertFalse(activeInstances.contains(programInstance));
	}
}
