package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class RegisteredUserMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadUserWithSimpleValues() throws Exception {

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).originalApplicationQueryString("?hi&hello").toUser();

		assertNull(user.getId());

		sessionFactory.getCurrentSession().save(user);

		assertNotNull(user.getId());
		Integer id = user.getId();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertSame(user, reloadedUser);

		flushAndClearSession();

		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertNotSame(user, reloadedUser);
		assertEquals(user, reloadedUser);
		assertEquals(user.getPassword(), reloadedUser.getPassword());
		assertEquals(user.getUsername(), reloadedUser.getUsername());
		assertEquals(user.getFirstName(), reloadedUser.getFirstName());
		assertEquals(user.getLastName(), reloadedUser.getLastName());
		assertEquals(user.getEmail(), reloadedUser.getEmail());
		assertEquals("?hi&hello", reloadedUser.getOriginalApplicationQueryString());
		assertFalse(reloadedUser.isAccountNonExpired());
		assertFalse(reloadedUser.isAccountNonLocked());
		assertFalse(reloadedUser.isCredentialsNonExpired());
		assertFalse(reloadedUser.isEnabled());

	}
	

	@Test
	public void shouldLoadRegisteredUserWithReferees() {
		
		Referee referee1 = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").phoneNumber("hallihallo").toReferee();
		Referee referee2 = new RefereeBuilder().id(5).firstname("ref1").lastname("erre1").email("ref1@test.com").phoneNumber("hallihallo").toReferee();
		

		RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").toUser();
		
		sessionFactory.getCurrentSession().save(admin1);
		flushAndClearSession();
		referee1.setUser(admin1);
		referee2.setUser(admin1);

		save(referee1, referee2);

		RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin1.getId()));
		
		assertTrue(reloadedUser.getReferees().contains(referee1));
		assertTrue(reloadedUser.getReferees().contains(referee2));
		assertNotNull(referee1.getUser());

	}

	@Test
	public void shouldSaveAndLoadRegisteredUserWithReferee() {


		RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").toUser();

		flushAndClearSession();
		assertNull(admin1.getId());

		sessionFactory.getCurrentSession().save(admin1);

		Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("ref@test.com").user(admin1).phoneNumber("hallihallo").toReferee();
		save(referee);
		assertNotNull(admin1.getId());
		Integer id = admin1.getId();
		RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id));
		
		assertSame(admin1, reloadedUser);

		flushAndClearSession();

		
		reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id));
		
		assertNotSame(admin1, reloadedUser);
		assertEquals(admin1, reloadedUser);

		assertEquals("email", reloadedUser.getUsername());
		Assert.assertNotNull(reloadedUser.getReferees());
		Assert.assertTrue(reloadedUser.getReferees().contains(referee));
	}
	
	



	@Test
	public void shouldSaveAndLoadUserWithRoles() throws Exception {

		// clear out whatever test data is in there -remember, it will all be
		// rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();

		Role roleOne = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		Role roleTwo = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		save(roleOne, roleTwo);
		flushAndClearSession();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleOne).role(roleTwo).toUser();

		sessionFactory.getCurrentSession().save(user);

		Integer id = user.getId();
		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);

		assertEquals(2, reloadedUser.getRoles().size());

		assertTrue(reloadedUser.getRoles().containsAll(Arrays.asList(roleOne, roleTwo)));

	}

	@Test
	public void shouldDeleteRoleMappingWhenDeletingUser() throws Exception {
		// clear out whatever test data is in there -remember, it will all be
		// rolled back!
		sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		save(role);
		Integer roleId = role.getId();
		flushAndClearSession();

		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(role).toUser();

		sessionFactory.getCurrentSession().save(user);

		Integer id = user.getId();

		flushAndClearSession();
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
		assertEquals(BigInteger.valueOf(1),
				sessionFactory.getCurrentSession().createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = " + roleId).uniqueResult());
		sessionFactory.getCurrentSession().delete(reloadedUser);
		flushAndClearSession();

		assertEquals(BigInteger.valueOf(0),
				sessionFactory.getCurrentSession().createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = " + roleId).uniqueResult());

	}
	
	@Test
	public void shouldSaveAndLoadProgramsOfWhichAdministrator() throws Exception {

		Program program = new ProgramBuilder().code("111111").title("hello").toProgram();
		save(program);
		flushAndClearSession();

		RegisteredUser admin = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(admin);

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichAdministrator().size());
		assertTrue(reloadedUser.getProgramsOfWhichAdministrator().containsAll(Arrays.asList(program)));

	}

	@Test
	public void shouldLoadProgramsOfWhichApprover() throws Exception {

		Program program = new ProgramBuilder().code("111111").title("hello").toProgram();
		save(program);
		flushAndClearSession();

		RegisteredUser approver = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(approver);

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, approver.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichApprover().size());
		assertTrue(reloadedUser.getProgramsOfWhichApprover().containsAll(Arrays.asList(program)));

	}

	@Test
	public void shouldSaveAndLoadProgramsOfWhichReviewer() throws Exception {

		Program program = new ProgramBuilder().code("111111").title("hello").toProgram();
		save(program);
		flushAndClearSession();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(reviewer);

		flushAndClearSession();

		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, reviewer.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichReviewer().size());
		assertTrue(reloadedUser.getProgramsOfWhichReviewer().containsAll(Arrays.asList(program)));
		
		Program reloadedProgram = (Program) sessionFactory.getCurrentSession().get(Program.class, program.getId());
		assertTrue(reloadedProgram.getProgramReviewers().contains(reloadedUser));
	}
	
	
	@Test
	public void shouldSaveAndLoadProgramsOfWhichInterviewer() throws Exception {
		
		Program program = new ProgramBuilder().code("111111").title("hello").toProgram();
		save(program);
		flushAndClearSession();
		
		RegisteredUser interviewer = new RegisteredUserBuilder().programsOfWhichInterviewer(program).firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username10").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();
		
		save(interviewer);
		
		flushAndClearSession();
		
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, interviewer.getId());
		assertEquals(1, reloadedUser.getProgramsOfWhichInterviewer().size());
		assertTrue(reloadedUser.getProgramsOfWhichInterviewer().containsAll(Arrays.asList(program)));
		
	}
	
	
	@Test
	public void shouldLoadRegisteredUserWithComments() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com").toUser();
		
		sessionFactory.getCurrentSession().save(applicant);
		
		ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).id(1).toApplicationForm();
		
		sessionFactory.getCurrentSession().save(application);
		
		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).adminsNotified(false).comment("This is a review comment").commentType(CommentType.REVIEW).toReviewComment();
		Comment comment1 = new CommentBuilder().id(1).application(application).comment("This is another generic Comment").toComment();
		
		RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").toUser();
		
		sessionFactory.getCurrentSession().save(admin1);
		flushAndClearSession();
		comment.setUser(admin1);
		reviewComment.setUser(admin1);
		comment1.setUser(admin1);

		save(comment, comment1, reviewComment);

		RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin1.getId()));
		
		assertEquals(3, reloadedUser.getComments().size());
		assertTrue(reloadedUser.getComments().contains(comment));
		assertTrue(reloadedUser.getComments().contains(comment1));
		assertTrue(reloadedUser.getComments().contains(reviewComment));
		assertNotNull(comment.getUser());
		assertNotNull(comment1.getUser());
		assertNotNull(reviewComment.getUser());

	}
	
	@Test
	public void shouldSaveAndLoadNotificationRecordsWithUser() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		NotificationRecord recordOne = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("01 12 2011 14:09:26")).notificationType(NotificationType.APPLICANT_SUBMISSION_NOTIFICATION).toNotificationRecord();
		NotificationRecord recordTwo = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("03 12 2011 14:09:26")).notificationType(NotificationType.REFEREE_RESPONDED_NOTIFICATION).toNotificationRecord();
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).notificationRecords(recordOne, recordTwo).toUser();
		
		save(user);
		Integer recordOneId = recordOne.getId();
		assertNotNull(recordOneId);
		assertNotNull(recordTwo.getId());
		flushAndClearSession();
		
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, user.getId());
		assertEquals(2, reloadedUser.getNotificationRecords().size());
		assertTrue(reloadedUser.getNotificationRecords().containsAll(Arrays.asList(recordOne, recordTwo)));
		
		recordOne = (NotificationRecord) sessionFactory.getCurrentSession().get(NotificationRecord.class, recordOneId);
		reloadedUser.getNotificationRecords().remove(recordOne);
		save(reloadedUser);
		flushAndClearSession();
		
		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, user.getId());
		assertEquals(1, reloadedUser.getNotificationRecords().size());
		assertTrue(reloadedUser.getNotificationRecords().containsAll(Arrays.asList(recordTwo)));
		
		assertNull(sessionFactory.getCurrentSession().get(NotificationRecord.class, recordOneId));

	}
	
	@Test
	public void shouldSaveAndLoadPendingRoleNotificationsWithUser() throws ParseException {
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
		Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		save(program);
		
		PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).toPendingRoleNotification();
		PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).toPendingRoleNotification();
		
		
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo).toUser();
		
		save(user);
		Integer recordOneId = pendingOne.getId();
		assertNotNull(recordOneId);
		assertNotNull(pendingTwo.getId());
		flushAndClearSession();
		
		RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, user.getId());
		assertEquals(2, reloadedUser.getPendingRoleNotifications().size());
		assertTrue(reloadedUser.getPendingRoleNotifications().containsAll(Arrays.asList(pendingOne, pendingTwo)));
		
		pendingOne = (PendingRoleNotification) sessionFactory.getCurrentSession().get(PendingRoleNotification.class, recordOneId);
		reloadedUser.getPendingRoleNotifications().remove(pendingOne);
		save(reloadedUser);
		flushAndClearSession();
		
		reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, user.getId());
		assertEquals(1, reloadedUser.getPendingRoleNotifications().size());
		assertTrue(reloadedUser.getPendingRoleNotifications().containsAll(Arrays.asList(pendingTwo)));
		
		assertNull(sessionFactory.getCurrentSession().get(PendingRoleNotification.class, recordOneId));


	}
}
