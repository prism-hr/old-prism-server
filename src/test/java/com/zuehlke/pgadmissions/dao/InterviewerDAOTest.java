package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

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
	public void shouldNotReturInterviewerInLastNotifiedIsNotNull() {
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
