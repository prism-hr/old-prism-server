package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Program;

public class ProgramsServiceTest {

	private ProgramDAO programDAOMock;
	private ProgramsService programsService;

	@Test
	public void shouldGetAllPrograms() {
		Program programOne = EasyMock.createMock(Program.class);
		Program programTwo = EasyMock.createMock(Program.class);
		EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(programOne, programTwo, programDAOMock);
		
		List<Program> allPrograms = programsService.getAllPrograms();
		Assert.assertEquals(2, allPrograms.size());
		Assert.assertTrue(allPrograms.contains(programOne));
		Assert.assertTrue(allPrograms.contains(programTwo));
	}
	
	@Test
	public void shouldGetProgramById() {
		Program program = EasyMock.createMock(Program.class);
		program.setId(2);
		EasyMock.expect(programDAOMock.getProgramById(2)).andReturn(program);
		EasyMock.replay(program, programDAOMock);
		Assert.assertEquals(program, programsService.getProgramById(2));
	}
	
	@Test
	public void shouldGetProgramByCode() {
		Program program = EasyMock.createMock(Program.class);
		EasyMock.expect(programDAOMock.getProgramByCode("code")).andReturn(program);
		EasyMock.replay(program, programDAOMock);
		Assert.assertEquals(program, programsService.getProgramByCode("code"));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		Program program = EasyMock.createMock(Program.class);
		programDAOMock.save(program);
		EasyMock.replay(programDAOMock);
		programsService.save(program);
		EasyMock.verify(programDAOMock);
		
	}
	
	@Before
	public void setUp() {
		programDAOMock = EasyMock.createMock(ProgramDAO.class);
		programsService = new ProgramsService(programDAOMock);
	}
}
