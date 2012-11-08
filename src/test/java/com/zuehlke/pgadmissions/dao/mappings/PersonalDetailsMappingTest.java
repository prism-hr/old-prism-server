package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PassportInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class PersonalDetailsMappingTest extends AutomaticRollbackTestCase {

	private Country country1;
	private Country country2;
	private Domicile country3;
	
	private ApplicationForm applicationForm;

	@Test
	public void shouldSaveAndLoadPersonalDetails() throws Exception {
        PersonalDetails personalDetails = new PersonalDetailsBuilder()
                .country(country1)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
                .email("email")
                .firstName("firstName")
                .title(Title.BROTHER)
                .gender(Gender.MALE)
                .lastName("lastname")
                .residenceDomicile(country3)
                .requiresVisa(true)
                .englishFirstLanguage(true)
                .phoneNumber("abc").toPersonalDetails();
        
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
		assertTrue(reloadedDetails.getRequiresVisa());
		assertTrue(reloadedDetails.getEnglishFirstLanguage());
		assertEquals(personalDetails.getPhoneNumber(), reloadedDetails.getPhoneNumber());
	}

	@Test
    public void shouldSaveAndLoadPersonalDetailsWithPassportInformation() throws Exception {
	    PassportInformation passportInformation = new PassportInformationBuilder().passportNumber("000")
	            .nameOnPassport("Kevin Francis Denver")
	            .passportExpiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20))
	            .toPassportInformation();
	    
	    PersonalDetails personalDetails = new PersonalDetailsBuilder()
                .country(country1)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
                .email("email")
                .firstName("firstName")
                .title(Title.BROTHER)
                .gender(Gender.MALE)
                .lastName("lastname")
                .residenceDomicile(country3)
                .requiresVisa(true)
                .englishFirstLanguage(true)
                .phoneNumber("abc")
                .applicationForm(applicationForm)
                .toPersonalDetails();
	    
	    personalDetails.setPassportInformation(passportInformation);
	    
	    sessionFactory.getCurrentSession().save(personalDetails);
	    
        flushAndClearSession();
        
        PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
        
        assertNotSame(personalDetails, reloadedDetails);
        
        assertEquals(personalDetails.getPassportInformation(), reloadedDetails.getPassportInformation());
	}
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithLanguageQualifications() throws ParseException {
        LanguageQualification languageQualification1 = new LanguageQualificationBuilder().dateOfExamination(new Date())
                .examTakenOnline(false).languageQualification(LanguageQualificationEnum.OTHER).listeningScore("1")
                .otherQualificationTypeName("FooBar").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                .toLanguageQualification();
        
        LanguageQualification languageQualification2 = new LanguageQualificationBuilder().dateOfExamination(new Date())
                .examTakenOnline(false).languageQualification(LanguageQualificationEnum.OTHER).listeningScore("1")
                .otherQualificationTypeName("FooBar").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                .toLanguageQualification();

        PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email")
                .firstName("firstName").title(Title.BROTHER).gender(Gender.MALE).lastName("lastname")
                .residenceDomicile(country3).requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc")
                .applicationForm(applicationForm).toPersonalDetails();
        
        personalDetails.addLanguageQualification(languageQualification1);
        personalDetails.addLanguageQualification(languageQualification2);
        
        sessionFactory.getCurrentSession().save(personalDetails);
        
        flushAndClearSession();
        
        PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
        
        assertNotSame(personalDetails, reloadedDetails);
        
        assertNotNull(reloadedDetails.getLanguageQualifications().get(0));
        
        assertNotNull(reloadedDetails.getLanguageQualifications().get(0).getPersonalDetails());
        
        reloadedDetails.getLanguageQualifications().remove(0);
        
        sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
        
        flushAndClearSession();
        
        reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
        
        assertNotSame(personalDetails, reloadedDetails);
        
        assertEquals(1, reloadedDetails.getLanguageQualifications().size());
	}
	
	@Test
	public void shouldSaveAndLoadPersonalDetailsWithCandiateNationalities() throws Exception {
		Language nationality1 = new LanguageBuilder().name("aaaaa").code("aa").enabled(true).toLanguage();
		Language nationality2 = new LanguageBuilder().name("bbbbb").code("bb").enabled(true).toLanguage();
		Language nationality3 = new LanguageBuilder().name("ccccc").code("cc").enabled(true).toLanguage();
		Language nationality4 = new LanguageBuilder().name("ddddd").code("dd").enabled(true).toLanguage();
		
		sessionFactory.getCurrentSession().save(nationality1);
		sessionFactory.getCurrentSession().save(nationality2);
		sessionFactory.getCurrentSession().save(nationality3);
		sessionFactory.getCurrentSession().save(nationality4);
		

		flushAndClearSession();
		
		PersonalDetails personalDetails = new PersonalDetailsBuilder().candiateNationalities(nationality1, nationality2).country(country1)
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).email("email").firstName("firstName").title(Title.MR).gender(Gender.MALE)
				.englishFirstLanguage(false).requiresVisa(false).phoneNumber("abc")
				.lastName("lastname").residenceDomicile(country3).applicationForm(applicationForm)
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
	
	@Before
	public void setUp() {
		super.setUp();

		country1 = new CountryBuilder().name("AA").code("AA").enabled(true).toCountry();
		country2 = new CountryBuilder().name("CC").code("CC").enabled(true).toCountry();
		country3 = new DomicileBuilder().name("DD").code("DD").enabled(true).toDomicile();
		save(country1, country2, country3);

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();		
		save(program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
}
