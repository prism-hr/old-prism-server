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

public class ProgramInstanceDAOTest extends AutomaticRollbackTestCase {


	@Test
	public void shouldNotReturnProgramInstanceForOtherProgram(){
		Program progOne = new ProgramBuilder().code("aaaaa").title("hi").build();
		Program progTwo = new ProgramBuilder().code("bbbb").title("hello").build();
		save(progOne, progTwo);
		Date now = Calendar.getInstance().getTime();
		Date oneYearInFuture = DateUtils.addYears(now, 1);
		
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(progOne).applicationDeadline(oneYearInFuture).sequence(1).studyOption("31", "Part-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(progTwo).applicationDeadline(oneYearInFuture).sequence(1).studyOption("31", "Part-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstanceOne, programInstanceTwo);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(progOne);
		assertTrue(listContainsId(programInstanceOne, activeInstances));
		assertFalse(listContainsId(programInstanceTwo, activeInstances));
	}
	
	@Test
	public void shouldReturnProgramInstanceWithDeadlineInTheFuture(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearInFuture = DateUtils.addYears(now, 1);
		
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearInFuture).sequence(1).studyOption("31", "Part-time").applicationStartDate(now).applicationStartDate(now).academicYear("2013").enabled(true).build();
		
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
		assertTrue(listContainsId(programInstance, activeInstances));
	}
	@Test
	public void shouldReturnProgramInstanceWithDeadlineToday(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstance = new ProgramInstanceBuilder().applicationDeadline(today).program(program).sequence(1).studyOption("31", "Part-time").applicationStartDate(now).applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
		assertTrue(listContainsId(programInstance, activeInstances));
	}
	@Test
	public void shouldNotReturnProgramInstanceWithDeadlineInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).sequence(1).studyOption("31", "Part-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> activeInstances = dao.getActiveProgramInstances(program);
		assertFalse(activeInstances.contains(programInstance));
	}
	
	@Test
	public void shouldNotReturnProgramInstanceWithStudyOptionAndDeadlineNotInThePastForOtherProgram(){
		Program progOne = new ProgramBuilder().code("aaaaa").title("hi").build();
		Program progTwo = new ProgramBuilder().code("bbbb").title("hello").build();
		save(progOne, progTwo);
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(progOne).applicationDeadline(today).sequence(1).studyOption("1", "Full-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(progTwo).applicationDeadline(today).sequence(1).studyOption("1", "Full-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstanceOne, programInstanceTwo);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(progOne, "Full-time");
		assertTrue(listContainsId(programInstanceOne, matchedInstances));
		assertFalse(listContainsId(programInstanceTwo, matchedInstances));
	}
	
	@Test
	public void shouldReturnProgramInstanceWithStudyOptionAndDeadlineNotInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(today).sequence(1).studyOption("1", "Full-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, "Full-time");
		assertTrue(listContainsId(programInstance, matchedInstances));
	}
	
	@Test
	public void shouldNotReturnProgramInstanceWithStudyOptionAndDeadlineInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).sequence(1).studyOption("1", "Full-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, "Full-time");
		assertFalse(matchedInstances.contains(programInstance));
	}
	@Test
	public void shouldNotReturnProgramInstanceWithoutStudyOptionAndDeadlineNotInThePast(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date oneYearAgo = DateUtils.addYears(now, -1);
		ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAgo).sequence(1).studyOption("1", "Full-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstance);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		
		List<ProgramInstance> matchedInstances = dao.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(program, "Full-time");
		assertFalse(matchedInstances.contains(programInstance));
	}
	
	
	@Test
	public void shouldFindProgramInstanceForToday(){
		Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
		save(program);
		Date now = Calendar.getInstance().getTime();
		Date eightMonthsAgo = DateUtils.addMonths(now, -8);
		Date fourMonthsFromNow = DateUtils.addMonths(now, 4);
		Date oneYearAndfourMonthsFromNow = DateUtils.addMonths(now, 16);
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(program).applicationDeadline(eightMonthsAgo).sequence(1).studyOption("31", "Modular/flexible study").applicationStartDate(now).academicYear("2013").enabled(true).build();
		
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow).sequence(2).studyOption("31", "Modular/flexible study").applicationStartDate(now).academicYear("2013").enabled(true).build();
		ProgramInstance programInstanceThree = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAndfourMonthsFromNow).sequence(3).studyOption("31", "Modular/flexible study").applicationStartDate(now).academicYear("2013").enabled(true).build();
		ProgramInstance programInstanceFour = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow).sequence(4).studyOption("31", "Part-time").applicationStartDate(now).academicYear("2013").enabled(true).build();
		save(programInstanceOne,  programInstanceThree,programInstanceFour, programInstanceTwo);
		flushAndClearSession();
		
		ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
		ProgramInstance programInstance = dao.getCurrentProgramInstanceForStudyOption(program, "Modular/flexible study");
		assertEquals(programInstanceTwo.getId(), programInstance.getId());
	}
	
	@Test
	public void shouldFindProgrameInstancesWithAStartDateInTheFuture() {
	    Program program = new ProgramBuilder().code("aaaaa").title("hi").build();
        save(program);
        Date now = Calendar.getInstance().getTime();
        Date yesterday = DateUtils.addDays(now, -1);
        Date eightMonthsAgo = DateUtils.addMonths(now, -8);
        Date fourMonthsFromNow = DateUtils.addMonths(now, 4);
        Date oneYearAndfourMonthsFromNow = DateUtils.addMonths(now, 16);
        
        Date startDateInOneMonth = DateUtils.addMonths(now, 1);
        
        ProgramInstance programInstanceOne = new ProgramInstanceBuilder().program(program).applicationDeadline(eightMonthsAgo).sequence(1).studyOption("31", "Modular/flexible study").applicationStartDate(yesterday).academicYear("2013").enabled(true).build();
        
        ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow).sequence(2).studyOption("31", "Modular/flexible study").applicationStartDate(startDateInOneMonth).academicYear("2013").enabled(true).build();
        ProgramInstance programInstanceThree = new ProgramInstanceBuilder().program(program).applicationDeadline(oneYearAndfourMonthsFromNow).sequence(3).studyOption("1", "Full-time").applicationStartDate(yesterday).academicYear("2013").enabled(true).build();
        ProgramInstance programInstanceFour = new ProgramInstanceBuilder().program(program).applicationDeadline(fourMonthsFromNow).sequence(4).studyOption("31", "Part-time").applicationStartDate(yesterday).academicYear("2013").enabled(true).build();
        save(programInstanceOne,  programInstanceThree,programInstanceFour, programInstanceTwo);
        flushAndClearSession();
        
        ProgramInstanceDAO dao = new ProgramInstanceDAO(sessionFactory);
        List<ProgramInstance> activeProgramInstancesOrderedByApplicationStartDate = dao.getActiveProgramInstancesOrderedByApplicationStartDate(program, "Modular/flexible study");
        
        assertEquals(1, activeProgramInstancesOrderedByApplicationStartDate.size());
	}
	
    private boolean listContainsId(ProgramInstance instance, List<ProgramInstance> instances) {
        for (ProgramInstance entry : instances) {
            if (entry.getId().equals(instance.getId())) {
                return true;
            }
        }
        return false;
    }	
}
