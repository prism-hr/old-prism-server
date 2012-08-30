package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgramInstanceDAOTest extends AutomaticRollbackTestCase {


	@Test
	public void shouldNotReturnProgramInstanceForOtherProgram(){
		Program progOne = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		Program progTwo = new ProgramBuilder().code("bbbb").title("hello").toProgram();
		save(progOne, progTwo);
		Date now = Calendar.getInstance().getTime();
		Date oneYearInFuture = DateUtils.addYears(now, 1);
		
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(progOne).applicationDeadline(oneYearInFuture).sequence(1).studyOption(StudyOption.PART_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(progTwo).applicationDeadline(oneYearInFuture).sequence(1).studyOption(StudyOption.PART_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstanceOne, programInstanceTwo);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(progOne);
		assertTrue(activeInstances.contains(programInstanceOne));
		assertFalse(activeInstances.contains(programInstanceTwo));
	}
	
	@Test
	public void shouldReturnProgramInstanceWithDeadlineInTheFuture(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearInFuture = DateUtils.addYears(now, 1);
		
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearInFuture).sequence(1).studyOption(StudyOption.PART_TIME).applicationStartDate(now).applicationStartDate(now).academicYear("2013").toProgramInstance();
		
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
		assertTrue(activeInstances.contains(programInstance));
	}
	@Test
	public void shouldReturnProgramInstanceWithDeadlineToday(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(today).program(program).sequence(1).studyOption(StudyOption.PART_TIME).applicationStartDate(now).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
		assertTrue(activeInstances.contains(programInstance));
	}
	@Test
	public void shouldNotReturnProgramInstanceWithDeadlineInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).sequence(1).studyOption(StudyOption.PART_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
		assertFalse(activeInstances.contains(programInstance));
	}
	
	@Test
	public void shouldNotReturnProgramInstanceWithStudyOptionAndDeadlineNotInThePastForOtherProgram(){
		Program progOne = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		Program progTwo = new ProgramBuilder().code("bbbb").title("hello").toProgram();
		save(progOne, progTwo);
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(progOne).applicationDeadline(today).sequence(1).studyOption(StudyOption.FULL_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(progTwo).applicationDeadline(today).sequence(1).studyOption(StudyOption.FULL_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstanceOne, programInstanceTwo);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(progOne, StudyOption.FULL_TIME);
		assertTrue(matchedInstances.contains(programInstanceOne));
		assertFalse(matchedInstances.contains(programInstanceTwo));

	}
	
	@Test
	public void shouldReturnProgramInstanceWithStudyOptionAndDeadlineNotInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(today).sequence(1).studyOption(StudyOption.FULL_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, StudyOption.FULL_TIME);
		assertTrue(matchedInstances.contains(programInstance));
	}
	
	@Test
	public void shouldNotReturnProgramInstanceWithStudyOptionAndDeadlineInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).sequence(1).studyOption(StudyOption.FULL_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, StudyOption.FULL_TIME);
		assertFalse(matchedInstances.contains(programInstance));
	}
	@Test
	public void shouldNotReturnProgramInstanceWithoutStudyOptionAndDeadlineNotInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).sequence(1).studyOption(StudyOption.FULL_TIME_DISTANCE_LEARNING).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, StudyOption.FULL_TIME);
		assertFalse(matchedInstances.contains(programInstance));
	}
	
	
	@Test
	public void shouldFindProgramInstanceForToday(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").toProgram();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date eightMonthsAgo = DateUtils.addMonths(now, -8);
		Date fourMonthsFromNow = DateUtils.addMonths(now, 4);
		Date oneYearAndfourMonthsFromNow = DateUtils.addMonths(now, 16);
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(program).applicationDeadline(eightMonthsAgo).sequence(1).studyOption(StudyOption.FULL_TIME_DISTANCE_LEARNING).applicationStartDate(now).academicYear("2013").toProgramInstance();
		
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow).sequence(2).studyOption(StudyOption.FULL_TIME_DISTANCE_LEARNING).applicationStartDate(now).academicYear("2013").toProgramInstance();
		ProgramInstance programInstanceThree = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAndfourMonthsFromNow).sequence(3).studyOption(StudyOption.FULL_TIME_DISTANCE_LEARNING).applicationStartDate(now).academicYear("2013").toProgramInstance();
		ProgramInstance programInstanceFour = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow).sequence(4).studyOption(StudyOption.PART_TIME).applicationStartDate(now).academicYear("2013").toProgramInstance();
		save(programInstanceOne,  programInstanceThree,programInstanceFour, programInstanceTwo);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		ProgramInstance programInstance = dao.getCurrentProgramInstanceForStudyOption(program, StudyOption.FULL_TIME_DISTANCE_LEARNING);
		assertEquals(programInstanceTwo, programInstance);
	}
}
