package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;

public class PersonalDetailsMappingTest extends AutomaticRollbackTestCase {

	private Country country1;
	private Country country2;
	private ApplicationForm applicationForm;

	@Test
	public void shouldSaveAndLoadPersonalDetails() throws Exception {

		PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").gender(Gender.MALE).lastName("lastname").residenceCountry(country2)
				.requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc").applicationForm(applicationForm).toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		assertNotNull(personalDetails.getId());
		Integer id = personalDetails.getId();
		PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, id);

		assertSame(personalDetails, reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, id);

		assertNotSame(personalDetails, reloadedDetails);
		assertEquals(personalDetails, reloadedDetails);

		assertEquals(personalDetails.getApplication(), reloadedDetails.getApplication());
		assertEquals(personalDetails.getCountry(), reloadedDetails.getCountry());
		assertEquals(personalDetails.getDateOfBirth(), reloadedDetails.getDateOfBirth());
		assertEquals(personalDetails.getEmail(), reloadedDetails.getEmail());
		assertEquals(personalDetails.getFirstName(), reloadedDetails.getFirstName());
		assertEquals(personalDetails.getGender(), reloadedDetails.getGender());
		assertEquals(personalDetails.getLastName(), reloadedDetails.getLastName());
		assertEquals(personalDetails.getResidenceCountry(), reloadedDetails.getResidenceCountry());
		assertTrue(reloadedDetails.isRequiresVisa());
		assertTrue(reloadedDetails.isEnglishFirstLanguage());
		assertEquals(personalDetails.getPhoneNumber(), reloadedDetails.getPhoneNumber());
		

	}

	

	@Test
	public void shouldSaveAndLoadPersonalDetailsWithCandiateNationalities() throws Exception {
		Country nationality1 = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		Country nationality2 = new CountryBuilder().code("bb").name("bbbbb").toCountry();
		Country nationality3 = new CountryBuilder().code("cc").name("ccccc").toCountry();
		Country nationality4 = new CountryBuilder().code("dd").name("ddddd").toCountry();
		
		sessionFactory.getCurrentSession().save(nationality1);
		sessionFactory.getCurrentSession().save(nationality2);
		sessionFactory.getCurrentSession().save(nationality3);
		sessionFactory.getCurrentSession().save(nationality4);
		

		flushAndClearSession();
		
		PersonalDetails personalDetails = new PersonalDetailsBuilder().candiateNationalities(nationality1, nationality2).maternalGuardianNationalities(nationality4).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.englishFirstLanguage(false).requiresVisa(false).phoneNumber("abc")
				.lastName("lastname").residenceCountry(country1).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getCandidateNationalities().size());
		assertTrue(reloadedDetails.getCandidateNationalities().containsAll(Arrays.asList(nationality1,nationality2)));
		reloadedDetails.getCandidateNationalities().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getCandidateNationalities().size());
		assertTrue(reloadedDetails.getCandidateNationalities().containsAll(Arrays.asList(nationality1)));

		reloadedDetails.getCandidateNationalities().add(nationality3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getCandidateNationalities().size());
		assertTrue(reloadedDetails.getCandidateNationalities().containsAll(Arrays.asList(nationality1, nationality3)));
		
	}
	
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithMaternalGuardianNationalities() throws Exception {
		Country nationality1 = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		Country nationality2 = new CountryBuilder().code("bb").name("bbbbb").toCountry();
		Country nationality3 = new CountryBuilder().code("cc").name("ccccc").toCountry();
		Country nationality4 = new CountryBuilder().code("dd").name("ddddd").toCountry();
		
		sessionFactory.getCurrentSession().save(nationality1);
		sessionFactory.getCurrentSession().save(nationality2);
		sessionFactory.getCurrentSession().save(nationality3);
		sessionFactory.getCurrentSession().save(nationality4);

		flushAndClearSession();
		
		PersonalDetails personalDetails = new PersonalDetailsBuilder().maternalGuardianNationalities(nationality1, nationality2).candiateNationalities(nationality4).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.englishFirstLanguage(false).requiresVisa(false).phoneNumber("abc")
				.lastName("lastname").residenceCountry(country1).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getMaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getMaternalGuardianNationalities().containsAll(Arrays.asList(nationality1,nationality2)));
		reloadedDetails.getMaternalGuardianNationalities().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getMaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getMaternalGuardianNationalities().containsAll(Arrays.asList(nationality1)));

		reloadedDetails.getMaternalGuardianNationalities().add(nationality3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getMaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getMaternalGuardianNationalities().containsAll(Arrays.asList(nationality1, nationality3)));
		
	}
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithPaternalGuardianNationalities() throws Exception {
		Country nationality1 = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		Country nationality2 = new CountryBuilder().code("bb").name("bbbbb").toCountry();
		Country nationality3 = new CountryBuilder().code("cc").name("ccccc").toCountry();
		Country nationality4 = new CountryBuilder().code("dd").name("ddddd").toCountry();
		
		sessionFactory.getCurrentSession().save(nationality1);
		sessionFactory.getCurrentSession().save(nationality2);
		sessionFactory.getCurrentSession().save(nationality3);
		sessionFactory.getCurrentSession().save(nationality4);

		flushAndClearSession();
		
		PersonalDetails personalDetails = new PersonalDetailsBuilder().paternalGuardianNationalities(nationality1, nationality2).candiateNationalities(nationality4).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.englishFirstLanguage(false).requiresVisa(false).phoneNumber("abc")
				.lastName("lastname").residenceCountry(country1).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		
		PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getPaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getPaternalGuardianNationalities().containsAll(Arrays.asList(nationality1,nationality2)));
		reloadedDetails.getPaternalGuardianNationalities().remove(nationality2);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getPaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getPaternalGuardianNationalities().containsAll(Arrays.asList(nationality1)));

		reloadedDetails.getPaternalGuardianNationalities().add(nationality3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getPaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getPaternalGuardianNationalities().containsAll(Arrays.asList(nationality1, nationality3)));
	}
	
	
	@Before
	public void setUp() {
		super.setUp();

		country1 = new CountryBuilder().code("AA").name("AA").toCountry();
		country2 = new CountryBuilder().code("CC").name("CC").toCountry();
		save(country1, country2);

		Program program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		Project project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(program, project);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).project(project).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
}
