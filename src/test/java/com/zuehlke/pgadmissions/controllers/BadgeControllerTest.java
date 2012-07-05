package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

public class BadgeControllerTest {

	private UserService userServiceMock;
	private BadgeController controller;
	private ProgramsService programServiceMock;
	private RegisteredUser currentUserMock;

	@Test
	public void shouldReturnCurrentUser() {

		assertEquals(currentUserMock, controller.getUser());
	}

	@Test
	public void shouldReturnAllProgramsForSuperAdmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);

		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programServiceMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(programServiceMock);

		List<Program> allPrograms = controller.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}

	@Test
	public void shouldReturnProgramsOfWhichUserAdministratorForAdmin() {
		Program programOne = new ProgramBuilder().id(1).toProgram();
		Program programTwo = new ProgramBuilder().id(2).toProgram();

		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.expect(currentUserMock.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(programOne, programTwo));
		EasyMock.replay(currentUserMock);

		List<Program> allPrograms = controller.getPrograms();

		assertEquals(2, allPrograms.size());
		assertTrue(allPrograms.containsAll(Arrays.asList(programOne, programTwo)));
	}

	@Test
	public void shouldReturnBadgePage(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		
		assertEquals("private/staff/superAdmin/badge_management", controller.getCreateBadgePage());
		
	}
	

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNietherSuperAdminOrAdmin(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);		
		controller.getCreateBadgePage();		
	}
	

	@Test
	public void shouldReturnProgramForCode(){
		Program program = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		EasyMock.replay(programServiceMock);
		assertEquals(program, controller.getProgram("code"));
		
	}
	
	@Test
	public void shouldReturnEnvironmentHostAsHost(){
		assertEquals(Environment.getInstance().getApplicationHostName(), controller.getHost());
	}
	
	@Test
	public void shouldReturnBadge(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();		
		EasyMock.replay(currentUserMock);		
		assertEquals("private/staff/superAdmin/badge", controller.getBadge());
	}
	

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionForBadgeIfUserNietherSuperAdminOrAdmin(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);		
		controller.getBadge();		
	}
	@Before
	public void setUp() {

		userServiceMock = EasyMock.createMock(UserService.class);
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		controller = new BadgeController(userServiceMock, programServiceMock);

		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

	}
}
