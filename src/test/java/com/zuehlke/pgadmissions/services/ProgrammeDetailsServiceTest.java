package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.SourcesOfInterestDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;


public class ProgrammeDetailsServiceTest {
	
	private ProgrammeDetailDAO programmeDetailDAOMock;
	private ProgramDetailsService programmeService;
	private ProgramInstanceDAO programInstanceDAOMock;
	private SourcesOfInterestDAO sourcesOfInterestDAOMock;
	
	@Test
	public void shouldDelegateSaveToDAO() {
		ProgramDetails program = EasyMock.createMock(ProgramDetails.class);
		programmeDetailDAOMock.save(program);
		EasyMock.replay(programmeDetailDAOMock);
		programmeService.save(program);
		EasyMock.verify(programmeDetailDAOMock);
	}
	
	@Test
	public void shouldReturnStudyOptionsAvaialbleForProgram(){
		ProgramInstance programInstanceOne = new ProgramInstanceBuilder().id(1).studyOption("1", "Full-time").build();
		ProgramInstance programInstanceTwo = new ProgramInstanceBuilder().id(1).studyOption("31", "Part-time").build();
		ProgramInstance programInstanceThree = new ProgramInstanceBuilder().id(1).studyOption("1", "Full-time").build();
		Program program = new ProgramBuilder().id(6).build();
		EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstanceOne, programInstanceTwo, programInstanceThree));
		EasyMock.replay(programInstanceDAOMock);
		
		List<StudyOption> options =  programmeService.getAvailableStudyOptions(program);
		assertEquals(2, options.size());
		assertTrue(options.containsAll(Arrays.asList(new StudyOption("1", "Full-time"), new StudyOption("31", "Part-time"))));
	}
	
	@Before
	public void setUp() {
		programmeDetailDAOMock = EasyMock.createMock(ProgrammeDetailDAO.class);
		programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
		sourcesOfInterestDAOMock = EasyMock.createMock(SourcesOfInterestDAO.class);
		programmeService = new ProgramDetailsService(programmeDetailDAOMock, programInstanceDAOMock, sourcesOfInterestDAOMock);
	}
}
