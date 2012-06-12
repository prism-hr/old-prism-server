package com.zuehlke.pgadmissions.dao;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
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

		ApplicationForm inApplication = new ApplicationForm();
		inApplication.setProgram(program);

		inApplication.setApplicant(user);

		assertNull(inApplication.getId());

		applicationDAO.save(inApplication);

		assertNotNull(inApplication.getId());
		Integer id = inApplication.getId();
		ApplicationForm reloadedApplication = applicationDAO.get(id);
		assertSame(inApplication, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = applicationDAO.get(id);
		assertNotSame(inApplication, reloadedApplication);
		assertEquals(inApplication, reloadedApplication);
		assertEquals(inApplication.getApplicant(), user);
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
		ApplicationForm inApplication = new ApplicationForm();
		inApplication.setProgram(program);
		inApplication.setApplicant(user);

		applicationDAO.save(inApplication);

		Integer id = inApplication.getId();
		ApplicationForm reloadedApplication = applicationDAO.get(id);
		assertNotNull(reloadedApplication.getApplicationTimestamp());

	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderMoreThanAWeekAgoReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date eightDaysAgo = DateUtils.addDays(now, -8);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(eightDaysAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithNoReminderForOneWeekReminder() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationsNotInValidationStageForOneWeekReminder() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date eightDaysAgo = DateUtils.addDays(today, -8);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.APPROVED)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(eightDaysAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationaInValidationStageButNotOverDueForOneWeekReminder() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekInFuture = DateUtils.addWeeks(today, 1);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneWeekInFuture).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationaInValidationStageWitDueDateTodayForAReminderOfOneWeek() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
				.dueDate(now).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnOverDueApplicationInValidationStageWithReminderSixDaysWeekAgo() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date sixDaysAgo = DateUtils.addDays(today, -6);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(sixDaysAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderOneWeekAndFiveMinAgoForAReminderOfOneWeek() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date oneWeekAgo = DateUtils.addWeeks(now, -1);
		Date oneWeekAgoAndFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneWeekAgoAndFiveMinAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminder1HourAgoForAReminderWith30MinutesInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(30);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneDayAgo = DateUtils.addDays(now, -1);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.program(program)
		.applicant(user)
		.status(ApplicationFormStatus.VALIDATION)
		.dueDate(oneMonthAgo)
		.notificationRecords(
				new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneDayAgo)
				.toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}
	
	@Test
	public void shouldNotReturnOverDueApplicationInValidationStageWithReminder1HourAgoForAReminderWith2HoursInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(2);
		reminderInterval.setUnit(DurationUnitEnum.HOURS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date onehourAgo = DateUtils.addHours(now, -1);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.program(program)
		.applicant(user)
		.status(ApplicationFormStatus.VALIDATION)
		.dueDate(oneMonthAgo)
		.notificationRecords(
				new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(onehourAgo)
				.toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}
	
	@Test
	public void shouldNotReturnOverDueApplicationInValidationStageWithReminder1DayAgoForAReminderWith2DaysInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(2);
		reminderInterval.setUnit(DurationUnitEnum.DAYS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneDayAgo = DateUtils.addDays(now, -1);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.program(program)
		.applicant(user)
		.status(ApplicationFormStatus.VALIDATION)
		.dueDate(oneMonthAgo)
		.notificationRecords(
				new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneDayAgo)
				.toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}
	
	@Test
	public void shouldNotReturnOverDueApplicationInValidationStageWithReminder5MinutesAgoForAReminderWith10MinutesInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(10);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.program(program)
		.applicant(user)
		.status(ApplicationFormStatus.VALIDATION)
		.dueDate(oneMonthAgo)
		.notificationRecords(
				new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(fiveMinutesAgo)
				.toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}
	
	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminder10MinutesAgoForAReminderWith5MinutesInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(5);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.program(program)
		.applicant(user)
		.status(ApplicationFormStatus.VALIDATION)
		.dueDate(oneMonthAgo)
		.notificationRecords(
				new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(tenMinutesAgo)
				.toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}
	
	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminder1DayAgoForAReminderWith23HoursInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(23);
		reminderInterval.setUnit(DurationUnitEnum.HOURS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date oneDayAgo = DateUtils.addDays(now, -1);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.program(program)
		.applicant(user)
		.status(ApplicationFormStatus.VALIDATION)
		.dueDate(oneMonthAgo)
		.notificationRecords(
				new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneDayAgo)
				.toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}
	
	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderOneWeekMinusFiveMinAgoForOneWeekReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date oneWeekAgo = DateUtils.addWeeks(now, -1);
		Date oneWeekAgoAndFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).notificationDate(oneWeekAgoAndFiveMinAgo)
								.toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueAdminReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldReturnApplicationUpdatedSinceLastAlertAndLastAlertMoreThan24HoursAgoForOneWeekReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
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
	public void shouldNotReturnApplicationUpdatedSinceLastAlertAndLastAlertLessThan24HoursAgoFor1WeekReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
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
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
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
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
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
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormDueReviewNotificationIfRejectedAndNewStatusIsRejected() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REJECTED)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REJECTED).toEvent()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		
		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICATION_MOVED_TO_REJECT_NOTIFICATION, ApplicationFormStatus.REJECTED);
		assertTrue(applicationsDueApplicantReviewNotification.contains(applicationForm));
		
	}
	
	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfApproved() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).toApplicationForm();
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
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).notificationRecords(lastNotificationRecord).toApplicationForm();
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
				.events(new StateChangeEventBuilder().date(twentyMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent(), new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent()).notificationRecords(lastNotificationRecord).toApplicationForm();
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
		StateChangeEvent firstEvent = new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event lastEvent = new StateChangeEventBuilder().date(twoMinutesAgo).newStatus(ApplicationFormStatus.REVIEW).toEvent();
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
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.VALIDATION).toEvent()).toApplicationForm();
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
	public void shouldReturnAllApplicationsRefereeIsAssignedTo(){
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("applicant").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
		save(applicant);
		Country country = new CountryBuilder().code("1").name("country").toCountry();
		save(country);
		RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
		save(refereeUser);
		Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(country).addressLocation("london").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumber("hallihallo").toReferee();
		save(referee);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).referees(referee).applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		
		save(applicationFormOne);	
		flushAndClearSession();
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
		assertTrue(applications.contains(applicationFormOne));
		
	}
	
	@Test
	public void shouldNotReturnApplicationsRefereeIsNotAssignedTo(){
		RoleDAO roleDAO = new RoleDAO(sessionFactory);
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("applicant").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
		save(applicant);
		Country country = new CountryBuilder().code("1").name("country").toCountry();
		save(country);
		RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
		save(refereeUser);
		Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(country).addressLocation("london").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumber("hallihallo").toReferee();
		save(referee);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		
		save(applicationFormOne);	
		flushAndClearSession();
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
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
	public void shouldReturnAppsOfWhichApplicationAdministrator(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		
		RegisteredUser applicationAdministrator = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
	
		applicationForm.setApplicationAdministrator(applicationAdministrator);
		
		save(applicationForm, applicationAdministrator);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicationAdministrator);
		assertTrue(applications.contains(applicationForm));
		
	}	
	@Test
	public void shouldNotReturnUbnsubmittedAppsOfWhichApplicationAdministrator(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		
		RegisteredUser applicationAdministrator = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
	
		applicationForm.setApplicationAdministrator(applicationAdministrator);
		
		save(applicationForm, applicationAdministrator);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicationAdministrator);
		assertFalse(applications.contains(applicationForm));
		
	}	
	
	@Test
	public void shouldReturnAppsOfWhichReviewerOfLatestReviewRound(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		
		RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).toReviewRound();
		applicationForm.setLatestReviewRound(reviewRound);
		
		save(applicationForm, reviewerUser,reviewer, reviewRound);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
		assertTrue(applications.contains(applicationForm));
		
	}	
	
	
	@Test
	public void shouldNotReturnAppsOfWhichReviewerOfPreviousReviewRound(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		
		RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).toReviewRound();
		applicationForm.getReviewRounds().add(reviewRound);
		
		save(reviewerUser, applicationForm, reviewerUser, reviewer, reviewRound);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
		assertFalse(applications.contains(applicationForm));		
				
	}	
	
	@Test	
	public void shouldNotReturnAppsOfWhichReviewerNotInReviewState(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		
		RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();
		
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).toReviewRound();
		applicationForm.setLatestReviewRound(reviewRound);
		
		save(applicationForm, reviewerUser,reviewer, reviewRound);
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
		
		Reviewer reviewer = new ReviewerBuilder().user(reviewerAndAdminUser).toReviewer();
		
		ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationFormOne).reviewers(reviewer).toReviewRound();
		applicationFormOne.setLatestReviewRound(reviewRound);
		
		save(reviewerAndAdminUser, applicationFormOne, reviewer, reviewRound);
		
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(applicationFormTwo);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerAndAdminUser);
		
		assertTrue(applications.contains(applicationFormOne));
		assertTrue(applications.contains(applicationFormTwo));
				
	}
	
	@Test
	public void shouldReturnAppsOfWhichInterviewerOfLatestInterview(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		
		RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();		

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		
		Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();
		
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		
		applicationForm.setLatestInterview(interview);
		
		save(applicationForm, interviewerUser, interviewer, interview);
		
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
		assertTrue(applications.contains(applicationForm));		
				
	}
	
	@Test
	public void shouldNotReturnAppsOfWhichInterviewerOfPreviousInterview(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		
		RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();		

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		
		Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();
		
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		
				
		save(applicationForm, interviewerUser, interviewer, interview);
	

		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
		assertFalse(applications.contains(applicationForm));		
				
	}
	@Test
	public void shouldNotReturnAppsOfWhichInterviewerNotInInterviewState(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);
		
		
		RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();		

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		
		Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();
		
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		
		applicationForm.setLatestInterview(interview);
		
		save(applicationForm, interviewerUser, interviewer, interview);
	
		
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
		assertFalse(applications.contains(applicationForm));		
				
	}	
	
	@Test
	public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichInterviewerIsAdminAndInterviewer(){
		Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(otherProgram);

		RegisteredUser interviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();

		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();		
		Interviewer interviewer = new InterviewerBuilder().user(interviewerAndAdminUser).toInterviewer();		
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		applicationFormOne.setLatestInterview(interview);		
		save(applicationFormOne, interviewerAndAdminUser, interviewer, interview);
		
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

	@Test
	public void shouldReturnApplicationsWithNoRejectNotificationDate() {
		BigInteger rejectedBigInt = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from APPLICATION_FORM where reject_notification_date IS NULL AND status = 'REJECTED'").uniqueResult();
		int numOfRejecteAppl = rejectedBigInt.intValue();

		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();

		ApplicationForm applicationForm = new ApplicationFormBuilder()//
				.program(program).applicant(user).status(ApplicationFormStatus.REVIEW)//
				.approver(approver).status(ApplicationFormStatus.REJECTED)//
				.toApplicationForm();

		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueRejectNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(numOfRejecteAppl + 1, applications.size());
		assertTrue(applications.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationsWithRejectNotificationDate() {
		BigInteger rejectedBigInt = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from APPLICATION_FORM where reject_notification_date IS NULL AND status = 'REJECTED'").uniqueResult();
		int numOfRejecteAppl = rejectedBigInt.intValue();

		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();

		ApplicationForm applicationForm = new ApplicationFormBuilder()//
				.program(program).applicant(user).status(ApplicationFormStatus.REVIEW)//
				.approver(approver).status(ApplicationFormStatus.REJECTED)//
				.toApplicationForm();

		applicationForm.setRejectNotificationDate(new Date());
		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueRejectNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(numOfRejecteAppl, applications.size());
	}
	
	@Test
	public void shouldReturnApplicationsWithNoApprovedNotificationRecord() {
		Integer noOfAppsBefore  = applicationDAO.getApplicationsDueApprovedNotifications().size();
		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program)
				.notificationRecords(new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION).notificationDate(new Date()).toNotificationRecord()).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).applicant(user).status(ApplicationFormStatus.REVIEW).approver(approver).status(ApplicationFormStatus.APPROVED)
		.toApplicationForm();
		
		save(approver, applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getApplicationsDueApprovedNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(noOfAppsBefore + 1, applications.size());
		assertTrue(applications.contains(applicationForm));
	}
	
	@Test
	public void shouldNotReturnApplicationsWithApprovedNotificationrRecord() {
		int noOfAppsBefore  = applicationDAO.getApplicationsDueApprovedNotifications().size();
		
		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder()
		.notificationRecords(new NotificationRecordBuilder().notificationType(NotificationType.APPROVED_NOTIFICATION).notificationDate(new Date()).toNotificationRecord()).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
		.approver(approver).status(ApplicationFormStatus.APPROVED)
		.toApplicationForm();
		
		save(approver, applicationForm);
		flushAndClearSession();
		
		List<ApplicationForm> applications = applicationDAO.getApplicationsDueApprovedNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(noOfAppsBefore, applications.size());
	}
	
	
	@Test
	public void shouldReturnNumberOfApplicationsInProgramThisYear(){
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		String lastYear = new Integer(Integer.parseInt(thisYear) - 1).toString();
		String nextYear = new Integer(Integer.parseInt(thisYear) + 1).toString();
		Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(program);		
		flushAndClearSession();
		
		int number =  applicationDAO.getApplicationsInProgramThisYear(program, thisYear);
		assertEquals(0, number);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		save( applicationFormOne);

		flushAndClearSession();	
		
		assertEquals(1,applicationDAO.getApplicationsInProgramThisYear(program, thisYear));
		
		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save( applicationFormTwo);

		flushAndClearSession();
		
		assertEquals(2, applicationDAO.getApplicationsInProgramThisYear(program, thisYear));
		assertEquals(0 ,applicationDAO.getApplicationsInProgramThisYear(program,lastYear));
		assertEquals(0 ,applicationDAO.getApplicationsInProgramThisYear(program, nextYear));


	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowDataAccessAcceptionOnParseException(){
	
	  applicationDAO.getApplicationsInProgramThisYear(program, "bob");	

	}

	@Test
	public void shouldGetApplicationByApplicationNumber(){
		Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
		save(program);
		
	
		
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		
		save(applicationFormOne);

		flushAndClearSession();
		
		ApplicationForm returnedForm = applicationDAO.getApplicationByApplicationNumber("ABC");
		assertEquals(applicationFormOne, returnedForm);
	}
	private List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException {

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
	
	@Test
	public void shouldNotReturnApplicationNotMovedInApprovalSinceLastAlert() {
		Date now = Calendar.getInstance().getTime();
		Date twentyFiveHoursAgo = DateUtils.addHours(now, -25);
		Date twentySevenHoursAgo = DateUtils.addHours(now, -27);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.APPROVAL_NOTIFICATION)
				.notificationDate(twentyFiveHoursAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).lastUpdated(twentySevenHoursAgo)
				.status(ApplicationFormStatus.APPROVAL).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO.getApplicationsDueApprovalNotifications();
		assertFalse(applicationsDueApprovalNotification.contains(applicationForm));

	}
	
	@Test
	public void shouldNotReturnApplicationMovedInApprovalSinceLastAlertAndLastAlertLessThan24HoursAgoFor1WeekReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = Calendar.getInstance().getTime();
		Date twentyThreeHoursAgo = DateUtils.addHours(now, -23);
		Date twelveHoursAgo = DateUtils.addHours(now, -12);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder().notificationType(NotificationType.APPROVAL_NOTIFICATION)
				.notificationDate(twentyThreeHoursAgo).toNotificationRecord();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).lastUpdated(twelveHoursAgo)
				.status(ApplicationFormStatus.APPROVAL).notificationRecords(lastNotificationRecord).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO.getApplicationsDueApprovalNotifications();
		assertFalse(applicationsDueApprovalNotification.contains(applicationForm));

	}
	

	@Test
	public void shouldGetApplicationsDueApprovalNotifications(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).notificationRecords(new NotificationRecordBuilder().notificationDate(new Date()).notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION).toNotificationRecord()).toApplicationForm();
		save(applicationForm);

		flushAndClearSession();
		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO.getApplicationsDueApprovalNotifications();
		assertTrue(applicationsDueApprovalNotification.contains(applicationForm));
		
	}
	@Test
	public void shouldNotReturnApplicationIfInApprovalButHasApprovalNotificationRecord(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).notificationRecords(new NotificationRecordBuilder().notificationDate(new Date()).notificationType(NotificationType.APPROVAL_NOTIFICATION).toNotificationRecord()).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO.getApplicationsDueApprovalNotifications();
		assertFalse(applicationsDueApprovalNotification.contains(applicationForm));
		
	}

	
	@Test
	public void shouldReturnApplicationIfInApprovalButHasApprovalNotificationRecor(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		save(applicationForm);
		
		flushAndClearSession();
		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO.getApplicationsDueApprovalNotifications();
		assertTrue(applicationsDueApprovalNotification.contains(applicationForm));
		
	}
	
}
