package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class InterviewMappingTest extends AutomaticRollbackTestCase{

	private RegisteredUser user;
	private Program program;
	private RegisteredUser interviewerUser;
	private ApplicationForm application;
	
	@Test
	public void shouldSaveLoadInterviewWithInterviewer() {
		
		Interview interview = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).build()).application(application).build();
		
		sessionFactory.getCurrentSession().save(interview);
		
		flushAndClearSession();
		
		Interview reloadedInterview = (Interview) sessionFactory.getCurrentSession().get(Interview.class, interview.getId());
		assertNotSame(interview, reloadedInterview);
		assertEquals(interview.getId(), reloadedInterview.getId());
		
		Assert.assertEquals(1, reloadedInterview.getInterviewers().size());
		Interviewer interviewer = reloadedInterview.getInterviewers().get(0);
		assertEquals(interviewerUser.getId(), interviewer.getUser().getId());
		assertEquals(reloadedInterview.getId(), interviewer.getInterview().getId());
		assertNotNull(reloadedInterview.getCreatedDate());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DATE),DateUtils.truncate(reloadedInterview.getCreatedDate(), Calendar.DATE));
	}
	
	@Before
	public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        interviewerUser = new RegisteredUserBuilder().firstName("brad").lastName("brady").email("brady@test.com")
                .username("brady").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(user, institution, program, interviewerUser, application);
        flushAndClearSession();
	}
}
