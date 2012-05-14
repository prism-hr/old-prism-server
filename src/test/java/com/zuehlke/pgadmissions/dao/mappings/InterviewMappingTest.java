package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class InterviewMappingTest extends AutomaticRollbackTestCase{

	private RegisteredUser user;
	private Program program;
	private RegisteredUser interviewerUser;
	private ApplicationForm application;
	
	@Test
	public void shouldSaveLoadInterviewWithInterviewer() {
		
		Interview interview = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).application(application).toInterview();
		
		sessionFactory.getCurrentSession().save(interview);
		
		flushAndClearSession();
		
		Interview reloadedInterview = (Interview) sessionFactory.getCurrentSession().get(Interview.class, interview.getId());
		assertNotSame(interview, reloadedInterview);
		assertEquals(interview, reloadedInterview);
		
		Assert.assertEquals(1, reloadedInterview.getInterviewers().size());
		Interviewer interviewer = reloadedInterview.getInterviewers().get(0);
		assertEquals(interviewerUser, interviewer.getUser());
		assertEquals(reloadedInterview, interviewer.getInterview());
		
	}
	
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		interviewerUser = new RegisteredUserBuilder().firstName("brad").lastName("brady").email("brady@test.com").username("brady").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();
		save(user, program, interviewerUser, application);

		flushAndClearSession();
	}

}
