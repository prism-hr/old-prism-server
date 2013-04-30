package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class InterviewDAOTest extends AutomaticRollbackTestCase {

	private InterviewDAO dao;
	private RegisteredUser applicant;
	private Program program;

	@Test
	public void shouldSaveInterview() {
		ApplicationForm application = new ApplicationFormBuilder().id(2).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		Interview interview = new InterviewBuilder().application(application).furtherDetails("at 9pm").lastNotified(new Date()).locationURL("pgadmissions.com").build();
		dao.save(interview);
		assertNotNull(interview.getId());
		flushAndClearSession();

		Interview returnedInterview = (Interview) sessionFactory.getCurrentSession().get(Interview.class,interview.getId());
		assertEquals(returnedInterview.getId(), interview.getId());
		
	}
	
	@Test
	public void shouldGetInterviewerById() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		flushAndClearSession();
		
		Interview interview = new InterviewBuilder().build();
		dao.save(interview);
		assertNotNull(interview.getId());
		flushAndClearSession();
		assertEquals(interview.getId(), dao.getInterviewById(interview.getId()).getId());
	}
	
	@Before
	public void prepare() {
		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		program = new ProgramBuilder().code("doesntexist").title("another title").build();
		
		save(applicant, program, applicant);
		
		dao = new InterviewDAO(sessionFactory);
	}
}
