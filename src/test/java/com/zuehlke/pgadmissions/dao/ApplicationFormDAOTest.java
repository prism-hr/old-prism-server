package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase {

	private ApplicationFormDAO applicationDAO;
	private RegisteredUser user;
	private Program program;

	private ApplicationForm application;

	@Before
	public void setup() {
		applicationDAO = new ApplicationFormDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);

		flushAndClearSession();
	}

	@Test(expected = NullPointerException.class)
	public void shouldSendNullPointerException() {
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationFormDAO.save(applicationForm);
	}

	@Test
	public void shouldSaveAndLoadApplication() throws Exception {

		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);

		application.setApplicant(user);

		assertNull(application.getId());

		applicationDAO.save(application);

		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = applicationDAO.get(id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = applicationDAO.get(id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);
		assertEquals(application.getApplicant(), user);
	}

	
	
	@Test
	public void shouldFindAllQualificationsBelongingToSameApplication() throws ParseException {
		List<Qualification> qualifications = getQualificationsBelongingToSameApplication();
		applicationDAO.save(application);
		flushAndClearSession();
		List<Qualification> qualificationsByApplication = applicationDAO.getQualificationsByApplication(application);
		assertNotSame(qualifications, qualificationsByApplication);
		assertEquals(qualifications.get(0).getApplication(), qualifications.get(1).getApplication());
	}

	@Test
	public void shouldAssignDateToApplicationForm() {
		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);

		applicationDAO.save(application);

		Integer id = application.getId();
		ApplicationForm reloadedApplication = applicationDAO.get(id);
		assertNotNull(reloadedApplication.getApplicationTimestamp());

	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderMoreThanAWeekAgo() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date eightDaysAgo = DateUtils.addDays(today, -8);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(eightDaysAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithNoReminder() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(oneMonthAgo).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationsNotInValidationStage() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date eightDaysAgo = DateUtils.addDays(today, -8);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.APPROVED)
				.validationDueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(eightDaysAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationaInValidationStageButNotOverDue() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekInFuture = DateUtils.addWeeks(today, 1);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(oneWeekInFuture).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationaInValidationStageWitDueDateToday() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(today).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnOverDueApplicationInValidationStageWithReminderSixDaysWeekAgo() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date sixDaysAgo = DateUtils.addDays(today, -6);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(sixDaysAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderOneWeekAndFiveMinAgo() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekAgo = DateUtils.addWeeks(now, -1);
		Date oneWeekAgoAndFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneWeekAgoAndFiveMinAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderOneWeekMinusFiveMinAgo() {
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekAgo = DateUtils.addWeeks(now, -1);
		Date oneWeekAgoAndFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.validationDueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneWeekAgoAndFiveMinAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnApplicationUpdatedSinceLastAlertAndLastAlertMoreThan24HoursAgo() {
		Date now = Calendar.getInstance().getTime();
		Date twentyFiveHoursAgo = DateUtils.addHours(now, -25);
		Date twelveHoursAgo = DateUtils.addHours(now, -12);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION)
				.notificationDate(twentyFiveHoursAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).lastUpdated(twelveHoursAgo)
				.status(ApplicationFormStatus.VALIDATION).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertTrue(applicationsDueUpdateNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationUpdatedSinceLastAlertAndLastAlertLessThan24HoursAgo() {
		Date now = Calendar.getInstance().getTime();
		Date twentyThreeHoursAgo = DateUtils.addHours(now, -23);
		Date twelveHoursAgo = DateUtils.addHours(now, -12);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION)
				.notificationDate(twentyThreeHoursAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).lastUpdated(twelveHoursAgo)
				.status(ApplicationFormStatus.VALIDATION).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertFalse(applicationsDueUpdateNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationNotUpdatedSinceLastAlert() {
		Date now = Calendar.getInstance().getTime();
		Date twentyFiveHoursAgo = DateUtils.addHours(now, -25);
		Date twentySevenHoursAgo = DateUtils.addHours(now, -27);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION)
				.notificationDate(twentyFiveHoursAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).lastUpdated(twentySevenHoursAgo)
				.status(ApplicationFormStatus.VALIDATION).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertFalse(applicationsDueUpdateNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormDueReviewNotification() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.events(new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertTrue(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	
	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfWithdrawn() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.WITHDRAWN)
				.events(new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	
	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfRejected() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REJECTED)
				.events(new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	
	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfApproved() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED)
				.events(new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	@Test
	public void shouldNotReturnApplicationFormForReviewNotificationIfNotifiedSinceOnlyEvent() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION)
				.notificationDate(fiveMinutesAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.events(new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	@Test
	public void shouldNotReturnApplicationFormForReviewNotificationIfNotifiedSinceLastEvent() {		
		Date now = Calendar.getInstance().getTime();
		Date twentyMinutesAgo = DateUtils.addMinutes(now, -20);		
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION)
				.notificationDate(fiveMinutesAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.events(new EventBuilder().date(twentyMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent(), new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	@Test
	public void shouldReturnApplicationFormForReviewNotificationIfEventSinceLastNotified() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		Date twoMinutesAgo = DateUtils.addMinutes(now, -2);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION)
				.notificationDate(fiveMinutesAgo).toNotificationRecord();
		Event firstEvent = new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event lastEvent = new EventBuilder().date(twoMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.events(firstEvent, lastEvent).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertTrue(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}
	
	
	@Test
	public void shouldReturnApplicationFormDueApplicantSubmissionNotification() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.events(new EventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.VALIDATION).toEvent()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_SUBMISSION_NOTIFICATION, ApplicationFormStatus.VALIDATION);
		assertTrue(applications.contains(applicationForm));

	}
	

	@Test
	public void shouldReturnAllSubmittedApplicationsForSuperAdmin(){
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		save(applicationFormOne, applicationFormTwo);
		flushAndClearSession();
		RegisteredUser superAdmin = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(superAdmin);
		assertTrue(applications.contains(applicationFormOne));
		assertFalse(applications.contains(applicationFormTwo));				
	}
	
	@Test
	public void shouldReturnOwnApplicationsIfApplicant(){
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
				
		save(applicant, applicationFormOne);	
		flushAndClearSession();
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);
		assertTrue(applications.contains(applicationFormOne));
						
	}
	@Test
	public void shouldNotReturnApplicationsByOtherApplicant(){
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
				
		save(applicant, applicationFormOne);	
		flushAndClearSession();
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);
		assertFalse(applications.contains(applicationFormOne));
						
	}
	@Test
	public void shouldReturnAllSubmittedApplicationsInProgramForAdmin(){		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		RegisteredUser admin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();

		save(admin);
		
		save(applicationFormOne, applicationFormTwo);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(admin);
		assertTrue(applications.contains(applicationFormOne));
		assertFalse(applications.contains(applicationFormTwo));
				
	}
	
	@Test
	public void shouldReturnNotReturnApplicationsSubmittedToOtherProgramsForAdmin(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();		
		RegisteredUser admin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();

		save(admin);
		
		save(applicationFormOne);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(admin);
		
		assertFalse(applications.contains(applicationFormOne));
				
	}

	@Test
	public void shouldReturnAppsOfWhichReviewerEvenIfNotCurrentlyInRole(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();			
		save(applicationForm, reviewerUser);
		Reviewer reviewer = new ReviewerBuilder().application(applicationForm).user(reviewerUser).toReviewer();
		applicationForm.getReviewers().add(reviewer);
		save(applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
		assertTrue(applications.contains(applicationForm));		
				
	}	
	@Test
	public void shouldNotReturnAppsOfWhichReviewerNotInReviewState(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();			
		save(applicationForm, reviewerUser);
		Reviewer reviewer = new ReviewerBuilder().application(applicationForm).user(reviewerUser).toReviewer();
		applicationForm.getReviewers().add(reviewer);
		save(applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
		assertFalse(applications.contains(applicationForm));		
				
	}	
	
	@Test
	public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichReviewerIfAdminAndReviewer(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		RegisteredUser reviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();			
		save(applicationFormOne, reviewerAndAdminUser);
		Reviewer reviewer = new ReviewerBuilder().application(applicationFormOne).user(reviewerAndAdminUser).toReviewer();
		applicationFormOne.getReviewers().add(reviewer);
		save(applicationFormOne);
		
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(applicationFormTwo);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerAndAdminUser);
		
		assertTrue(applications.contains(applicationFormOne));
		assertTrue(applications.contains(applicationFormTwo));
				
	}
	
	@Ignore
	@Test
	public void shouldReturnAppsOfWhichInterviewerEvenIfNotCurrentlyInRole(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();			
		save(applicationForm, interviewerUser);
		Interviewer interviewer = new InterviewerBuilder().application(applicationForm).user(interviewerUser).toInterviewer();
		applicationForm.getCurrentInterview().getInterviewers().add(interviewer);
		save(applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
		assertTrue(applications.contains(applicationForm));		
				
	}
	
	@Ignore
	@Test
	public void shouldNotReturnAppsOfWhichInterviewerNotInInterviewState(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();			
		save(applicationForm, interviewerUser);
		Interviewer interviewer = new InterviewerBuilder().application(applicationForm).user(interviewerUser).toInterviewer();
		applicationForm.getCurrentInterview().getInterviewers().add(interviewer);
		save(applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
		assertFalse(applications.contains(applicationForm));		
				
	}	
	
	@Ignore
	@Test
	public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichInterviewerIfAdminAndInterviewer(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);

		RegisteredUser interviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();			
		save(applicationFormOne, interviewerAndAdminUser);
		Interviewer interviewer = new InterviewerBuilder().application(applicationFormOne).user(interviewerAndAdminUser).toInterviewer();
		applicationFormOne.getCurrentInterview().getInterviewers().add(interviewer);
		save(applicationFormOne);
		
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(applicationFormTwo);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerAndAdminUser);
		
		assertTrue(applications.contains(applicationFormOne));
		assertTrue(applications.contains(applicationFormTwo));
				
	}
	

	@Test
	public void shouldReturnApplicationsInProgramForApprover(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		
		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();

		save(approver, applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approver);
		assertTrue(applications.contains(applicationForm));
	}
	
	@Test
	public void shouldNotReturnApplicationsInProgramForApproverInNotInApproval(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		
		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();

		save(approver, applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approver);
		assertFalse(applications.contains(applicationForm));
		
	}
	
	@Test
	public void shouldReturnAppsSubmittedToUsersProgramsAsAdminAndAprrover(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		RegisteredUser approverAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).programsOfWhichApprover(otherProgram).toUser();
		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(approverAndAdminUser, applicationFormOne,applicationFormTwo);

		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approverAndAdminUser);
		
		assertTrue(applications.contains(applicationFormOne));
		assertTrue(applications.contains(applicationFormTwo));
				
	}
	public List<ApplicationForm> getApplicationFormsBelongingToSameUser() {
		List<ApplicationForm> applications = new ArrayList<ApplicationForm>();

		ApplicationForm application1 = new ApplicationForm();
		application1.setApplicant(user);
		application1.setProgram(program);

		applicationDAO.save(application1);

		applications.add(application1);

		ApplicationForm application2 = new ApplicationForm();
		application2.setApplicant(user);
		application2.setProgram(program);

		applicationDAO.save(application2);

		applications.add(application2);

		flushAndClearSession();

		return applications;
	}

	public List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException {

		application = new ApplicationForm();
		application.setApplicant(user);
		application.setProgram(program);

		List<Qualification> qualifications = new ArrayList<Qualification>();

		Qualification qualification1 = new Qualification();
		qualification1.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualificationGrade("");
		qualification1.setQualificationInstitution("");

		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		qualification1.setQualificationLanguage(languageDAO.getLanguageById(1));
		qualification1.setQualificationSubject("");
		qualification1.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualificationType("");

		qualifications.add(qualification1);

		Qualification qualification2 = new Qualification();
		qualification2.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualificationGrade("");
		qualification2.setQualificationInstitution("");
		qualification2.setQualificationLanguage(languageDAO.getLanguageById(1));
		qualification2.setQualificationSubject("");
		qualification2.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualificationType("");

		qualifications.add(qualification1);
		return qualifications;
	}

}
