package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

public class UsersInProgrammeControllerTest {
	private UserService userServiceMock;
	private ProgramService programsServiceMock;
	private RegisteredUser currentUserMock;
	private UsersInProgrammeController controller;

	@Test
	public void shouldReturnEmptyUserInRoleListIfNoProgram() {		
		List<RegisteredUser> users = controller.getUsersInProgram(null);
		assertTrue(users.isEmpty());
	}
	
	@Test
	public void shouldReturnUsersForProgramOrderedByLastnameFirstname() {
		Program program = new ProgramBuilder().id(5).build();
		RegisteredUser userOne = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userOne.getAuthoritiesForProgram(program)).andReturn(Arrays.asList( Authority.APPROVER)).anyTimes();
		EasyMock.expect(userOne.getId()).andReturn(1).anyTimes();
		EasyMock.expect(userOne.getLastName()).andReturn("ZZZ").anyTimes();
		EasyMock.expect(userOne.getFirstName()).andReturn("BBB").anyTimes();
		
		RegisteredUser userTwo = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userTwo.getAuthoritiesForProgram(program)).andReturn(Arrays.asList( Authority.APPROVER)).anyTimes();
		EasyMock.expect(userTwo.getId()).andReturn(2).anyTimes();
		EasyMock.expect(userTwo.getLastName()).andReturn("ZZZ").anyTimes();
		EasyMock.expect(userTwo.getFirstName()).andReturn("AAA").anyTimes();
		
		RegisteredUser userThree = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userThree.getId()).andReturn(3).anyTimes();
		EasyMock.expect(userThree.getAuthoritiesForProgram(program)).andReturn(Arrays.asList( Authority.APPROVER)).anyTimes();
		EasyMock.expect(userThree.getLastName()).andReturn("AAA").anyTimes();
		EasyMock.expect(userThree.getFirstName()).andReturn("GGG").anyTimes();
		
		EasyMock.expect(programsServiceMock.getProgramByCode("enc")).andReturn(program);
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo, userThree));
		EasyMock.replay(userOne, userTwo, userThree, programsServiceMock, userServiceMock);
		
		List<RegisteredUser> users = controller.getUsersInProgram("enc");		
		
		assertEquals(3, users.size());
		assertEquals(userThree, users.get(0));
		assertEquals(userTwo, users.get(1));
		assertEquals(userOne, users.get(2));
		
	}
	@SuppressWarnings("unchecked")
	@Test
	public void shouldExcludeUsersWhoAreSuperadminsOnly() {
		
		Program program = new ProgramBuilder().id(5).build();
		RegisteredUser userOne = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userOne.getAuthoritiesForProgram(program)).andReturn(Collections.EMPTY_LIST).anyTimes();
		RegisteredUser userTwo = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userTwo.getAuthoritiesForProgram(program)).andReturn(Arrays.asList( Authority.APPROVER)).anyTimes();
		EasyMock.expect(programsServiceMock.getProgramByCode("enc")).andReturn(program);
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userOne, userTwo, programsServiceMock, userServiceMock);
		List<RegisteredUser> users = controller.getUsersInProgram("enc");		
		assertEquals(1, users.size());
		assertTrue(users.containsAll(Arrays.asList(userTwo)));
		
	}
	
	@Test
	public void shouldReutrnEmptyListIfProgramDoesNotExist() {
		
		EasyMock.expect(programsServiceMock.getProgramByCode("enc")).andReturn(null);
	
		EasyMock.replay(programsServiceMock);
		List<RegisteredUser> users = controller.getUsersInProgram("enc");		
		assertTrue(users.isEmpty());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();		
		EasyMock.replay(currentUserMock);
		controller.getUsersInProgramView();
	}
	@Test
  	public void shouldReturnUsersInRolesView() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();		
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/users_roles", controller.getUsersInProgramView());
	}
	
	@Test
	public void shouldGetSelectedProgramfIdProvided() {
		Program program = new ProgramBuilder().id(5).build();
	
		EasyMock.expect(programsServiceMock.getProgramByCode("enc")).andReturn(program);
		EasyMock.replay(programsServiceMock);

		assertEquals(program, controller.getSelectedProgram("enc"));
		EasyMock.verify(programsServiceMock);
	}

	@Test
	public void shoudlReturnNullIfProgramIdNotProvided() {
		assertNull(controller.getSelectedProgram(null));
	}

	@Before
	public void setUp(){
		userServiceMock = EasyMock.createMock(UserService.class);
		programsServiceMock = EasyMock.createMock(ProgramService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);		
		controller = new UsersInProgrammeController(userServiceMock, programsServiceMock);
	}
}
