package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewerDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private InterviewerDAO dao;
	private Program program;
	
	
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
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertTrue(interviewers.contains(interviewer));

	}

	@Test
	public void shouldNotReturnInterviewerInLastNotifiedIsNotNull() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Interviewer interviewer = new InterviewerBuilder().user(user).lastNotified(new Date()).application(application).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

	}

	@Test
	public void shouldNotReturnInterviewerIfApplicationNotInInterview() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();

		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueNotification();
		assertFalse(interviewers.contains(interviewer));

	}
	
	@Test
	public void shouldReturnInterviewerReminded7Minus5minDaysPastDuewDate() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date twoWeeksAgo = DateUtils.addDays(now, -14);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(twoWeeksAgo)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(interviewers.contains(interviewer));
	}
	
	@Test
	public void shouldNotReturnInterviewerForAppNotInInterview() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);		
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW).dueDate(sevenDaysAgo)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		save(interviewer);
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
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		save(interviewer);
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
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	
	@Test
	public void shouldNotReturnInterviewerIfNotInterviewerOfCurrentInterview() {
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sixDaysAgo = DateUtils.addDays(now, -6);
		Date sevenDaysMinus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, 5);
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).dueDate(sixDaysAgo)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysMinus5MinutesAgo).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	@Test
	@Ignore
	public void shouldReturnInterviewerReminded7Plus5minDaysAgo() {
		fail("");
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Date sevenDaysPlus5MinutesAgo = DateUtils.addMinutes(sevenDaysAgo, -5);
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysPlus5MinutesAgo).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertTrue(interviewers.contains(interviewer));
	}

	@Test
	@Ignore
	public void shouldNotReturnInterviewerLastNotified6DaysAgo() {
		fail("");
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sixDaysAgo = DateUtils.addDays(now, -6);

		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sixDaysAgo).toInterviewer();
		save(interviewer);
		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}

	@Test
	@Ignore
	public void shouldReturnNotInterviewerWithInterview() {
		fail("");
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.REVIEW)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysAgo).toInterviewer();
		InterviewComment reviewComment = new InterviewCommentBuilder().interviewer(interviewer).adminsNotified(CheckedStatus.NO).commentType(CommentType.REVIEW)
				.comment("This is a review comment").suitableCandidate(CheckedStatus.NO).user(user).application(application).decline(CheckedStatus.YES)
				.willingToSupervice(CheckedStatus.NO).toInterviewComment();
		save(interviewer, reviewComment);

		flushAndClearSession();

		List<Interviewer> interviewers = dao.getInterviewersDueReminder();
		assertFalse(interviewers.contains(interviewer));
	}
	@Test
	@Ignore
	public void shouldNotReturnInterviewerIfApplicationFormNotInInterview() {
		fail("");
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
				.toApplicationForm();
		save(application);
		flushAndClearSession();
		Date now = new Date();
		Date sevenDaysAgo = DateUtils.addDays(now, -7);		
		Interviewer interviewer = new InterviewerBuilder().user(user).application(application).lastNotified(sevenDaysAgo).toInterviewer();
		save(interviewer);
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
		
		save(user, program);
		
		dao = new InterviewerDAO(sessionFactory);
	}
}
