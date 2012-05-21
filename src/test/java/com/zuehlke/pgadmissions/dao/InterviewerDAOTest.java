package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class InterviewerDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private InterviewerDAO dao;
	private Program program;
	private ReminderInterval reminderInterval;
	
	
	@Test
	public void shouldGetInterviewerById() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().toInterviewer();
		dao.save(interviewer);
		assertNotNull(interviewer.getId());
		flushAndClearSession();
		assertEquals(interviewer, dao.getInterviewerById(interviewer.getId()));

	}
	
	@Test
	public void shouldSaveInterviewer() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().toInterviewer();
		dao.save(interviewer);
		assertNotNull(interviewer.getId());
		flushAndClearSession();

		Interviewer returnedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class,interviewer.getId());
		assertEquals(returnedInterviewer, interviewer);
		
	}
	
	@Test
	public void shouldReturnInterviewerIfLastModifiedIsNull() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertTrue(interviewers.contains(interviewer));

	}

	@Test
	public void shouldNotReturnInterviewerInLastNotifiedIsNotNull() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(new Date()).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

	}

	@Test
	public void shouldNotReturnInterviewerIfApplicationNotInInterview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

		
	}
	@Test
	public void shouldNotReturnInterviewerifNotInterviewerOfLatestInterview() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.getInterviews().add(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

	}
	
	@Test
	public void shouldReturnInterviewerReminded7Minus5minDaysPastDuewDateForASixDaysIReminderInterval() {
		reminderInterval.setId(1);
		reminderInterval.setDuration(6);
		reminderInterval.setUnit(DurationUnitEnum.DAYS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(interviewers.contains(interviewer));
	}
	
	
	@Test
	public void shouldReturnInterviewerReminded2MinutesAgoForOneMinuteReminderInterval() {
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = new Date();
		Date twoMinutesAgo = DateUtils.addMinutes(now, -2);
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(sevenDaysAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(twoMinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
		
		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(interviewers.contains(interviewer));
	}
	
	
	@Test
	public void shouldNotReturnInterviewerReminded1MinutesAgoForOneMinuteReminderInterval() {
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		Date now = new Date();
		Date oneMinuteAgo = DateUtils.addMinutes(now, -1);
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(sevenDaysAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(oneMinuteAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
		
		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	
	@Test
	public void shouldNotReturnInterviewerIfNotInterviewerOfLatestInterview() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.getInterviews().add(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	@Test
	public void shouldNotReturnInterviewerForAppNotInInterview() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	@Test
	public void shouldNotReturnInterviewerIfApplicationNotPastDuewDate() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date inTwoWeeks = DateUtils.addDays(now, 14);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(inTwoWeeks)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
	

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	@Test
	public void shouldNotReturnInterviewerIfApplication6DaysPastDuewDate() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sixDaysAgo = DateUtils.addDays(now, -6);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(sixDaysAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	@Test	
	public void shouldReturnInterviewerReminded7Plus5minDaysAgo() {

		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
		Date sevenDaysPlus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, -5);			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysPlus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
		
		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(interviewers.contains(interviewer));
	}

	@Test
	public void shouldNotReturnInterviewerLastReminded6DaysAgo() {
		Date now = new Date();
		Date sixDaysAgo = DateUtils.addDays(now, -6);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sixDaysAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}

	@Test
		public void shouldReturnNotInterviewerWhoHaveProvidedFeedback() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
		application.setLatestInterview(interview);
		InterviewComment interviewComment = new InterviewCommentBuilder().interviewer(interviewer).adminsNotified(CheckedStatus.NO).commentType(CommentType.INTERVIEW).comment("This is an interview comment").suitableCandidate(CheckedStatus.NO).user(user).application(application).toInterviewComment();
		save(application, interviewer, interview, interviewComment);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	
	}
	
	
	@Before
	public void setUp() {
		super.setUp();
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		
		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		save(user, program);
		
		dao = new InterviewerDAO(sessionFactory);
	}
}
