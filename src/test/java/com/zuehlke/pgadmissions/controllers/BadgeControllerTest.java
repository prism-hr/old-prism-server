package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.dao.BadgeDAO;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.BadgeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.BadgeService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.validators.BadgeValidator;

public class BadgeControllerTest {

	private UserService userServiceMock;
	private BadgeController controller;
	private ProgramsService programServiceMock;
	private RegisteredUser currentUserMock;
	private BadgeDAO badgeDAOMock;
	private BadgeValidator badgeValidatorMock;
	private ProgramPropertyEditor programPropertyEditorMock;
	private DatePropertyEditor datePropertyEditorMock;
    private BadgeService badgeServiceMock;

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
	public void shouldSaveBadge() {
	    Badge badge = new BadgeBuilder().id(1).closingDate(new Date()).projectTitle("pro").toBadge();
	    badgeDAOMock.save(badge);
	    BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);
        EasyMock.expect(badgeDAOMock.getDuplicateBadges(badge)).andReturn(new ArrayList<Badge>());
	    EasyMock.replay(badgeDAOMock);
	    controller.saveBadgeDetails(badge, errors);
	    EasyMock.verify(badgeDAOMock);
	}
	
	@Before
	public void setUp() {
		userServiceMock = EasyMock.createMock(UserService.class);
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
		badgeValidatorMock = EasyMock.createMock(BadgeValidator.class);
		programPropertyEditorMock = EasyMock.createMock(ProgramPropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		badgeServiceMock = EasyMock.createMock(BadgeService.class);
		controller = new BadgeController(userServiceMock, programServiceMock, badgeDAOMock, datePropertyEditorMock, programPropertyEditorMock, badgeValidatorMock, badgeServiceMock);

		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);

	}
}
