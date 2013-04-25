package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.ApplicationsFilterDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.UserFactory;

public class UserServiceTest {

    private UserDAO userDAOMock;
    private RegisteredUser currentUser;
    private UserService userService;
    private RoleDAO roleDAOMock;
    private ApplicationsFilterDAO applicationsFilterDAOMock;
    private UserService userServiceWithCurrentUserOverride;
    private RegisteredUser currentUserMock;
    private UserFactory userFactoryMock;
    private EncryptionUtils encryptionUtilsMock;
    private MailSendingService mailServiceMock;

	@Test
	public void shouldGetUserFromDAO() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userDAOMock.get(1)).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUser(1));
	}

	@Test
	public void shouldGetUserFromDAOByActivationCode() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userDAOMock.getUserByActivationCode("Abc")).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUserByActivationCode("Abc"));
	}

	@Test
	public void shouldGetAllUsersWithAuthority() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
		Authority auth = Authority.ADMINISTRATOR;
		Role role = new RoleBuilder().id(1).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(auth)).andReturn(role);
		EasyMock.expect(userDAOMock.getUsersInRole(role)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(roleDAOMock, userDAOMock);

		List<RegisteredUser> users = userService.getUsersInRole(auth);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetAllUsersForProgram() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(2).build();
		RegisteredUser userTow = new RegisteredUserBuilder().id(3).build();
		Program program = new ProgramBuilder().id(7).build();
		EasyMock.expect(userDAOMock.getUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTow));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> users = userService.getAllUsersForProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTow)));
	}

	@Test
	public void shouldGetAllInternalUsers() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
		EasyMock.expect(userDAOMock.getInternalUsers()).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> internalUsers = userService.getAllInternalUsers();
		assertEquals(2, internalUsers.size());
		assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldDelegateSaveToDAO() {
		RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
		userDAOMock.save(user);
		EasyMock.replay(userDAOMock);
		userService.save(user);
		EasyMock.verify(userDAOMock);
	}

	@Test
	public void shouldGetAllUsers() {
		RegisteredUser userOne = EasyMock.createMock(RegisteredUser.class);
		RegisteredUser userTwo = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(userOne, userTwo));

		EasyMock.replay(userOne, userTwo, userDAOMock);
		List<RegisteredUser> allUsers = userService.getAllUsers();

		Assert.assertEquals(2, allUsers.size());
		Assert.assertTrue(allUsers.contains(userOne));
		Assert.assertTrue(allUsers.contains(userTwo));
	}

	@Test
	public void shouldGetUserByUsername() {
		RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userDAOMock.getUserByUsername("mike")).andReturn(user);

		EasyMock.replay(user, userDAOMock);
		Assert.assertEquals(user, userService.getUserByUsername("mike"));
	}

	@Test
	public void shouldGetUserFromSecurityContextAndRefresh() {
		RegisteredUser refreshedUser = new RegisteredUser();
		EasyMock.expect(userDAOMock.get(8)).andReturn(refreshedUser);
		EasyMock.replay(userDAOMock);
		RegisteredUser user = userService.getCurrentUser();
		assertSame(refreshedUser, user);
		EasyMock.verify(userDAOMock);
	}

	@Test
	public void shouldAddRoleToUser() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(
				new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build());
		EasyMock.replay(roleDAOMock);
		RegisteredUser user = new RegisteredUserBuilder().build();
		userService.addRoleToUser(user, Authority.ADMINISTRATOR);
		assertEquals(1, user.getRoles().size());
		assertEquals(Authority.ADMINISTRATOR, user.getRoles().get(0).getAuthorityEnum());
	}

	@Test
	public void shouldSaveSelectedUser() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		userDAOMock.save(selectedUser);
		EasyMock.replay(userDAOMock, roleDAOMock);
		Program selectedProgram = new ProgramBuilder().id(4).build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		EasyMock.verify(userDAOMock, roleDAOMock);
	}

	@Test
	public void shouldSetTwoApplicationsFilter() {
		ApplicationsFilter existingFilter = new ApplicationsFilterBuilder()
				.searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm("whatever").build();
		ApplicationsFilter existingFilter2 = new ApplicationsFilterBuilder()
				.searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("whoever").build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1)
				.applicationsFilters(existingFilter, existingFilter2).build();

		ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER)
				.searchTerm("whatever").build();
		ApplicationsFilter filter2 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME)
				.searchTerm("whoever").build();

		applicationsFilterDAOMock.removeFilter(existingFilter);
		applicationsFilterDAOMock.removeFilter(existingFilter2);

		applicationsFilterDAOMock.save(filter);
		applicationsFilterDAOMock.save(filter2);
		
		userDAOMock.save(selectedUser);

		replay(applicationsFilterDAOMock, userDAOMock);
		userService.setFilters(selectedUser, Arrays.asList(filter, filter2));
		verify(applicationsFilterDAOMock, userDAOMock);

		assertSame(selectedUser, filter.getUser());
		assertSame(selectedUser, filter2.getUser());

		assertThat(selectedUser.getApplicationsFilters(), Matchers.contains(filter, filter2));

	}

	@Test
	public void shouldAddUserRoleAdminIfNotAlreadyAdminAndAdminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Program selectedProgram = new ProgramBuilder().id(4).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR))
				.andReturn(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();

		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram,
				Authority.ADMINISTRATOR);
		assertTrue(selectedUser.isInRole(Authority.ADMINISTRATOR));
		assertTrue(selectedUser.isInRole(Authority.VIEWER));
	}

	@Test
	public void shouldAddUserRoleApproverIfNotAlreadyApproverAndAproverInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Program selectedProgram = new ProgramBuilder().id(4).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();

		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
		assertTrue(selectedUser.isInRole(Authority.APPROVER));
		assertTrue(selectedUser.isInRole(Authority.VIEWER));
	}

	@Test
	public void shouldAddUserRoleReviewerIfNotAlreadyRevieweAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Program selectedProgram = new ProgramBuilder().id(4).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.REVIEWER).build()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
		assertTrue(selectedUser.isInRole(Authority.REVIEWER));
		assertTrue(selectedUser.isInRole(Authority.VIEWER));
	}

	@Test
	public void shouldAddUserRoleInterviewerIfNotAlreadyInterviewerAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Program selectedProgram = new ProgramBuilder().id(4).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).build()).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();

		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);
		assertTrue(selectedUser.isInRole(Authority.INTERVIEWER));
		assertTrue(selectedUser.isInRole(Authority.VIEWER));
	}

	@Test
	public void shouldAddUserRoleSupervisorIfNotAlreadySupervisorAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Program selectedProgram = new ProgramBuilder().id(4).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERVISOR))
				.andReturn(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).build()).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERVISOR);
		assertTrue(selectedUser.isInRole(Authority.SUPERVISOR));
		assertTrue(selectedUser.isInRole(Authority.VIEWER));
	}

	@Test
	public void shouldAddUserRoleSuperAdmimnIfNotAlreadySuperadminAndSuperadminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Program selectedProgram = new ProgramBuilder().id(4).build();
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(role).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();

		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram,
				Authority.SUPERADMINISTRATOR);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
		assertTrue(selectedUser.isInRole(Authority.VIEWER));
		EasyMock.verify(roleDAOMock);
	}

	@Test
	public void shouldNotRemoveSuperadminRoleIfNotInNewListAndUserIsNotSuperadmin() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock, roleDAOMock);
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().role(role).id(1).build();

		Program selectedProgram = new ProgramBuilder().id(3).build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
	}

	@Test
	public void shouldAddProgramToAdminListIfNew() {
		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Role role = new RoleBuilder().id(2).authorityEnum(Authority.ADMINISTRATOR).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();

		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram,
				Authority.ADMINISTRATOR);
		assertTrue(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
		assertTrue(selectedUser.getProgramsOfWhichViewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}

	@Test
	public void shouldAddProgramToApproverlistIfNew() {
		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Role role = new RoleBuilder().id(3).authorityEnum(Authority.APPROVER).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(role).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
		assertTrue(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
		assertTrue(selectedUser.getProgramsOfWhichViewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}

	@Test
	public void shouldAddProgramToReviewerListIfNew() {
		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Role role = new RoleBuilder().id(4).authorityEnum(Authority.REVIEWER).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(role).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
		assertTrue(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
		assertTrue(selectedUser.getProgramsOfWhichViewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}

	@Test
	public void shouldAddProgramToInterviewerListIfNew() {
		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.INTERVIEWER).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(role).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
		EasyMock.replay(roleDAOMock);

		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);

		assertTrue(selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram));
		assertTrue(selectedUser.getProgramsOfWhichViewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}

	@Test
	public void shouldAddProgramToSupervisorListIfNew() {
		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).build();
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERVISOR).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERVISOR)).andReturn(role).anyTimes();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER))
				.andReturn(new RoleBuilder().authorityEnum(Authority.VIEWER).build()).anyTimes();
		EasyMock.replay(roleDAOMock);

		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERVISOR);

		assertTrue(selectedUser.getProgramsOfWhichSupervisor().contains(selectedProgram));
		assertTrue(selectedUser.getProgramsOfWhichViewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}

	@Test
	public void shouldRemoveFromProgramsOfWhichAdministratorIfNoLongerInList() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		EasyMock.replay(roleDAOMock);

		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichAdministrator(selectedProgram).id(1)
				.build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
	}

	@Test
	public void shouldRemoveFromProgramsOfWhichSupervisorIfNoLongerInList() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		EasyMock.replay(roleDAOMock);

		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichSupervisor(selectedProgram).id(1)
				.build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichSupervisor().contains(selectedProgram));
	}

	@Test
	public void shouldRemoveFromProgramsOfWhichApproverIfNoLongerInList() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		EasyMock.replay(roleDAOMock);

		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichApprover(selectedProgram).id(1)
				.build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
	}

	@Test
	public void shouldRemoveFromProgramsOfWhichReviewerIfNoLongerInList() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		EasyMock.replay(roleDAOMock);

		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichReviewer(selectedProgram).id(1)
				.build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
	}

	@Test
	public void shouldRemoveFromProgramsOfWhichInterviewerIfNoLongerInList() {
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(
				new RoleBuilder().authorityEnum(Authority.VIEWER).build());
		EasyMock.replay(roleDAOMock);

		Program selectedProgram = new ProgramBuilder().id(1).build();
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichInterviewer(selectedProgram).id(1)
				.build();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowISEwhenUserAlreadyExistsForNewUserInProgramme() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(1).build();

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
		EasyMock.replay(userDAOMock);
		userService.createNewUserForProgramme("la", "le", "some@email.com", new ProgramBuilder().id(4).build());
	}

	@Test
	public void shouldCreateUserAndWithRolesInProgramme() {
		Program program = new ProgramBuilder().id(4).build();
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);

		EasyMock.expect(
				userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Arrays.asList(
						Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER,
						Authority.INTERVIEWER, Authority.VIEWER))).andReturn(newUser);

		userDAOMock.save(newUser);
		EasyMock.expectLastCall().andDelegateTo(new CheckProgrammeAndSimulateSaveDAO(program));

		Role role_1 = new RoleBuilder().id(1).build();
		Role role_2 = new RoleBuilder().id(2).build();
		Role role_3 = new RoleBuilder().id(3).build();
		Role role_4 = new RoleBuilder().id(4).build();
		Role role_5 = new RoleBuilder().id(5).build();
		Role role_6 = new RoleBuilder().id(6).build();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(role_1);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role_2);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(role_3);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(role_4);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(role_5);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.VIEWER)).andReturn(role_6);

		EasyMock.replay(userDAOMock, roleDAOMock, userFactoryMock);

		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserForProgramme("la", "le",
				"some@email.com", program, Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER,
				Authority.REVIEWER, Authority.INTERVIEWER);

		EasyMock.verify(userDAOMock, roleDAOMock, userFactoryMock);
		assertEquals(newUser, createdUser);

		assertTrue(createdUser.getProgramsOfWhichAdministrator().contains(program));
		assertTrue(createdUser.getProgramsOfWhichApprover().contains(program));
		assertTrue(createdUser.getProgramsOfWhichInterviewer().contains(program));
		assertTrue(createdUser.getProgramsOfWhichReviewer().contains(program));

		assertEquals(6, createdUser.getPendingRoleNotifications().size());
		PendingRoleNotification pendingRoleNotification = createdUser.getPendingRoleNotifications().get(0);
		assertEquals(role_1, pendingRoleNotification.getRole());
		assertNull(pendingRoleNotification.getProgram());
		assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

		pendingRoleNotification = createdUser.getPendingRoleNotifications().get(1);
		assertEquals(role_4, pendingRoleNotification.getRole());
		assertEquals(program, pendingRoleNotification.getProgram());
		assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

		pendingRoleNotification = createdUser.getPendingRoleNotifications().get(2);
		assertEquals(role_2, pendingRoleNotification.getRole());
		assertEquals(program, pendingRoleNotification.getProgram());
		assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

		pendingRoleNotification = createdUser.getPendingRoleNotifications().get(3);
		assertEquals(role_3, pendingRoleNotification.getRole());
		assertEquals(program, pendingRoleNotification.getProgram());
		assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

		pendingRoleNotification = createdUser.getPendingRoleNotifications().get(4);
		assertEquals(role_5, pendingRoleNotification.getRole());
		assertEquals(program, pendingRoleNotification.getProgram());
		assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());

		pendingRoleNotification = createdUser.getPendingRoleNotifications().get(5);
		assertEquals(role_6, pendingRoleNotification.getRole());
		assertEquals(program, pendingRoleNotification.getProgram());
		assertEquals(currentUserMock, pendingRoleNotification.getAddedByUser());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowISEwhenUserAlreadyExistsForNewUserNotInProgramme() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(1).build();

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
		EasyMock.replay(userDAOMock);
		userService.createNewUserInRole("la", "le", "some@email.com", Authority.APPROVER, null, null);
	}

	@Test
	public void shouldCreateUserAndWithRolesNotInAnyProgramme() {

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER))
				.andReturn(newUser);
		userDAOMock.save(newUser);

		EasyMock.replay(userDAOMock, userFactoryMock);

		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole("la", "le",
				"some@email.com", Authority.REVIEWER, null, null);

		EasyMock.verify(userDAOMock, userFactoryMock);
		assertEquals(newUser, createdUser);

		assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichInterviewer().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichReviewer().isEmpty());

		assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
	}

	@Test
	public void shouldCreateUserWithDirectToLinkToAddReview() {

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER))
				.andReturn(newUser);
		userDAOMock.save(newUser);

		EasyMock.replay(userDAOMock, userFactoryMock);

		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole("la", "le",
				"some@email.com", Authority.REVIEWER, DirectURLsEnum.ADD_REVIEW, new ApplicationFormBuilder().id(1)
						.applicationNumber("bob").build());

		EasyMock.verify(userDAOMock, userFactoryMock);
		assertEquals(newUser, createdUser);
		assertEquals("/reviewFeedback?applicationId=bob", createdUser.getDirectToUrl());

		assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichInterviewer().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichReviewer().isEmpty());

		assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
	}

	@Test
	public void shouldCreateUserWithDirectToLinkToViewapplication() {

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).build();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.INTERVIEWER))
				.andReturn(newUser);
		userDAOMock.save(newUser);

		EasyMock.replay(userDAOMock, userFactoryMock);

		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole("la", "le",
				"some@email.com", Authority.INTERVIEWER, DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW,
				new ApplicationFormBuilder().id(1).applicationNumber("bob").build());

		EasyMock.verify(userDAOMock, userFactoryMock);
		assertEquals(newUser, createdUser);
		assertEquals("/application?view=view&applicationId=bob", createdUser.getDirectToUrl());

		assertTrue(createdUser.getProgramsOfWhichAdministrator().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichApprover().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichInterviewer().isEmpty());
		assertTrue(createdUser.getProgramsOfWhichReviewer().isEmpty());

		assertTrue(createdUser.getPendingRoleNotifications().isEmpty());
	}

	@Test
	public void shouldGetAllPreviousInterviewersOfProgam() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
		Program program = new ProgramBuilder().id(5).build();
		EasyMock.expect(userDAOMock.getAllPreviousInterviewersOfProgram(program)).andReturn(
				Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);

		List<RegisteredUser> users = userService.getAllPreviousInterviewersOfProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetAllPreviousReviewersOfProgam() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
		Program program = new ProgramBuilder().id(5).build();
		EasyMock.expect(userDAOMock.getAllPreviousReviewersOfProgram(program)).andReturn(
				Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);

		List<RegisteredUser> users = userService.getAllPreviousReviewersOfProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetAllReviewersWillingToItnerview() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
		EasyMock.expect(userDAOMock.getReviewersWillingToInterview(applicationForm)).andReturn(
				Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);

		List<RegisteredUser> users = userService.getReviewersWillingToInterview(applicationForm);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetAllPreviousSupervisorsOfProgam() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).build();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).build();
		Program program = new ProgramBuilder().id(5).build();
		EasyMock.expect(userDAOMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(
				Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);

		List<RegisteredUser> users = userService.getAllPreviousSupervisorsOfProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

    @Test
    public void shouldUpdateCurrentUserAndSendEmailConfirmation() throws UnsupportedEncodingException {
        final RegisteredUser currentUser = new RegisteredUserBuilder().firstName("f").lastName("l").id(7)
                .password("12").email("em").username("em").build();
        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
                encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock) {

			@Override
			public RegisteredUser getCurrentUser() {
				return currentUser;
			}

		};
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("newpass")).andReturn("encryptednewpass");
		RegisteredUser userOne = new RegisteredUserBuilder().firstName("a").firstName2("a2").firstName3("a3")
				.lastName("o").email("two").password("12").newPassword("newpass").build();

		EasyMock.replay(encryptionUtilsMock);

		userServiceWithCurrentUserOverride.updateCurrentUser(userOne);
		EasyMock.verify(encryptionUtilsMock);
		assertEquals("two", currentUser.getUsername());
		assertEquals("two", currentUser.getEmail());
		assertEquals("a", currentUser.getFirstName());
		assertEquals("a2", currentUser.getFirstName2());
		assertEquals("a3", currentUser.getFirstName3());
		assertEquals("o", currentUser.getLastName());
		assertEquals("encryptednewpass", currentUser.getPassword());
	}

    @Test
    public void shouldNotChangePassIfPasswordIsBlank() {
        final RegisteredUser currentUser = new RegisteredUserBuilder().password("12").email("em").username("em")
                .build();
        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
                encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock) {

			@Override
			public RegisteredUser getCurrentUser() {
				return currentUser;
			}

		};
		RegisteredUser userOne = new RegisteredUserBuilder().username("one").email("two").password("").id(5).build();
		userServiceWithCurrentUserOverride.save(currentUser);
		userServiceWithCurrentUserOverride.updateCurrentUser(userOne);
		assertEquals("two", currentUser.getUsername());
		assertEquals("two", currentUser.getEmail());
		assertEquals("12", currentUser.getPassword());
	}

	@Test
	public void shouldReturnIfAccountsHaveAlreadyBeenLinked() {
		final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
				.accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password")
				.build();

		final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true)
				.accountNonLocked(true).enabled(true).activationCode("abcd").email("A@B.com").password("password")
				.build();

		secondAccount.setPrimaryAccount(currentAccount);

        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
               encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock) {

			@Override
			public RegisteredUser getCurrentUser() {
				return currentAccount;
			}

			@Override
			public RegisteredUser getUserByEmail(String email) {
				return secondAccount;
			}
		};

		try {
			userServiceWithCurrentUserOverride.linkAccounts(secondAccount.getEmail());
		} catch (LinkAccountsException e) {
			fail();
		}
	}

	@Test(expected = LinkAccountsException.class)
	public void shouldReturnFalseIfAccountsDisabled() throws LinkAccountsException {
		final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
				.accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password")
				.build();

		final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true)
				.accountNonLocked(true).enabled(false).activationCode("abcd").email("A@B.com").password("password")
				.build();

        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
               encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock) {

			@Override
			public RegisteredUser getCurrentUser() {
				return currentAccount;
			}

			@Override
			public RegisteredUser getUserByEmail(String email) {
				return secondAccount;
			}
		};

		userServiceWithCurrentUserOverride.linkAccounts(secondAccount.getEmail());
	}

	@Test(expected = LinkAccountsException.class)
	public void shouldReturnFalseIfAccountsIsExpired() throws LinkAccountsException {
		final RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
				.accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password")
				.build();

		final RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(false)
				.accountNonLocked(true).enabled(true).activationCode("abcd").email("A@B.com").password("password")
				.build();

        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
                encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock) {

			@Override
			public RegisteredUser getCurrentUser() {
				return currentAccount;
			}

			@Override
			public RegisteredUser getUserByEmail(String email) {
				return secondAccount;
			}
		};

		userServiceWithCurrentUserOverride.linkAccounts(secondAccount.getEmail());
	}

	@Test
    public void shouldSendemailToDelegateForInterviewAdministration() {
    	RegisteredUser delegate = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
    	RegisteredUser admin1 = new RegisteredUserBuilder().email("admin1@zuhlke.com").id(2).build();
    	RegisteredUser admin2 = new RegisteredUserBuilder().email("admin2@zuhlke.com").id(3).firstName("Claudio").lastName("Scandura").build();
    	ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().administrators(admin1, admin2).build()).build();
    	
    	mailServiceMock.scheduleInterviewAdministrationRequest(delegate, applicationForm);
    	
    	replay(mailServiceMock);
    	userService.sendEmailToDelegateAndRegisterReminder(applicationForm, delegate);
    	verify(mailServiceMock);
	}
	
	 @Before
	    public void setUp() {
	        encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
	        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
	        currentUser = new RegisteredUserBuilder().id(8).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
	        currentUserMock = EasyMock.createMock(RegisteredUser.class);
	        authenticationToken.setDetails(currentUser);
	        SecurityContextImpl secContext = new SecurityContextImpl();
	        secContext.setAuthentication(authenticationToken);
	        SecurityContextHolder.setContext(secContext);

	        userDAOMock = EasyMock.createMock(UserDAO.class);
	        roleDAOMock = EasyMock.createMock(RoleDAO.class);
	        applicationsFilterDAOMock = EasyMock.createMock(ApplicationsFilterDAO.class);
	        userFactoryMock = EasyMock.createMock(UserFactory.class);
	        mailServiceMock = createMock(MailSendingService.class);
	        userService = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
	                encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock);
	        userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock, userFactoryMock,
	                encryptionUtilsMock, applicationsFilterDAOMock, mailServiceMock) {

	            @Override
	            public RegisteredUser getCurrentUser() {
	                return currentUserMock;
	            }
	        };
	    }

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	class CheckProgrammeAndSimulateSaveDAO extends UserDAO {
		private final Program expectedProgramme;

		public CheckProgrammeAndSimulateSaveDAO(Program programme) {
			super(null);
			this.expectedProgramme = programme;
		}

		@Override
		public void save(RegisteredUser user) {
			Assert.assertTrue(user.getProgramsOfWhichReviewer().contains(expectedProgramme));
		}
	}
}
