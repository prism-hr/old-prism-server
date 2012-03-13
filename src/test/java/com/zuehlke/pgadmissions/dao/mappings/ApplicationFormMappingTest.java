package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.MessengerBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
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
		
		assertNotNull(application.getPersonalDetails());
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
		
		assertNotNull(application.getPersonalDetails());
		
		assertNull(application.getPersonalDetails().getId());
	}
	@Test
	public void shouldLoadApplicationFormWithPersonalDetails() throws ParseException {
		Country country1 = new CountryBuilder().code("AA").name("AA").toCountry();
		Country country2 = new CountryBuilder().code("CC").name("CC").toCountry();
		save(country1, country2);

		ApplicationForm application = new ApplicationFormBuilder().applicant(user).project(project).submissionStatus(SubmissionStatus.UNSUBMITTED)
				.toApplicationForm();

		sessionFactory.getCurrentSession().save(application);
		flushAndClearSession();
		PersonalDetail personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").gender(Gender.MALE).lastName("lastname").residenceCountry(country2)
				.residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(application).toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(personalDetails, reloadedApplication.getPersonalDetails());

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

	@Test
	public void shouldSaveAndLoadApplicationFormWithAddress() {
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		Address address = new AddressBuilder().application(application).country("Germany").location("1 Main Street").postCode("NW2 456").location("london")
				.purpose("EMPLOYER").startDate(new Date()).endDate(new Date()).contactAddress(AddressStatus.NO).toAddress();
		application.setAddresses(Arrays.asList(address));

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

	@Test
	public void shouldSaveQualificationsWithApplication() throws ParseException {

		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);

		// sessionFactory.getCurrentSession().save(application);
		// Integer id = application.getId();
		// flushAndClearSession();
		Qualification qualification1 = new QualificationBuilder().q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).q_grade("")
				.q_institution("").q_language_of_study("").q_level("").q_name_of_programme("").q_score("")
				.q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();
		Qualification qualification2 = new QualificationBuilder().q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).q_grade("")
				.q_institution("").q_language_of_study("").q_level("").q_name_of_programme("").q_score("")
				.q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();

		application.getQualifications().addAll(Arrays.asList(qualification1, qualification2));

		sessionFactory.getCurrentSession().saveOrUpdate(application);
		flushAndClearSession();

		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertEquals(2, reloadedApplication.getQualifications().size());

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
