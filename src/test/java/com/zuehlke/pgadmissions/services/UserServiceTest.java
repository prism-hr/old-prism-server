package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;

public class UserServiceTest {

	private UserDAO userDAOMock;
	private RegisteredUser user;
	private UserService userService;
	private RoleDAO roleDAOMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;
	private JavaMailSender mailsenderMock;

	@Test
	public void shouldGetUserFromDAO() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userDAOMock.get(1)).andReturn(user);
		EasyMock.replay(userDAOMock);
		assertEquals(user, userService.getUser(1));
	}

	@Test
	public void shouldgetListOfReviewersForApplication() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("tom").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> reviewersForApplication = userService.getReviewersForApplication(form);
		Assert.assertTrue(reviewersForApplication.contains(reviewer));
		Assert.assertEquals(1, reviewersForApplication.size());
	}

	@Test
	public void shouldgetEmptyListOfReviewersForApplication() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("tom").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();

		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);

		ApplicationForm form = new ApplicationFormBuilder().id(1).reviewers(reviewers).toApplicationForm();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userDAOMock);

		List<RegisteredUser> reviewersForApplication = userService.getReviewersForApplication(form);

		Assert.assertFalse(reviewersForApplication.contains(reviewer));
		Assert.assertEquals(0, reviewersForApplication.size());
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
		
		List<RegisteredUser>users = userService.getUsersInRole(auth);
		assertEquals(2, users.size());
		assertTrue(users.containsAll(Arrays.asList(userOne, userTwo)));
	}

	@Test
	public void shouldGetSuperAdministrators(){
		Role superAdminRole = new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).id(1).toRole();		
		
		RegisteredUser superAdmin1 = new RegisteredUserBuilder().id(2).role(superAdminRole).toUser();
		RegisteredUser superAdmin2 = new RegisteredUserBuilder().id(3).roles(superAdminRole).toUser();
		
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).andReturn(superAdminRole);
		EasyMock.replay(roleDAOMock);
		EasyMock.expect(userDAOMock.getUsersInRole(superAdminRole)).andReturn(Arrays.asList(superAdmin1, superAdmin2));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> superAdmins = userService.getSuperAdmins();
		assertEquals(2, superAdmins.size());
		assertTrue(superAdmins.containsAll(Arrays.asList(superAdmin1, superAdmin2)));
	}
	
	@Test
	public void shouldGetAllUsersForProgram(){
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
	public void shouldGetAllInternalUsers(){
		final RegisteredUser userOne = new RegisteredUserBuilder().id(1).toUser();
		final RegisteredUser userTwo = new RegisteredUserBuilder().id(2).toUser();
		final RegisteredUser userThree = new RegisteredUserBuilder().id(3).toUser();
		final RegisteredUser userFour = new RegisteredUserBuilder().id(4).toUser();
		final RegisteredUser userFive = new RegisteredUserBuilder().id(5).toUser();
		userService = new UserService(userDAOMock, roleDAOMock, mimeMessagePreparatorFactoryMock, mailsenderMock){

			@SuppressWarnings("unchecked")
			@Override
			public List<RegisteredUser> getUsersInRole(Authority auth) {
				if(auth == Authority.ADMINISTRATOR){
					return Arrays.asList(userOne, userTwo);
				}
				if(auth == Authority.APPROVER){
					 return Arrays.asList(userTwo, userThree);
				}
				if(auth == Authority.REVIEWER){
					 return Arrays.asList(userThree, userFour);
				}
				if(auth == Authority.SUPERADMINISTRATOR){
					 return Arrays.asList(userFour, userOne);
				}
				if(auth == Authority.APPLICANT){
					 return Arrays.asList(userFour, userFive);
				}
				return Collections.EMPTY_LIST;
			}
			
		};
		

		List<RegisteredUser> internalUsers= userService.getAllInternalUsers();
	
		assertEquals(4, internalUsers.size());
		assertTrue(internalUsers.containsAll(Arrays.asList(userOne, userTwo, userThree, userFour)));
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
	public void shouldSaveRefereeAndSendEmailToReferee() throws UnsupportedEncodingException{
	
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("applicant").lastName("hen").email("applicant@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).id(2).project(project).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).toReferee();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).referee(referee).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ProgrammeDetail programmeDetails = new ProgrammeDetail();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		userService.save(refereeUser);
		
		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("hh@test.com", "harry hen");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Referee Registration"),EasyMock.eq("private/referees/mail/register_referee_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
		mailsenderMock.send(preparatorMock1);
	
		EasyMock.replay(mimeMessagePreparatorFactoryMock, mailsenderMock);
	
		
		userService.saveAndEmailReferee(refereeUser);
		EasyMock.verify(mimeMessagePreparatorFactoryMock, mailsenderMock);
	}
	
	@Test
	public void shouldNotSendEmailIfSaveFails() throws UnsupportedEncodingException {
		userService.save(null);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(mimeMessagePreparatorFactoryMock, mailsenderMock);
		try {
			userService.saveAndEmailReferee(null);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(mimeMessagePreparatorFactoryMock, mailsenderMock);
	}
	
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").toUser();
		Program program = new ProgramBuilder().administrators(administrator).toProgram();
		Project project = new ProjectBuilder().program(program).toProject();
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("applicant").lastName("hen").email("applicant@test.com").toUser();
		ApplicationForm form = new ApplicationFormBuilder().applicant(applicant).id(2).project(project).toApplicationForm();
		Referee referee = new RefereeBuilder().application(form).toReferee();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(2).referee(referee).firstName("harry").lastName("hen").email("hh@test.com").toUser();
		ProgrammeDetail programmeDetails = new ProgrammeDetail();	
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		userService.save(refereeUser);

		MimeMessagePreparator preparatorMock1 = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress1 = new InternetAddress("hh@test.com", "harry hen");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress1), EasyMock.eq("Referee Registration"),EasyMock.eq("private/referees/mail/register_referee_confirmation.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock1);
	
		mailsenderMock.send(preparatorMock1);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(mimeMessagePreparatorFactoryMock, mailsenderMock);
		userService.saveAndEmailReferee(refereeUser);

		EasyMock.verify(mimeMessagePreparatorFactoryMock);

	}
	
	@Before
	public void setUp() {
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		mailsenderMock =EasyMock.createMock(JavaMailSender.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		userService = new UserService(userDAOMock, roleDAOMock, mimeMessagePreparatorFactoryMock, mailsenderMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
