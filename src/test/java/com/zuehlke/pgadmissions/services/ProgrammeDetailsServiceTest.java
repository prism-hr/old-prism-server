package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;


public class ProgrammeDetailsServiceTest {
	
	private ProgrammeDetailDAO programmeDetailDAOMock;
	private ProgrammeDetailsService programmeService;
	private ProgramInstanceDAO programInstanceDAOMock;

	
	
	@Test
	public void shouldDelegateSaveToDAO() {
		ProgrammeDetails program = EasyMock.createMock(ProgrammeDetails.class);
		programmeDetailDAOMock.save(program);
		EasyMock.replay(programmeDetailDAOMock);
		programmeService.save(program);
		EasyMock.verify(programmeDetailDAOMock);
		
	}
	
	@Test
	public void shouldReturnStudyOptionsAvaialbleForProgram(){
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME).toProgramInstance();
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.PART_TIME_DISTANCE).toProgramInstance();
		ProgramInstance programInstanceThree = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME).toProgramInstance();
		Program program = new ProgramBuilder().id(6).toProgram();
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstanceOne, programInstanceTwo, programInstanceThree));
		EasyMock.replay(programInstanceDAOMock);
		
		List<StudyOption> options =  programmeService.getAvailableStudyOptions(program);
		assertEquals(2, options.size());
		assertTrue(options.containsAll(Arrays.asList(StudyOption.FULL_TIME, StudyOption.PART_TIME_DISTANCE)));
	}
	
	@Before
	public void setUp() {
		programmeDetailDAOMock = EasyMock.createMock(ProgrammeDetailDAO.class);
		programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
		programmeService = new ProgrammeDetailsService(programmeDetailDAOMock, programInstanceDAOMock);
	}
}
