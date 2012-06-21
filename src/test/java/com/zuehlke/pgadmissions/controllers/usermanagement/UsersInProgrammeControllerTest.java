package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class UsersInProgrammeControllerTest {
	private UserService userServiceMock;
	private ProgramsService programsServiceMock;
	private RegisteredUser currentUserMock;
	private EncryptionHelper encryptionHelperMock;
	private UsersInProgrammeController controller;


	@Test
	public void shouldReturnEmptyUserInRoleListIfNoProgram() {		
		List<RegisteredUser> users = controller.getUsersInProgram(null);
		assertTrue(users.isEmpty());
	}

	
	@Test
	public void shouldReturnAllUsersForProgram() {
		
		Program program = new ProgramBuilder().id(5).toProgram();
		RegisteredUser userOne = new RegisteredUserBuilder().id(3).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(4).toUser();
		
		EasyMock.expect(programsServiceMock.getProgramByCode("enc")).andReturn(program);
		EasyMock.expect(userServiceMock.getAllUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(programsServiceMock, userServiceMock);
		List<RegisteredUser> users = controller.getUsersInProgram("enc");		
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
		
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
		Program program = new ProgramBuilder().id(5).toProgram();
	
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
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);		
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		

		controller = new UsersInProgrammeController(userServiceMock, programsServiceMock,  encryptionHelperMock);
	}
}
