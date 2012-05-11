package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.type.PrimitiveByteArrayBlobType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewUserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class CreateNewUserControllerTest {

	private ProgramsService programServiceMock;
	private CreateNewUserController controller;
	private UserService userServiceMock;
	private RegisteredUser currentUserMock;

	@Test
	public void shouldReturnCurrentUser(){
		assertEquals(currentUserMock, controller.getUser());		
	}
	
	
	@Test
	public void shouldReturnAllProgramsForSuperAdminUser(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);
		Program program1 = new ProgramBuilder().id(1).toProgram();
		Program program2 = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(programServiceMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
		EasyMock.replay(programServiceMock, currentUserMock);
		List<Program> programs = controller.getPrograms();
		assertEquals(2, programs.size());
		assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
	}
	
	@Test
	public void shouldReturnProgramsOfWhichAdministratorForAdmins(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		Program program1 = new ProgramBuilder().id(1).toProgram();
		Program program2 = new ProgramBuilder().id(2).toProgram();
		EasyMock.expect(currentUserMock.getProgramsOfWhichAdministrator()).andReturn(Arrays.asList(program1, program2));
		EasyMock.replay(programServiceMock, currentUserMock);
		List<Program> programs = controller.getPrograms();
		assertEquals(2, programs.size());
		assertTrue(programs.containsAll(Arrays.asList(program1, program2)));
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForSuperadmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		List<Authority> authorities = controller.getAuthorities();
		assertEquals(5, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.SUPERADMINISTRATOR, Authority.INTERVIEWER)));
	}

	@Test
	public void shouldReturnCorrectPossibleRolesForAdmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);
		
		List<Authority> authorities = controller.getAuthorities();
		assertEquals(4, authorities.size());
		assertTrue(authorities.containsAll(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER,  Authority.INTERVIEWER)));
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserNeitherSuperAdminOrAdmin()
	{
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);
		controller.getAddUsersView();
	}
	
	@Test
	public void shoulReturnNewUsersViewForSuperadmin(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();		
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());
	}
	@Test
	public void shoulReturnNewUsersViewForAdmin(){
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.expect(currentUserMock.isInRole(Authority.ADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/create_new_user_in_role_page", controller.getAddUsersView());
	}
	
	@Test
	@Ignore
	public void shouldCreateNewUserInRoles(){
		NewUserDTO newUserDTO = new NewUserDTO();
		newUserDTO.setFirstName("Jane");
		newUserDTO.setLastName("Doe");
		newUserDTO.setEmail("jane.doe@test.com");
		Program program = new ProgramBuilder().id(5).toProgram();
		newUserDTO.setSelectedprogram(program);
		newUserDTO.setSelectedAuthorities(Authority.REVIEWER, Authority.ADMINISTRATOR);
		
	//	EasyMock.expect(userServiceMock.createNewUser(firstname, lastname, email))
		controller.handleNewUserToProgramSubmission(newUserDTO);
	}

	
	@Before
	public void setUp(){		

		userServiceMock = EasyMock.createMock(UserService.class);
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		controller = new CreateNewUserController(programServiceMock,userServiceMock);
	}
}
