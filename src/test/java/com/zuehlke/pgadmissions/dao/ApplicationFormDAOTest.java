package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase {

	private ApplicationFormDAO applicationDAO;
	private RegisteredUser user;
	private Program program;

	private ApplicationForm application;

	@Before
	public void prepare() {
		applicationDAO = new ApplicationFormDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).build();

		program = new ProgramBuilder().code("doesntexist").title("another title").build();

		save(user, program);

		flushAndClearSession();
	}

	@Test(expected = NullPointerException.class)
	public void shouldSendNullPointerException() {
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
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
		assertEquals(inApplication.getId(), reloadedApplication.getId());
		assertEquals(inApplication.getApplicant().getId(), user.getId());
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
				.suppressChangeStateNotifications(false)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(eightDaysAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
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
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program)
				.applicant(user)
				.suppressChangeStateNotifications(false)
				.status(ApplicationFormStatus.VALIDATION).dueDate(oneMonthAgo).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);

		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
	}

	@Test
	public void shouldReturnOverDueApplicationInApprovalStageWithNoReminderForOneWeekReminder() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneMonthAgo = DateUtils.addMonths(today, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program)
				.applicant(user)
				.suppressChangeStateNotifications(false)
				.status(ApplicationFormStatus.APPROVAL).dueDate(oneMonthAgo).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
	}

	@Test
	public void shouldNotReturnApplicationsNotInApprovalStageForOneWeekReminder() {
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
						new NotificationRecordBuilder().notificationType(NotificationType.APPROVAL_REMINDER)
								.notificationDate(eightDaysAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
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
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(eightDaysAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationaInApprovalStageButNotOverDueForOneWeekReminder() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

		Date now = Calendar.getInstance().getTime();
		Date today = DateUtils.truncate(now, Calendar.DATE);
		Date oneWeekInFuture = DateUtils.addWeeks(today, 1);
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).dueDate(oneWeekInFuture).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL);
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
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.VALIDATION).dueDate(oneWeekInFuture).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationaInApprovalStageWitDueDateTodayForAReminderOfOneWeek() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

		Date now = Calendar.getInstance().getTime();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).dueDate(now).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL);
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

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.VALIDATION).dueDate(now).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
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
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(sixDaysAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertFalse(applicationsDueReminder.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnOverDueApplicationInApprovalStageWithReminderSixDaysWeekAgo() {
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
				.status(ApplicationFormStatus.APPROVAL)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.APPROVAL_REMINDER)
								.notificationDate(sixDaysAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.APPROVAL_REMINDER, ApplicationFormStatus.APPROVAL);
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
		Date oneWeekAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(7, TimeUnit.DAYS)));
		Date oneWeekAgoAndFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.suppressChangeStateNotifications(false)
				.status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(oneWeekAgoAndFiveMinAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
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
				.suppressChangeStateNotifications(false)
				.status(ApplicationFormStatus.VALIDATION)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(oneDayAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
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
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(onehourAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
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
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(oneDayAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
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
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(fiveMinutesAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
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
				.suppressChangeStateNotifications(false)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(tenMinutesAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
	}
	
    @Test
    public void shouldReturnOverDueApplicationInValidationStageWithReminder10MinutesAgoForAReminderWith5MinutesIntervalWithSuppressChangeStateNotificationsNull() {
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
                .suppressChangeStateNotifications(null)
                .dueDate(oneMonthAgo)
                .notificationRecords(
                        new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
                                .notificationDate(tenMinutesAgo).build()).build();
        save(applicationForm);

        flushAndClearSession();

        List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
                NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
        assertTrue(listContainsId(applicationForm, applicationsDueReminder));
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
				.suppressChangeStateNotifications(false)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(oneDayAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
	}

	@Test
	public void shouldReturnOverDueApplicationInValidationStageWithReminderOneWeekMinusFiveMinAgoForOneWeekReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

		Date now = Calendar.getInstance().getTime();
		Date oneWeekAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(7, TimeUnit.DAYS)));
		Date oneWeekAgoAndFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		Date oneMonthAgo = DateUtils.addMonths(now, -1);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.suppressChangeStateNotifications(false)
				.dueDate(oneMonthAgo)
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER)
								.notificationDate(oneWeekAgoAndFiveMinAgo).build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueReminder = applicationDAO.getApplicationsDueUserReminder(
				NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applicationsDueReminder));
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
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(twentyFiveHoursAgo).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.lastUpdated(twelveHoursAgo).status(ApplicationFormStatus.VALIDATION)
				.notificationRecords(lastNotificationRecord).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertTrue(listContainsId(applicationForm, applicationsDueUpdateNotification));
	}

	@Test
	public void shouldNotReturnApplicationUpdatedSinceLastAlertAndLastAlertLessThanOneoursAgoFor1WeekReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);

		Date now = Calendar.getInstance().getTime();
		Date twentyThreeMinutesAgo = DateUtils.addMinutes(now, -23);
		Date twelveMinutesAgo = DateUtils.addMinutes(now, -12);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(twentyThreeMinutesAgo)
				.build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.lastUpdated(twelveMinutesAgo).status(ApplicationFormStatus.VALIDATION)
				.notificationRecords(lastNotificationRecord).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertFalse(applicationsDueUpdateNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationNotUpdatedSinceLastAlert() {
		Date now = Calendar.getInstance().getTime();
		Date sixtyOneMinutesAgo = DateUtils.addMinutes(now, -61);
		Date sixtySeventMinutesAgo = DateUtils.addMinutes(now, -67);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(sixtyOneMinutesAgo).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.lastUpdated(sixtySeventMinutesAgo).status(ApplicationFormStatus.VALIDATION)
				.notificationRecords(lastNotificationRecord).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertFalse(applicationsDueUpdateNotification.contains(applicationForm));
	}

	@Test
	public void shouldNotReturnApplicationForDuplicateNotificationRecords() throws ParseException {
		Date now = Calendar.getInstance().getTime();
		Date sixtySeventMinutesAgo = DateUtils.addMinutes(now, -67);

		NotificationRecord updatedNotification = new NotificationRecordBuilder()
				.notificationType(NotificationType.UPDATED_NOTIFICATION)
				.notificationDate(DateUtils.parseDate("2012-09-09T00:03:00", new String[] { "yyyy-MM-dd'T'HH:mm:ss" }))
				.build();

		NotificationRecord duplicateNpdatedNotification = new NotificationRecordBuilder()
				.notificationType(NotificationType.UPDATED_NOTIFICATION).notificationDate(new Date()).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.lastUpdated(sixtySeventMinutesAgo).status(ApplicationFormStatus.VALIDATION)
				.notificationRecords(updatedNotification, duplicateNpdatedNotification).build();

		save(applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applicationsDueUpdateNotification = applicationDAO.getApplicationsDueUpdateNotification();
		assertFalse(applicationsDueUpdateNotification.contains(applicationForm));
	}

	@Test
	public void shouldReturnApplicationFormDueReviewNotification() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.REVIEW)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertTrue(listContainsId(applicationForm, applicationsDueApplicantReviewNotification));
	}

	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfWithdrawn() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.WITHDRAWN)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfRejected() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.REJECTED)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormDueReviewNotificationIfRejectedAndNewStatusIsRejected() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.REJECTED)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REJECTED)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICATION_MOVED_TO_REJECT_NOTIFICATION, ApplicationFormStatus.REJECTED);
		assertTrue(listContainsId(applicationForm, applicationsDueApplicantReviewNotification));
	}

	@Test
	public void shouldReturnApplicationFormDueApprovedNotificationIfApprovedAndNewStatusIsApproved() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.APPROVED)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.APPROVED)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantApprovedNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICATION_MOVED_TO_APPROVED_NOTIFICATION, ApplicationFormStatus.APPROVED);
		assertTrue(listContainsId(applicationForm, applicationsDueApplicantApprovedNotification));
	}

	@Test
	public void shouldNotReturnApplicationFormDueReviewNotificationIfApproved() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.APPROVED)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormForReviewNotificationIfNotifiedSinceOnlyEvent() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION)
				.notificationDate(fiveMinutesAgo).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).notificationRecords(lastNotificationRecord).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormForReviewNotificationIfNotifiedSinceLastEvent() {
		Date now = Calendar.getInstance().getTime();
		Date twentyMinutesAgo = DateUtils.addMinutes(now, -20);
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION)
				.notificationDate(fiveMinutesAgo).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.events(new StateChangeEventBuilder().date(twentyMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build(),
						new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
								.build()).notificationRecords(lastNotificationRecord).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormForReviewNotificationIfEventSinceLastNotified() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		Date fiveMinutesAgo = DateUtils.addMinutes(now, -5);
		Date twoMinutesAgo = DateUtils.addMinutes(now, -2);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION)
				.notificationDate(fiveMinutesAgo).build();
		StateChangeEvent firstEvent = new StateChangeEventBuilder().date(tenMinutesAgo)
				.newStatus(ApplicationFormStatus.REVIEW).build();
		Event lastEvent = new StateChangeEventBuilder().date(twoMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
				.build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.REVIEW).events(firstEvent, lastEvent)
				.notificationRecords(lastNotificationRecord).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueNotificationForStateChangeEvent(
						NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW);
		assertTrue(listContainsId(applicationForm, applicationsDueApplicantReviewNotification));
	}

	@Test
	public void shouldReturnApplicationFormDueApplicantSubmissionNotification() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.VALIDATION)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.VALIDATION)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueNotificationForStateChangeEvent(
				NotificationType.APPLICANT_SUBMISSION_NOTIFICATION, ApplicationFormStatus.VALIDATION);
		assertTrue(listContainsId(applicationForm, applications));
	}

	@Test
	public void shouldReturnApplicationsWithNoRejectNotificationDate() {
		BigInteger rejectedBigInt = (BigInteger) sessionFactory
				.getCurrentSession()
				.createSQLQuery(
						"select count(*) from APPLICATION_FORM where reject_notification_date IS NULL AND status = 'REJECTED'")
				.uniqueResult();
		int numOfRejecteAppl = rejectedBigInt.intValue();

		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder()
		//
				.program(program).applicant(user).status(ApplicationFormStatus.REVIEW)//
				.approver(approver).status(ApplicationFormStatus.REJECTED)//
				.build();

		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueRejectNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(numOfRejecteAppl + 1, applications.size());
		assertTrue(listContainsId(applicationForm, applications));
	}

	@Test
	public void shouldNotReturnApplicationsWithRejectNotificationDate() {
		BigInteger rejectedBigInt = (BigInteger) sessionFactory
				.getCurrentSession()
				.createSQLQuery(
						"select count(*) from APPLICATION_FORM where reject_notification_date IS NULL AND status = 'REJECTED'")
				.uniqueResult();
		int numOfRejecteAppl = rejectedBigInt.intValue();

		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder()
		//
				.program(program).applicant(user).status(ApplicationFormStatus.REVIEW)//
				.approver(approver).status(ApplicationFormStatus.REJECTED)//
				.build();

		applicationForm.setRejectNotificationDate(new Date());
		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueRejectNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(numOfRejecteAppl, applications.size());
	}

	@Test
	public void shouldReturnApplicationsWithNoApprovedNotificationRecord() {
		Integer noOfAppsBefore = applicationDAO.getApplicationsDueApprovedNotifications().size();
		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.notificationRecords(
						new NotificationRecordBuilder()
								.notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION)
								.notificationDate(new Date()).build()).program(program).applicant(user)
				.status(ApplicationFormStatus.REVIEW).applicant(user).status(ApplicationFormStatus.REVIEW)
				.approver(approver).status(ApplicationFormStatus.APPROVED).build();

		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueApprovedNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(noOfAppsBefore + 1, applications.size());
		assertTrue(listContainsId(applicationForm, applications));
	}

	@Test
	public void shouldNotReturnApplicationsWithApprovedNotificationrRecord() {
		int noOfAppsBefore = applicationDAO.getApplicationsDueApprovedNotifications().size();

		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationType(NotificationType.APPROVED_NOTIFICATION)
								.notificationDate(new Date()).build()).program(program).applicant(user)
				.status(ApplicationFormStatus.REVIEW).approver(approver).status(ApplicationFormStatus.APPROVED).build();

		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueApprovedNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(noOfAppsBefore, applications.size());
	}

	@Test
	public void shouldReturnNumberOfApplicationsInProgramThisYear() {
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		String lastYear = new Integer(Integer.parseInt(thisYear) - 1).toString();
		String nextYear = new Integer(Integer.parseInt(thisYear) + 1).toString();
		Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
		save(program);
		flushAndClearSession();

		long number = applicationDAO.getApplicationsInProgramThisYear(program, thisYear);
		assertEquals(0, number);
		ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).build();

		save(applicationFormOne);

		flushAndClearSession();

		assertEquals(Long.valueOf(1), applicationDAO.getApplicationsInProgramThisYear(program, thisYear));

		ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.VALIDATION).build();
		save(applicationFormTwo);

		flushAndClearSession();

		assertEquals(Long.valueOf(2), applicationDAO.getApplicationsInProgramThisYear(program, thisYear));
		assertEquals(Long.valueOf(0), applicationDAO.getApplicationsInProgramThisYear(program, lastYear));
		assertEquals(Long.valueOf(0), applicationDAO.getApplicationsInProgramThisYear(program, nextYear));

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowDataAccessAcceptionOnParseException() {

		applicationDAO.getApplicationsInProgramThisYear(program, "bob");

	}

	@Test
	public void shouldGetApplicationByApplicationNumber() {
		Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
		save(program);

		ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(program)
				.applicant(user).status(ApplicationFormStatus.APPROVAL).build();

		save(applicationFormOne);

		flushAndClearSession();

		ApplicationForm returnedForm = applicationDAO.getApplicationByApplicationNumber("ABC");
		assertEquals(applicationFormOne.getId(), returnedForm.getId());
	}

	private List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException {

		application = new ApplicationForm();
		application.setApplicant(user);
		application.setProgram(program);

		QualificationTypeDAO typeDao = new QualificationTypeDAO(sessionFactory);

		List<Qualification> qualifications = new ArrayList<Qualification>();

		Qualification qualification1 = new Qualification();
		qualification1.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualificationGrade("");
		qualification1.setQualificationInstitution("");

		qualification1.setQualificationLanguage("Abkhazian");
		qualification1.setQualificationSubject("");
		qualification1.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualificationType(typeDao.getAllQualificationTypes().get(0));

		qualifications.add(qualification1);

		Qualification qualification2 = new Qualification();
		qualification2.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualificationGrade("");
		qualification2.setQualificationInstitution("");
		qualification2.setQualificationLanguage("Abkhazian");
		qualification2.setQualificationSubject("");
		qualification2.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualificationType(typeDao.getAllQualificationTypes().get(0));

		qualifications.add(qualification1);
		return qualifications;
	}

	@Test
	public void shouldNotReturnApplicationNotMovedInApprovalSinceLastAlert() {
		Date now = Calendar.getInstance().getTime();
		Date twentyFiveHoursAgo = DateUtils.addHours(now, -25);
		Date twentySevenHoursAgo = DateUtils.addHours(now, -27);
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.APPROVAL_NOTIFICATION).notificationDate(twentyFiveHoursAgo).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.lastUpdated(twentySevenHoursAgo).status(ApplicationFormStatus.APPROVAL)
				.notificationRecords(lastNotificationRecord).build();
		Supervisor supervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(true).build();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).supervisors(supervisor)
				.build();
		applicationForm.setLatestApprovalRound(approvalRound);
		save(applicationForm, approvalRound);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO
				.getApplicationsDueApprovalReminder();
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
		NotificationRecord lastNotificationRecord = new NotificationRecordBuilder()
				.notificationType(NotificationType.APPROVAL_NOTIFICATION).notificationDate(twentyThreeHoursAgo).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.lastUpdated(twelveHoursAgo).status(ApplicationFormStatus.APPROVAL)
				.notificationRecords(lastNotificationRecord).build();
		Supervisor supervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(true).build();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).supervisors(supervisor)
				.build();
		applicationForm.setLatestApprovalRound(approvalRound);
		save(applicationForm, approvalRound);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO
				.getApplicationsDueApprovalReminder();
		assertFalse(applicationsDueApprovalNotification.contains(applicationForm));

	}

	@Test
	public void shouldGetApplicationsDueDueApprovalReminder() {
	    DateTime oldDate = new DateTime(2000,1, 1, 00, 0);
        ApplicationForm applicationForm = new ApplicationFormBuilder()
                .program(program)
                .applicant(user)
                .status(ApplicationFormStatus.APPROVAL)
                .notificationRecords(
                        new NotificationRecordBuilder().notificationDate(oldDate.toDate())
                                .notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION).build(),
                        new NotificationRecordBuilder().notificationDate(oldDate.toDate())
                                .notificationType(NotificationType.APPROVAL_REMINDER).build())
                .build();

		Supervisor supervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(true).build();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).supervisors(supervisor).build();
		applicationForm.setLatestApprovalRound(approvalRound);

		save(applicationForm, approvalRound);

		flushAndClearSession();
		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO.getApplicationsDueApprovalReminder();
		assertTrue(listContainsId(applicationForm, applicationsDueApprovalNotification));
	}

	@Test
	public void shouldNotReturnApplicationIfInApprovalButHasApprovalNotificationRecord() {
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.APPROVAL)
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(new Date())
								.notificationType(NotificationType.APPROVAL_NOTIFICATION).build()).build();
		Supervisor supervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(true).build();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).supervisors(supervisor)
				.build();
		applicationForm.setLatestApprovalRound(approvalRound);
		save(applicationForm, approvalRound);

		flushAndClearSession();
		List<ApplicationForm> applicationsDueApprovalNotification = applicationDAO
				.getApplicationsDueApprovalReminder();
		assertFalse(applicationsDueApprovalNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormDueRegistryNotification() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.registryUsersDueNotification(true)
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.REVIEW)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueRegistryNotification();
		assertTrue(listContainsId(applicationForm, applicationsDueApplicantReviewNotification));
	}

	@Test
	public void shouldNotReturnApplicationFormNotDueRegistryNotification() {
		Date now = Calendar.getInstance().getTime();
		Date tenMinutesAgo = DateUtils.addMinutes(now, -10);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.registryUsersDueNotification(false)
				.program(program)
				.applicant(user)
				.status(ApplicationFormStatus.REVIEW)
				.events(new StateChangeEventBuilder().date(tenMinutesAgo).newStatus(ApplicationFormStatus.REVIEW)
						.build()).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDueApplicantReviewNotification = applicationDAO
				.getApplicationsDueRegistryNotification();
		assertFalse(applicationsDueApplicantReviewNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormPendingApprovalRestart() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().pendingApprovalRestart(true).program(program)
				.applicant(user).status(ApplicationFormStatus.APPROVAL).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationsDueApprovalRequestNotification();
		assertTrue(listContainsId(applicationForm, applicationsDoApprovalRequestNotification));
	}

	@Test
	public void shouldNotReturnApplicationFormPendingApprovalRestartNotInApproval() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().pendingApprovalRestart(true).program(program)
				.applicant(user).status(ApplicationFormStatus.REJECTED).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationsDueApprovalRequestNotification();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shoulNotdReturnApplicationFormNotPendingApprovalRestart() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().pendingApprovalRestart(false).program(program)
				.applicant(user).status(ApplicationFormStatus.APPROVAL).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationsDueApprovalRequestNotification();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormPendingApprovalRestartIfNotificationRecordExists() {

		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(new Date())
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build())
				.pendingApprovalRestart(true).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationsDueApprovalRequestNotification();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormPendingApprovalRestartForReminder() {
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
				.createCriteria(ReminderInterval.class).uniqueResult();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		Date now = Calendar.getInstance().getTime();
		Date twoWeeksAgo = DateUtils.addWeeks(now, -2);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(twoWeeksAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build())
				.pendingApprovalRestart(true).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertTrue(listContainsId(applicationForm, applicationsDoApprovalRequestNotification));
	}

	@Test
	public void shouldNotReturnApplicationFormPendingApprovalRestartForReminderIfNoNotification() {

		ApplicationForm applicationForm = new ApplicationFormBuilder().pendingApprovalRestart(true).program(program)
				.applicant(user).status(ApplicationFormStatus.APPROVAL).build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationFormPendingApprovalRestartForReminderIfNotificationAndRemindersMoreThanReminderIntervalAgo() {

		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
				.createCriteria(ReminderInterval.class).uniqueResult();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		Date now = Calendar.getInstance().getTime();
		Date twoWeeksAgo = DateUtils.addWeeks(now, -2);
		Date threeWeeksAgo = DateUtils.addDays(now, -3);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(threeWeeksAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build(),
						new NotificationRecordBuilder().notificationDate(twoWeeksAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER).build())
				.pendingApprovalRestart(true).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertTrue(listContainsId(applicationForm, applicationsDoApprovalRequestNotification));
	}

	@Test
	public void shouldNoteturnApplicationFormPendingApprovalRestartForReminderIfNotInApproval() {
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
				.createCriteria(ReminderInterval.class).uniqueResult();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		Date now = Calendar.getInstance().getTime();
		Date twoWeeksAgo = DateUtils.addWeeks(now, -2);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(twoWeeksAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build())
				.pendingApprovalRestart(true).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormPendingApprovalRestartForReminderIfNotPendingApprovalRestart() {
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
				.createCriteria(ReminderInterval.class).uniqueResult();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		Date now = Calendar.getInstance().getTime();
		Date twoWeeksAgo = DateUtils.addWeeks(now, -2);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(twoWeeksAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build())
				.pendingApprovalRestart(false).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormPendingApprovalRestartForReminderInNotificationWithinReminderPeriod() {
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
				.createCriteria(ReminderInterval.class).uniqueResult();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		Date now = Calendar.getInstance().getTime();
		Date threeDaysAgo = DateUtils.addDays(now, -3);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(threeDaysAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build())
				.pendingApprovalRestart(true).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldNotReturnApplicationFormPendingApprovalRestartForReminderIfReminderWithinReminderPeriod() {
		ReminderInterval reminderInterval = (ReminderInterval) sessionFactory.getCurrentSession()
				.createCriteria(ReminderInterval.class).uniqueResult();
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);

		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		Date now = Calendar.getInstance().getTime();
		Date twoWeeksAgo = DateUtils.addWeeks(now, -2);
		Date threeDaysAgo = DateUtils.addDays(now, -3);
		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder().notificationDate(twoWeeksAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build(),
						new NotificationRecordBuilder().notificationDate(threeDaysAgo)
								.notificationType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER).build())
				.pendingApprovalRestart(true).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		save(applicationForm);

		flushAndClearSession();

		List<ApplicationForm> applicationsDoApprovalRequestNotification = applicationDAO
				.getApplicationDueApprovalRestartRequestReminder();
		assertFalse(applicationsDoApprovalRequestNotification.contains(applicationForm));

	}

	@Test
	public void shouldReturnApplicationsWithNoApprovalNotificationRecord() {
		Integer noOfAppsBefore = applicationDAO.getApplicationsDueMovedToApprovalNotifications().size();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).build();

		save(applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueMovedToApprovalNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(noOfAppsBefore + 1, applications.size());
		assertTrue(listContainsId(applicationForm, applications));
	}

	@Test
	public void shouldNotReturnApplicationsWithApprovalNotificationrRecord() {
		int noOfAppsBefore = applicationDAO.getApplicationsDueMovedToApprovalNotifications().size();

		RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
				.username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder()
				.notificationRecords(
						new NotificationRecordBuilder()
								.notificationType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION)
								.notificationDate(new Date()).build()).program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).build();

		save(approver, applicationForm);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsDueMovedToApprovalNotifications();
		Assert.assertNotNull(applications);
		Assert.assertEquals(noOfAppsBefore, applications.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetTwoApplicationsByApplicantAndProgram() {

		Program program = new ProgramBuilder().title("program").code("KLOP").build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.APPROVAL).build();
		ApplicationForm applicationForm2 = new ApplicationFormBuilder().program(program).applicant(user)
				.status(ApplicationFormStatus.UNSUBMITTED).build();

		save(program, applicationForm, applicationForm2);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
		assertNotNull(applications);

		Matcher<Iterable<ApplicationForm>> hasItems = hasItems( //
				hasProperty("status", equalTo(ApplicationFormStatus.APPROVAL)), //
				hasProperty("status", equalTo(ApplicationFormStatus.UNSUBMITTED)));
		assertThat(applications, hasItems);
	}

	@Test
	public void shouldGetNoApplicationsByApplicantAndProgram() {

		Program program = new ProgramBuilder().title("program").code("KLOP").build();

		save(program);
		flushAndClearSession();

		List<ApplicationForm> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
		assertThat(applications, is(empty()));
	}

	private boolean listContainsId(ApplicationForm form, List<ApplicationForm> aplicationForms) {
		for (ApplicationForm entry : aplicationForms) {
			if (form.getId().equals(entry.getId())) {
				return true;
			}
		}
		return false;
	}

}
