package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;


public class ProgrammeDetailsServiceTest {
	
	private ProgrammeDetailDAO programmeDetailDAOMock;
	private ProgrammeDetailsService programmeService;

	@Test
	public void shouldGetProgrammeById() {
		ProgrammeDetails program = EasyMock.createMock(ProgrammeDetails.class);
		program.setId(2);
		EasyMock.expect(programmeDetailDAOMock.getProgrammeDetailWithId(2)).andReturn(program);
		EasyMock.replay(program, programmeDetailDAOMock);
		Assert.assertEquals(program, programmeService.getProgrammeDetailsById(2));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		ProgrammeDetails program = EasyMock.createMock(ProgrammeDetails.class);
		programmeDetailDAOMock.save(program);
		EasyMock.replay(programmeDetailDAOMock);
		programmeService.save(program);
		EasyMock.verify(programmeDetailDAOMock);
		
	}
	
	@Before
	public void setUp() {
		programmeDetailDAOMock = EasyMock.createMock(ProgrammeDetailDAO.class);
		programmeService = new ProgrammeDetailsService(programmeDetailDAOMock);
	}
}
