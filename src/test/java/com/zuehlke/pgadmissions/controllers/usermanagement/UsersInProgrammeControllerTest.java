package com.zuehlke.pgadmissions.controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

public class UsersInProgrammeControllerTest {
	private UserService userServiceMock;
	private ProgramService programsServiceMock;
	private User currentUserMock;
	private UsersInProgrammeController controller;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowExceptionForNonAdministrators() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.replay(currentUserMock);
		controller.getUsersInProgramView();
	}
	@Test
  	public void shouldReturnUsersInRolesView() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.replay(currentUserMock);
		assertEquals("private/staff/superAdmin/users_roles", controller.getUsersInProgramView());
	}
	
	@Test
	public void shouldGetSelectedProgramfIdProvided() {
		Program program = new Program().withId(5);
	
		EasyMock.expect(programsServiceMock.getProgramByCode("enc")).andReturn(program);
		EasyMock.replay(programsServiceMock);

		assertEquals(program, controller.getSelectedProgram("enc"));
		EasyMock.verify(programsServiceMock);
	}

	@Test
	public void shoudlReturnNullIfProgramIdNotProvided() {
		assertNull(controller.getSelectedProgram(null));
	}

}
