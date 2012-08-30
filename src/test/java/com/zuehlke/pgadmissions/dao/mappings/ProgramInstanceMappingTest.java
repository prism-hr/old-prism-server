package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgramInstanceMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadProgramInstance(){
		Program program = new ProgramBuilder().code("xxxxxx").title("hi").toProgram();
		save(program);
		flushAndClearSession();
		Date applicationDeadline = new Date();
		
		
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(applicationDeadline).studyOption(StudyOption.FULL_TIME).sequence(7).applicationStartDate(new Date()).academicYear("2013").toProgramInstance();
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
		assertEquals(program, reloadedProgramInstance.getProgram());
	}
}
