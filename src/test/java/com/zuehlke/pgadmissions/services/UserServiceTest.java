package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
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

	@Test
	public void shouldGetUserFromDAO() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userDAOMock.get(1)).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUser(1));
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

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveRefereeAndSendEmailToReferee() throws UnsupportedEncodingException {

		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();

		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("applicant").lastName("hen").email("applicant@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).id(2).program(program).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).toReferee();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).referees(referee).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		refereeUser.setCurrentReferee(referee);
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		userService.save(refereeUser);

		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("hh@test.com", "harry hen");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Referee Registration"),
						EasyMock.eq("private/referees/mail/register_referee_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		mailsenderMock.send(preparatorMock1);

		EasyMock.replay(mimeMessagePreparatorFactoryMock, mailsenderMock);

		userService.saveAndEmailRegisterConfirmationToReferee(refereeUser);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, mailsenderMock);
	}

	@Test
	public void shouldNotSendEmailIfSaveFails() throws UnsupportedEncodingException {
		userService.save(null);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(mimeMessagePreparatorFactoryMock, mailsenderMock);
		try {
			userService.saveAndEmailRegisterConfirmationToReferee(null);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(mimeMessagePreparatorFactoryMock, mailsenderMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();

		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("applicant").lastName("hen").email("applicant@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).id(2).program(program).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).toReferee();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).referees(referee).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		refereeUser.setCurrentReferee(referee);
		ProgrammeDetails programmeDetails = new ProgrammeDetails();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		userService.save(refereeUser);

		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("hh@test.com", "harry hen");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Referee Registration"),
						EasyMock.eq("private/referees/mail/register_referee_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);

		mailsenderMock.send(preparatorMock1);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(mimeMessagePreparatorFactoryMock, mailsenderMock);
		userService.saveAndEmailRegisterConfirmationToReferee(refereeUser);

		EasyMock.verify(mimeMessagePreparatorFactoryMock);

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
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
		assertTrue(selectedUser.isInRole(Authority.ADMINISTRATOR));
	}	
	
	@Test
	public void shouldAddUserRoleApproverIfNotAlreadyApproverAndAproverInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
		assertTrue(selectedUser.isInRole(Authority.APPROVER));
	}

	@Test
	public void shouldAddUserRoleReviewerIfNotAlreadyRevieweAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
		assertTrue(selectedUser.isInRole(Authority.REVIEWER));
	}
	
	@Test
	public void shouldAddUserRoleInterviewerIfNotAlreadyInterviewerAndRevieweInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);
		assertTrue(selectedUser.isInRole(Authority.INTERVIEWER));
	}
	@Test
	public void shouldAddUserRoleSuperAdmimnIfNotAlreadySuperadminAndSuperadminInNewRoles() {
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();
		Program selectedProgram = new ProgramBuilder().id(4).toProgram();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.SUPERADMINISTRATOR);
		assertTrue(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
		EasyMock.verify(roleDAOMock);
	}
	
	@Test
	public void shouldRemoveSuperadminRoleIfNotInNewListAndUserIsSuperadmin() {
		EasyMock.expect(currentUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
		EasyMock.replay(currentUserMock);
		Role role= new RoleBuilder().id(1).authorityEnum(Authority.SUPERADMINISTRATOR).toRole();
		RegisteredUser selectedUser = new RegisteredUserBuilder().role(role).id(1).toUser();		
	
		Program selectedProgram = new ProgramBuilder().id(3).toProgram();
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram);
		assertFalse(selectedUser.isInRole(Authority.SUPERADMINISTRATOR));
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
	public void shouldAddProgramToAdminlistIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();			
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.ADMINISTRATOR);
		assertTrue(selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}
	
	@Test
	public void shouldAddProgramToApproverlistIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPROVER)).andReturn(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.APPROVER);
		assertTrue(selectedUser.getProgramsOfWhichApprover().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}
	
	
	@Test
	public void shouldAddProgramToReviewerListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();		
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REVIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.REVIEWER);
		assertTrue(selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram));
		EasyMock.verify(roleDAOMock);
	}
	
	@Test
	public void shouldAddProgramToInterviewerListIfNew(){
		Program selectedProgram = new ProgramBuilder().id(1).toProgram();
		RegisteredUser selectedUser = new RegisteredUserBuilder().id(1).toUser();	
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.INTERVIEWER)).andReturn(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole());
		EasyMock.replay(roleDAOMock);
		userServiceWithCurrentUserOverride.updateUserWithNewRoles(selectedUser, selectedProgram, Authority.INTERVIEWER);
		assertTrue(selectedUser.getProgramsOfWhichInterviewer().contains(selectedProgram));
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
	public void shouldThrowISEwhenUserAlreadyExists() {
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
		EasyMock.expect(userFactoryMock.createNewUserInRoles("la", "le", "some@email.com",Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER)).andReturn(newUser);
		userDAOMock.save(newUser);
		EasyMock.expectLastCall().andDelegateTo(new CheckProgrammeAndSimulateSaveDAO(program));

		EasyMock.replay(userDAOMock, roleDAOMock, userFactoryMock);
		RegisteredUser newReviewer = userService.createNewUserForProgramme( "la", "le", "some@email.com", program, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER);

		EasyMock.verify(userDAOMock, roleDAOMock, userFactoryMock);
		assertEquals(newUser, newReviewer);				
		assertTrue(newReviewer.getProgramsOfWhichAdministrator().contains(program));
		assertTrue(newReviewer.getProgramsOfWhichApprover().contains(program));
		assertTrue(newReviewer.getProgramsOfWhichInterviewer().contains(program));
		assertTrue(newReviewer.getProgramsOfWhichReviewer().contains(program));
		
	}
	@Before
	public void setUp() {
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
		userService = new UserService(userDAOMock, roleDAOMock,userFactoryMock,  mimeMessagePreparatorFactoryMock, mailsenderMock);
		userServiceWithCurrentUserOverride = new UserService(userDAOMock, roleDAOMock,userFactoryMock,  mimeMessagePreparatorFactoryMock, mailsenderMock){

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
