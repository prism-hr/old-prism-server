package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class InterviewerDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private InterviewerDAO dao;
	private Program program;
	private ReminderInterval reminderInterval;
	
	
	@Test
	public void shouldGetInterviewerById() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().build();
		dao.save(interviewer);
		assertNotNull(interviewer.getId());
		flushAndClearSession();
		assertEquals(interviewer.getId(), dao.getInterviewerById(interviewer.getId()).getId());

	}
	
	@Test
	public void shouldSaveInterviewer() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().build();
		dao.save(interviewer);
		assertNotNull(interviewer.getId());
		flushAndClearSession();

		Interviewer returnedInterviewer = (Interviewer) sessionFactory.getCurrentSession().get(Interviewer.class,interviewer.getId());
		assertEquals(returnedInterviewer.getId(), interviewer.getId());
		
	}
	
	@Test
	public void shouldReturnInterviewerIfLastModifiedIsNull() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();
		Interviewer interviewer = new InterviewerBuilder().user(user).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertTrue(listContainsId(interviewer, interviewers));
	}

	@Test
	public void shouldNotReturnInterviewerInLastNotifiedIsNotNull() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(new Date()).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

	}

	@Test
	public void shouldNotReturnInterviewerIfApplicationNotInInterview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
		Interviewer interviewer = new InterviewerBuilder().user(user).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

		
	}
	@Test
	public void shouldNotReturnInterviewerifNotInterviewerOfLatestInterview() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();
		Interviewer interviewer = new InterviewerBuilder().user(user).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(listContainsId(interviewer, interviewers));
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(twoMinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
		
		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(listContainsId(interviewer, interviewers));
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(oneMinuteAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	@Test	
	public void shouldReturnInterviewerReminded7Plus5minDaysAgo() {

		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(7, TimeUnit.DAYS)));
		Date twoWeeksAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(14, TimeUnit.DAYS)));
		Date sevenDaysPlus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, -5);			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysPlus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		save(application, interviewer, interview);
		flushAndClearSession();
		
		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(listContainsId(interviewer, interviewers));
	}

	@Test
	public void shouldNotReturnInterviewerLastReminded6DaysAgo() {
		Date now = new Date();
		Date sixDaysAgo = DateUtils.addDays(now, -6);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
			
			
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sixDaysAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
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
				.build();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(sevenDaysMinus5MinutesAgo).build();
		Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
		application.setLatestInterview(interview);
		InterviewComment interviewComment = new InterviewCommentBuilder().interviewer(interviewer).adminsNotified(false).commentType(CommentType.INTERVIEW).comment("This is an interview comment").suitableCandidateForUcl(false).user(user).application(application).build();
		save(application, interviewer, interview, interviewComment);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	
	}
	
	@Test
	public void shouldReturnInterviewersRequireAdminNotification() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.build();
		
		Interviewer interviewer1 = new InterviewerBuilder().user(user).id(1).requiresAdminNotification(true).dateAdminsNotified(null).build();
		Interviewer interviewer2 = new InterviewerBuilder().user(user).id(2).requiresAdminNotification(false).dateAdminsNotified(null).build();
		Interviewer interviewer3 = new InterviewerBuilder().user(user).id(1).requiresAdminNotification(true).dateAdminsNotified(new Date()).build();
		Interviewer interviewer4 = new InterviewerBuilder().user(user).id(2).requiresAdminNotification(false).dateAdminsNotified(new Date()).build();
		
		Interview reviewRound = new InterviewBuilder().application(application).interviewers(interviewer1, interviewer2, interviewer3, interviewer4).build();
		application.setLatestInterview(reviewRound);
		save(application,interviewer1, interviewer2, interviewer3, interviewer4, reviewRound);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersRequireAdminNotification();
		assertFalse(listContainsId(interviewer3, interviewers));
		assertFalse(listContainsId(interviewer4, interviewers));
		assertFalse(listContainsId(interviewer2, interviewers));
		assertTrue(listContainsId(interviewer1, interviewers));
	}
	
	@Before
	public void initialise() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		program = new ProgramBuilder().code("doesntexist").title("another title").build();
		
		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		save(user, program);
		
		dao = new InterviewerDAO(sessionFactory);
	}

	private boolean listContainsId(Interviewer interviewer, List<Interviewer> interviewers) {
	    for (Interviewer entry : interviewers) {
	        if (entry.getId().equals(interviewer.getId())) {
	            return true;
	        }
	    }
	    return false;
	}
}
