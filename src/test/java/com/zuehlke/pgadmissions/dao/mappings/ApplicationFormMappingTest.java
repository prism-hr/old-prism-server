package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
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
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory
				.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = (ApplicationForm) sessionFactory
				.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);

		assertEquals(user, reloadedApplication.getApplicant());
		assertEquals(project, reloadedApplication.getProject());
		assertEquals(SubmissionStatus.UNSUBMITTED,
				reloadedApplication.getSubmissionStatus());
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
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory
				.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = (ApplicationForm) sessionFactory
				.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);

		assertEquals(user, reloadedApplication.getApplicant());
		assertEquals(project, reloadedApplication.getProject());
		assertEquals(SubmissionStatus.SUBMITTED,
				reloadedApplication.getSubmissionStatus());
		Assert.assertEquals(1, reloadedApplication.getReviewers().size());
		Assert.assertTrue(reloadedApplication.getReviewers().contains(user));
	}
	
	@Test 
	public void shouldSaveAndLoadApplicationFormWithAddress() {
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		Address address = new AddressBuilder().application(application).country("Germany")
					.street("1 Main Street").postCode("NW2 456").city("london").startDate(new Date()).endDate(new Date()).toAddress();
		application.setAddresses(Arrays.asList(address));

		assertNull(application.getId());

		sessionFactory.getCurrentSession().save(application);

		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory
		.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = (ApplicationForm) sessionFactory
		.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);

		assertEquals(user, reloadedApplication.getApplicant());
		assertEquals(project, reloadedApplication.getProject());
		assertEquals(SubmissionStatus.SUBMITTED,
				reloadedApplication.getSubmissionStatus());
		Assert.assertEquals(1, reloadedApplication.getAddresses().size());
		Assert.assertTrue(reloadedApplication.getAddresses().contains(address));


	}

	@Test
	public void shouldLoadApplicationFormWithComments() {

		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);

		sessionFactory.getCurrentSession().save(application);
		Integer id = application.getId();
		flushAndClearSession();
		
		ApplicationReview applicationReviewOne = new ApplicationReviewBuilder().application(application).comment("comment1").user(user).toApplicationReview();
		ApplicationReview applicationReviewTwo = new ApplicationReviewBuilder().application(application).comment("comment2").user(user).toApplicationReview();
		save(applicationReviewOne, applicationReviewTwo);
		
		flushAndClearSession();
		
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertEquals(2, reloadedApplication.getApplicationComments().size());
		assertTrue(reloadedApplication.getApplicationComments().containsAll(Arrays.asList(applicationReviewOne, applicationReviewTwo)));
	}
	
	@Ignore
	@Test
	public void shouldSaveQualificationsWithApplication() {
		
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		
	//	sessionFactory.getCurrentSession().save(application);
		//Integer id = application.getId();
		//flushAndClearSession();
		
		Qualification qualification1 = new QualificationBuilder().date_taken("").degree("").grade("").institution("").toQualification();

		Qualification qualification2 = new QualificationBuilder().date_taken("").degree("d").grade("1").institution("").toQualification();

		application.getQualifications().addAll(Arrays.asList(qualification1, qualification2));

		
		sessionFactory.getCurrentSession().saveOrUpdate(application);
		flushAndClearSession();
		
		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertEquals(2, reloadedApplication.getQualifications().size());
		
	}

	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username")
				.password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false)
				.enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist")
				.description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis")
				.description("hello").title("title two").program(program)
				.toProject();
		save(user, program, project);

		flushAndClearSession();
	}

}
