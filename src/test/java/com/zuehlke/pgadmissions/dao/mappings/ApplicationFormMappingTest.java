package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormMappingTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;
	private Project project;

	@Test
	public void shouldSaveAndLoadApplicationForm() {

		ApplicationForm application = new ApplicationForm();
		application.setApplicant(user);
		application.setProject(project);
		application.setSubmissionStatus(SubmissionStatus.UNSUBMITTED);
		
		
		assertNull(application.getId());

		sessionFactory.getCurrentSession().save(application);

		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);

		assertEquals(user, reloadedApplication.getApplicant());
		assertEquals(project, reloadedApplication.getProject());
		assertEquals(SubmissionStatus.UNSUBMITTED, reloadedApplication.getSubmissionStatus());
	}
	
	@Test
	public void shouldSaveAndLoadApplicationFormWithReviewer() {

		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		application.setReviewers(Arrays.asList(user));
		
		assertNull(application.getId());

		sessionFactory.getCurrentSession().save(application);

		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);

		assertEquals(user, reloadedApplication.getApplicant());
		assertEquals(project, reloadedApplication.getProject());
		assertEquals(SubmissionStatus.SUBMITTED, reloadedApplication.getSubmissionStatus());
		Assert.assertEquals(1, reloadedApplication.getReviewers().size());
		Assert.assertTrue(reloadedApplication.getReviewers().contains(user));
	}
	

	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(user, program, project);

		flushAndClearSession();
	}

}
