package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.domain.enums.NationalityType;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

public class PersonalDetailsMappingTest extends AutomaticRollbackTestCase {

	private Country country1;
	private Country country2;
	private ApplicationForm applicationForm;
	

	@Test
	public void shouldSaveAndLoadPersonalDetails() throws Exception {

		PersonalDetail personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").gender(Gender.MALE).lastName("lastname").residenceCountry(country2)
				.residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(applicationForm).toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		assertNotNull(personalDetails.getId());
		Integer id = personalDetails.getId();
		PersonalDetail reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, id);

		assertSame(personalDetails, reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, id);

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
		assertEquals(personalDetails.getResidenceStatus(), reloadedDetails.getResidenceStatus());

	}

	@Test
	public void shouldSaveAndLoadPersonalDetailsWithPhoneNumbers() throws Exception {
		Telephone telephone1 = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.MOBILE).toTelephone();
		Telephone telephone2 = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.HOME).toTelephone();
		Telephone telephone3 = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.WORK).toTelephone();
		PersonalDetail personalDetails = new PersonalDetailsBuilder().phoneNumbers(telephone1, telephone2).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.lastName("lastname").residenceCountry(country1).residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		assertNotNull(telephone1.getId());
		assertNotNull(telephone2.getId());
		flushAndClearSession();
		PersonalDetail reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getPhoneNumbers().size());
		assertTrue(reloadedDetails.getPhoneNumbers().containsAll(Arrays.asList(telephone1, telephone2)));

		reloadedDetails.getPhoneNumbers().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getPhoneNumbers().size());
		assertTrue(reloadedDetails.getPhoneNumbers().containsAll(Arrays.asList(telephone1)));

		reloadedDetails.getPhoneNumbers().add(telephone3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getPhoneNumbers().size());
		assertTrue(reloadedDetails.getPhoneNumbers().containsAll(Arrays.asList(telephone1, telephone3)));

	}
	


	@Test
	public void shouldSaveAndLoadPersonalDetailsWithCandiateNationalities() throws Exception {
		Country country = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		sessionFactory.getCurrentSession().save(country);

		Document document1 = new DocumentBuilder().content("aa".getBytes()).fileName("bob").toDocument();
		Document document2 = new DocumentBuilder().content("bb".getBytes()).fileName("fred").toDocument();
		save(document1, document2);

		flushAndClearSession();
		
		Nationality nationality1 = new Nationality();
		nationality1.setCountry(country);
		nationality1.setSupportingDocuments(Arrays.asList(document1, document2));
		nationality1.setType(NationalityType.CANDIDATE);
		
		Nationality nationality2 = new Nationality();
		nationality2.setCountry(country);
		nationality2.setType(NationalityType.CANDIDATE);
	
		
		Nationality nationality3 = new Nationality();
		nationality3.setCountry(country);
		nationality3.setType(NationalityType.CANDIDATE);

		Nationality nationality4 = new Nationality();
		nationality4.setCountry(country);
		nationality4.setType(NationalityType.MATERNAL_GUARDIAN);
		
		PersonalDetail personalDetails = new PersonalDetailsBuilder().candiateNationalities(nationality1, nationality2).maternalGuardianNationalities(nationality4).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.lastName("lastname").residenceCountry(country1).residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		PersonalDetail reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getCandidateNationalities().size());
		assertTrue(reloadedDetails.getCandidateNationalities().containsAll(Arrays.asList(nationality1,nationality2)));
		Integer tobeRemovedId = nationality2.getId();
		reloadedDetails.getCandidateNationalities().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getCandidateNationalities().size());
		assertTrue(reloadedDetails.getCandidateNationalities().containsAll(Arrays.asList(nationality1)));

		reloadedDetails.getCandidateNationalities().add(nationality3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getCandidateNationalities().size());
		assertTrue(reloadedDetails.getCandidateNationalities().containsAll(Arrays.asList(nationality1, nationality3)));
		
		 assertNull(sessionFactory.getCurrentSession().get(Nationality.class, tobeRemovedId));
	}
	
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithMaternalGuardianNationalities() throws Exception {
		Country country = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		sessionFactory.getCurrentSession().save(country);

		Document document1 = new DocumentBuilder().content("aa".getBytes()).fileName("bob").toDocument();
		Document document2 = new DocumentBuilder().content("bb".getBytes()).fileName("fred").toDocument();
		save(document1, document2);

		flushAndClearSession();
		
		Nationality nationality1 = new Nationality();
		nationality1.setCountry(country);
		nationality1.setSupportingDocuments(Arrays.asList(document1, document2));
		nationality1.setType(NationalityType.MATERNAL_GUARDIAN);
		
		Nationality nationality2 = new Nationality();
		nationality2.setCountry(country);
		nationality2.setType(NationalityType.MATERNAL_GUARDIAN);
	
		
		Nationality nationality3 = new Nationality();
		nationality3.setCountry(country);
		nationality3.setType(NationalityType.MATERNAL_GUARDIAN);
		
		Nationality nationality4 = new Nationality();
		nationality4.setCountry(country);
		nationality4.setType(NationalityType.CANDIDATE);
		
		
		PersonalDetail personalDetails = new PersonalDetailsBuilder().maternalGuardianNationalities(nationality1, nationality2).candiateNationalities(nationality4).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.lastName("lastname").residenceCountry(country1).residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		PersonalDetail reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getMaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getMaternalGuardianNationalities().containsAll(Arrays.asList(nationality1,nationality2)));
		Integer tobeRemovedId = nationality2.getId();
		reloadedDetails.getMaternalGuardianNationalities().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getMaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getMaternalGuardianNationalities().containsAll(Arrays.asList(nationality1)));

		reloadedDetails.getMaternalGuardianNationalities().add(nationality3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getMaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getMaternalGuardianNationalities().containsAll(Arrays.asList(nationality1, nationality3)));
		
		 assertNull(sessionFactory.getCurrentSession().get(Nationality.class, tobeRemovedId));
	}
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithPaternalGuardianNationalities() throws Exception {
		Country country = new CountryBuilder().code("aa").name("aaaaa").toCountry();
		sessionFactory.getCurrentSession().save(country);

		Document document1 = new DocumentBuilder().content("aa".getBytes()).fileName("bob").toDocument();
		Document document2 = new DocumentBuilder().content("bb".getBytes()).fileName("fred").toDocument();
		save(document1, document2);

		flushAndClearSession();
		
		Nationality nationality1 = new Nationality();
		nationality1.setCountry(country);
		nationality1.setSupportingDocuments(Arrays.asList(document1, document2));
		nationality1.setType(NationalityType.PATERNAL_GUARDIAN);
		
		Nationality nationality2 = new Nationality();
		nationality2.setCountry(country);
		nationality2.setType(NationalityType.PATERNAL_GUARDIAN);
	
		
		Nationality nationality3 = new Nationality();
		nationality3.setCountry(country);
		nationality3.setType(NationalityType.PATERNAL_GUARDIAN);
		
		Nationality nationality4 = new Nationality();
		nationality4.setCountry(country);
		nationality4.setType(NationalityType.CANDIDATE);
		
		
		PersonalDetail personalDetails = new PersonalDetailsBuilder().paternalGuardianNationalities(nationality1, nationality2).candiateNationalities(nationality4).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.lastName("lastname").residenceCountry(country1).residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		PersonalDetail reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getPaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getPaternalGuardianNationalities().containsAll(Arrays.asList(nationality1,nationality2)));
		Integer tobeRemovedId = nationality2.getId();
		reloadedDetails.getPaternalGuardianNationalities().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getPaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getPaternalGuardianNationalities().containsAll(Arrays.asList(nationality1)));

		reloadedDetails.getPaternalGuardianNationalities().add(nationality3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getPaternalGuardianNationalities().size());
		assertTrue(reloadedDetails.getPaternalGuardianNationalities().containsAll(Arrays.asList(nationality1, nationality3)));
		
		 assertNull(sessionFactory.getCurrentSession().get(Nationality.class, tobeRemovedId));
	}
	
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithLanguageProficiencies() throws Exception {
		Language language = new LanguageBuilder().name("aaaaa").toLanguage();
		sessionFactory.getCurrentSession().save(language);

		flushAndClearSession();
		
		LanguageProficiency prof1 = new LanguageProficiency();
		prof1.setLanguage(language);
		prof1.setAptitude(LanguageAptitude.ELEMENTARY);
		
		LanguageProficiency prof2 = new LanguageProficiency();
		prof2.setLanguage(language);
		prof2.setAptitude(LanguageAptitude.FULL);
		
		LanguageProficiency prof3 = new LanguageProficiency();
		prof3.setLanguage(language);
		prof3.setAptitude(LanguageAptitude.LIMITED);
		
		
		PersonalDetail personalDetails = new PersonalDetailsBuilder().languageProficiencies(prof1, prof2).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").gender(Gender.MALE)
				.lastName("lastname").residenceCountry(country1).residenceStatus(ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN).applicationForm(applicationForm)
				.toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		PersonalDetail reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getLanguageProficiencies().size());
		assertTrue(reloadedDetails.getLanguageProficiencies().containsAll(Arrays.asList(prof1,prof2)));
		Integer tobeRemovedId = prof2.getId();
		reloadedDetails.getLanguageProficiencies().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

		flushAndClearSession();
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(1, reloadedDetails.getLanguageProficiencies().size());
		assertTrue(reloadedDetails.getLanguageProficiencies().containsAll(Arrays.asList(prof1)));

		reloadedDetails.getLanguageProficiencies().add(prof3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
		flushAndClearSession();
		
		reloadedDetails = (PersonalDetail) sessionFactory.getCurrentSession().get(PersonalDetail.class, personalDetails.getId());
		assertEquals(2, reloadedDetails.getLanguageProficiencies().size());
		assertTrue(reloadedDetails.getLanguageProficiencies().containsAll(Arrays.asList(prof1, prof3)));
		
		 assertNull(sessionFactory.getCurrentSession().get(Nationality.class, tobeRemovedId));
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
