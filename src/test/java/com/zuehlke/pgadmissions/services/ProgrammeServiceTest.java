package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;


public class ProgrammeServiceTest {
	
	private ProgrammeDetailDAO programmeDetailDAOMock;
	private ProgrammeService programmeService;

	@Test
	public void shouldGetProgrammeById() {
		ProgrammeDetail program = EasyMock.createMock(ProgrammeDetail.class);
		program.setId(2);
		EasyMock.expect(programmeDetailDAOMock.getProgrammeDetailWithId(2)).andReturn(program);
		EasyMock.replay(program, programmeDetailDAOMock);
		Assert.assertEquals(program, programmeService.getProgrammeDetailsById(2));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		ProgrammeDetail program = EasyMock.createMock(ProgrammeDetail.class);
		programmeDetailDAOMock.save(program);
		EasyMock.replay(programmeDetailDAOMock);
		programmeService.save(program);
		EasyMock.verify(programmeDetailDAOMock);
		
	}
	
	@Before
	public void setUp() {
		programmeDetailDAOMock = EasyMock.createMock(ProgrammeDetailDAO.class);
		programmeService = new ProgrammeService(programmeDetailDAOMock);
	}
}
