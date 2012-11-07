package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.UserFactory;

public class UserServiceTest {

	private UserDAO userDAOMock;
	private RegisteredUser currentUser;
	private UserService userService;
	private RoleDAO roleDAOMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private JavaMailSender mailsenderMock;
	private UserService userServiceWithCurrentUserOverride;
	private RegisteredUser currentUserMock;
	private UserFactory userFactoryMock;
	private MessageSource msgSourceMock;
	private EncryptionUtils encryptionUtilsMock;

	@Test
	public void shouldGetUserFromDAO() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userDAOMock.get(1)).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUser(1));
	}
	
	@Test
	public void shouldGetUserFromDAOByActivationCode() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userDAOMock.getUserByActivationCode("Abc")).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUserByActivationCode("Abc"));
	}
	
	@Test
	public void shouldGetAllUsersWithAuthority() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		Authority auth = Authority.ADMINISTRATOR;
		Role role = new RoleBuilder().id(1).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(auth)).andReturn(role);
		EasyMock.expect(userDAOMock.getUsersInRole(role)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(roleDAOMock, userDAOMock);

		List<RegisteredUser> users = userService.getUsersInRole(auth);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	

	@Test
	public void shouldGetAllUsersForProgram() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(2).toUser();
		RegisteredUser userTow = new RegisteredUserBuilder().id(3).toUser();
		Program program = new ProgramBuilder().id(7).toProgram();
		EasyMock.expect(userDAOMock.getUsersForProgram(program)).andReturn(Arrays.asList(userOne, userTow));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> users = userService.getAllUsersForProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTow)));
	}

	@Test
	public void shouldGetAllInternalUsers() {
		RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
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
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole());
		EasyMock.replay(roleDAOMock);
		RegisteredUser user = new RegisteredUserBuilder().toUser();
		userService.addRoleToUser(user, Authority.ADMINISTRATOR);
		assertEquals(1, user.getRoles().size());
		assertEquals(Authority.ADMINISTRATOR, user.getRoles().get(0).getAuthorityEnum());
		
	}
	
	
	@Test
	public void shouldSaveSelectedUser() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		userDAOMock.save(selectedUser);
		EasyMock.replay(userDAOMock);
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		EasyMock.verify(userDAOMock);
	}
	
	@Test
	public void shouldAddUserRoleAdminIfNotAlreadyAdminAndAdminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();				
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
		assertTrue(selectedUser.isInRole(Authority.ADMINISTRATOR));
	}	
	
	@Test
	public void shouldAddUserRoleApproverIfNotAlreadyApproverAndAproverInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
		assertTrue(selectedUser.isInRole(Authority.APPROVER));
	}

	@Test
	public void shouldAddUserRoleReviewerIfNotAlreadyRevieweAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
		assertTrue(selectedUser.isInRole(Authority.REVIEWER));
	}
	
	@Test
	public void shouldAddUserRoleInterviewerIfNotAlreadyInterviewerAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);
		assertTrue(selectedUser.isInRole(Authority.INTERVIEWER));
	}
	
	@Test
	public void shouldAddUserRoleSupervisorIfNotAlreadySupervisorAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERVISOR)).andReturn(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).toRole()).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERVISOR);
		assertTrue(selectedUser.isInRole(Authority.SUPERVISOR));
	}
	@Test
	public void shouldAddUserRoleSuperAdmimnIfNotAlreadySuperadminAndSuperadminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(role).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERADMINISTRATOR);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
		EasyMock.verify(roleDAOMock);
		
	}
	
	
	
	@Test
	public void shouldNotRemoveSuperadminRoleIfNotInNewListAndUserIsNotSuperadmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false).anyTimes();
		EasyMock.replay(currentUserMock);
		Role role= new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		RegisteredUser selectedUser = new RegisteredUserBuilder().role(role).id(1).toUser();
		

		Program selectedProgram = new ProgramBuilder().id(3).toProgram();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
	}
	
	@Test
	public void shouldAddProgramToAdminListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();			
		Role role = new RoleBuilder().id(2).authorityEnum(Authority.ADMINISTRATOR).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role).anyTimes();;
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
		assertTrue(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);

		
	}
	
	@Test
	public void shouldAddProgramToApproverlistIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		Role role = new RoleBuilder().id(3).authorityEnum(Authority.APPROVER).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(role).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
		assertTrue(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);

	}
	
	
	@Test
	public void shouldAddProgramToReviewerListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		Role role = new RoleBuilder().id(4).authorityEnum(Authority.REVIEWER).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(role).anyTimes();
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
		assertTrue(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);

	}
	
	@Test
	public void shouldAddProgramToInterviewerListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.INTERVIEWER).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(role).anyTimes();
		EasyMock.replay(roleDAOMock);
		
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);
		
		assertTrue(selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram));		
		EasyMock.verify(roleDAOMock);
		
	}
	
	
	@Test
	public void shouldAddProgramToSupervisorListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		Role role = new RoleBuilder().id(1).authorityEnum(Authority.SUPERVISOR).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERVISOR)).andReturn(role).anyTimes();
		EasyMock.replay(roleDAOMock);
		
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERVISOR);
		
		assertTrue(selectedUser.getProgramsOfWhichSupervisor().contains(selectedProgram));		
		EasyMock.verify(roleDAOMock);
		
	}
	
	@Test
	public void shouldRemoveFromProgramsOfWhichAdministratorIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichAdministrator(selectedProgram).id(1).toUser();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
	}
	
	@Test
	public void shouldRemoveFromProgramsOfWhichSupervisorIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichSupervisor(selectedProgram).id(1).toUser();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichSupervisor().contains(selectedProgram));
	}
	
	@Test
	public void shouldRemoveFromProgramsOfWhichApproverIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichApprover(selectedProgram).id(1).toUser();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
	}
	
	
	@Test
	public void shouldRemoveFromProgramsOfWhichReviewerIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichReviewer(selectedProgram).id(1).toUser();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
	}
	
	@Test
	public void shouldRemoveFromProgramsOfWhichInterviewerIfNoLongerInList(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();		
		RegisteredUser selectedUser = new RegisteredUserBuilder().programsOfWhichInterviewer(selectedProgram).id(1).toUser();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowISEwhenUserAlreadyExistsForNewUserInProgramme() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(1).toUser();
		
		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
		EasyMock.replay(userDAOMock);
		userService.createNewUserForProgramme( "la", "le", "some@email.com", new ProgramBuilder().id(4).toProgram());
	}

	@Test
	public void shouldCreateUserAndWithRolesInProgramme() {
		Program program = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com",Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER)).andReturn(newUser);
		userDAOMock.save(newUser);
		EasyMock.expectLastCall().andDelegateTo(new CheckProgrammeAndSimulateSaveDAO(program));


		Role role_1 = new RoleBuilder().id(1).toRole();
		Role role_2 = new RoleBuilder().id(2).toRole();
		Role role_3 = new RoleBuilder().id(3).toRole();
		Role role_4 = new RoleBuilder().id(4).toRole();
		Role role_5 = new RoleBuilder().id(5).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(role_1);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role_2);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(role_3);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(role_4);
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(role_5);
		EasyMock.replay(userDAOMock, roleDAOMock, userFactoryMock);
		
		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserForProgramme( "la", "le", "some@email.com", program,Authority.SUPERADMINISTRATOR,  Authority.ADMINISTRATOR, Authority.APPROVER, 
				Authority.REVIEWER, Authority.INTERVIEWER);

		EasyMock.verify(userDAOMock, roleDAOMock, userFactoryMock);
		assertEquals(newUser, createdUser);				

		assertTrue(createdUser.getProgramsOfWhichAdministrator().contains(program));
		assertTrue(createdUser.getProgramsOfWhichApprover().contains(program));
		assertTrue(createdUser.getProgramsOfWhichInterviewer().contains(program));
		assertTrue(createdUser.getProgramsOfWhichReviewer().contains(program));
		
		assertEquals(5, createdUser.getPendingRoleNotifications().size());
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
	}
	
	@Test(expected = IllegalStateException.class)
	public void shouldThrowISEwhenUserAlreadyExistsForNewUserNotInProgramme() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(1).toUser();
		
		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(existingUser);
		EasyMock.replay(userDAOMock);
		userService.createNewUserInRole( "la", "le", "some@email.com", Authority.APPROVER, null, null);
	}
	
	
	@Test
	public void shouldCreateUserAndWithRolesNotInAnyProgramme() {

		EasyMock.expect(userDAOMock.getUserByEmail("some@email.com")).andReturn(null);
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER)).andReturn(newUser);
		userDAOMock.save(newUser);		

		EasyMock.replay(userDAOMock, userFactoryMock);
		
		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole( "la", "le", "some@email.com", Authority.REVIEWER, null, null);

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
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.REVIEWER)).andReturn(newUser);
		userDAOMock.save(newUser);		
		
		EasyMock.replay(userDAOMock, userFactoryMock);
		
		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole( "la", "le", "some@email.com", Authority.REVIEWER, DirectURLsEnum.ADD_REVIEW, new ApplicationFormBuilder().id(1).applicationNumber("bob").toApplicationForm());
		
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
		RegisteredUser newUser = new RegisteredUserBuilder().id(5).toUser();
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com", Authority.INTERVIEWER)).andReturn(newUser);
		userDAOMock.save(newUser);		
		
		EasyMock.replay(userDAOMock, userFactoryMock);
		
		RegisteredUser createdUser = userServiceWithCurrentUserOverride.createNewUserInRole( "la", "le", "some@email.com", Authority.INTERVIEWER, DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW, new ApplicationFormBuilder().id(1).applicationNumber("bob").toApplicationForm());
		
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
	public void shouldGetAllPreviousInterviewersOfProgam(){
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).toUser();
		Program program = new ProgramBuilder().id(5).toProgram();
		EasyMock.expect(userDAOMock.getAllPreviousInterviewersOfProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);
		
		List<RegisteredUser> users = userService.getAllPreviousInterviewersOfProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));		
	}
	
	@Test
	public void shouldGetAllPreviousReviewersOfProgam(){
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).toUser();
		Program program = new ProgramBuilder().id(5).toProgram();
		EasyMock.expect(userDAOMock.getAllPreviousReviewersOfProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);
		
		List<RegisteredUser> users = userService.getAllPreviousReviewersOfProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));		
	}

	
	@Test
	public void shouldGetAllReviewersWillingToItnerview(){
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(userDAOMock.getReviewersWillingToInterview(applicationForm)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);
		
		List<RegisteredUser> users = userService.getReviewersWillingToInterview(applicationForm);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));		
	}
	
	@Test
	public void shouldGetAllPreviousSupervisorsOfProgam(){
		RegisteredUser userOne = new RegisteredUserBuilder().id(5).toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().id(6).toUser();
		Program program = new ProgramBuilder().id(5).toProgram();
		EasyMock.expect(userDAOMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(Arrays.asList(userOne, userTwo));
		EasyMock.replay(userDAOMock);
		
		List<RegisteredUser> users = userService.getAllPreviousSupervisorsOfProgram(program);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));		
	}
	
	@Test
	public void shouldUpdateCurrentUserAndSendEmailConfirmation() throws UnsupportedEncodingException{
		final RegisteredUser currentUser = new RegisteredUserBuilder().firstName("f").lastName("l").id(7).password("12").email("em").username("em").toUser();
		userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock,userFactoryMock,
				mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock){

			@Override
			public RegisteredUser getCurrentUser() {
				return currentUser;
			}
			
		};
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("newpass")).andReturn("encryptednewpass");
		RegisteredUser userOne = new RegisteredUserBuilder().firstName("a").lastName("o").email("two").password("12").newPassword("newpass").toUser();
		
		
		EasyMock.replay(encryptionUtilsMock);

		userServiceWithCurrentUserOverride.updateCurrentUser(userOne);
		EasyMock.verify(encryptionUtilsMock);
		assertEquals("two", currentUser.getUsername());
		assertEquals("two", currentUser.getEmail());
		assertEquals("a", currentUser.getFirstName());
		assertEquals("o", currentUser.getLastName());
		assertEquals("encryptednewpass", currentUser.getPassword());
		
	}
	
	@Test
	public void shouldNotChangePassIfPasswordIsBlank(){
		final RegisteredUser currentUser = new RegisteredUserBuilder().password("12").email("em").username("em").toUser();
		userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock,userFactoryMock,
				mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock){
			
			@Override
			public RegisteredUser getCurrentUser() {
				return currentUser;
			}
			
		};
		RegisteredUser userOne = new RegisteredUserBuilder().username("one").email("two").password("").id(5).toUser();
		userServiceWithCurrentUserOverride.save(currentUser);
		userServiceWithCurrentUserOverride.updateCurrentUser(userOne);
		assertEquals("two", currentUser.getUsername());
		assertEquals("two", currentUser.getEmail());
		assertEquals("12", currentUser.getPassword());
		
	}
	
	
	@Before
	public void setUp() {
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		mailsenderMock = EasyMock.createMock(JavaMailSender.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		currentUser = new RegisteredUserBuilder().id(8).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		userDAOMock = EasyMock.createMock(UserDAO.class);		
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		userFactoryMock = EasyMock.createMock(UserFactory.class);
		msgSourceMock = EasyMock.createMock(MessageSource.class);
		
		userService = new UserService(userDAOMock, roleDAOMock,userFactoryMock,  mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock);
		userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock,userFactoryMock,
				mimeMessagePreparatorFactoryMock, mailsenderMock, msgSourceMock, encryptionUtilsMock){

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
