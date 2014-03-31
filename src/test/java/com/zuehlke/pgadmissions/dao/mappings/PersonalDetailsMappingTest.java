package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class PersonalDetailsMappingTest extends AutomaticRollbackTestCase {

    private Country country1;
    private Country country2;
    private Domicile country3;

    private ApplicationForm applicationForm;
    private Language nationality1;
    private Language nationality2;
    private Language nationality3;

    @Test
    public void shouldSaveAndLoadPersonalDetails() throws Exception {
        PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
                .title(Title.BROTHER).gender(Gender.MALE).residenceDomicile(country3).requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc")
                .firstNationality(nationality1).build();

        sessionFactory.getCurrentSession().save(personalDetails);

        assertNotNull(personalDetails.getId());

        Integer id = personalDetails.getId();
        PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, id);

        assertSame(personalDetails, reloadedDetails);

        flushAndClearSession();

        reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, id);

        assertNotSame(personalDetails, reloadedDetails);
        assertEquals(personalDetails.getId(), reloadedDetails.getId());
        assertEquals(personalDetails.getApplication(), reloadedDetails.getApplication());
        assertEquals(personalDetails.getCountry().getId(), reloadedDetails.getCountry().getId());
        assertEquals(personalDetails.getDateOfBirth(), reloadedDetails.getDateOfBirth());
        assertEquals(personalDetails.getGender(), reloadedDetails.getGender());
        assertEquals(personalDetails.getResidenceCountry().getId(), reloadedDetails.getResidenceCountry().getId());
        assertTrue(reloadedDetails.getRequiresVisa());
        assertTrue(reloadedDetails.getEnglishFirstLanguage());
        assertEquals(personalDetails.getPhoneNumber(), reloadedDetails.getPhoneNumber());
    }

    @Test
    public void shouldSaveAndLoadPersonalDetailsWithLanguageQualification() throws ParseException {
        LanguageQualification languageQualification = new LanguageQualificationBuilder().examDate(new Date()).examOnline(false)
                .languageQualification(LanguageQualificationEnum.OTHER).listeningScore("1").qualificationTypeName("FooBar").overallScore("1")
                .readingScore("1").speakingScore("1").writingScore("1").build();

        PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
                .title(Title.BROTHER).gender(Gender.MALE).residenceDomicile(country3).requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc")
                .applicationForm(applicationForm).firstNationality(nationality1).build();

        personalDetails.setLanguageQualification(languageQualification);

        sessionFactory.getCurrentSession().save(personalDetails);

        flushAndClearSession();

        PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());

        assertNotSame(personalDetails, reloadedDetails);

        assertNotNull(reloadedDetails.getLanguageQualification());

        reloadedDetails.setLanguageQualification(null);

        sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);

        flushAndClearSession();

        reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());

        assertNotSame(personalDetails, reloadedDetails);

        assertNull(reloadedDetails.getLanguageQualification());
    }

    @Test
    public void shouldSaveAndLoadPersonalDetailsWithCandiateNationalities() throws Exception {
        flushAndClearSession();

        PersonalDetails personalDetails = new PersonalDetailsBuilder().firstNationality(nationality1).secondNationality(nationality2).country(country1)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980")).title(Title.MR).gender(Gender.MALE).englishFirstLanguage(false)
                .requiresVisa(false).phoneNumber("abc").residenceDomicile(country3).applicationForm(applicationForm).build();

        sessionFactory.getCurrentSession().save(personalDetails);
        flushAndClearSession();

        PersonalDetails reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());
        assertEquals(nationality1.getId(), reloadedDetails.getFirstNationality().getId());
        assertEquals(nationality2.getId(), reloadedDetails.getSecondNationality().getId());
        reloadedDetails.setSecondNationality(nationality3);

        sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
        flushAndClearSession();
        reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());

        assertEquals(nationality1.getId(), reloadedDetails.getFirstNationality().getId());
        assertEquals(nationality3.getId(), reloadedDetails.getSecondNationality().getId());
        reloadedDetails.setSecondNationality(null);

        sessionFactory.getCurrentSession().saveOrUpdate(reloadedDetails);
        flushAndClearSession();
        reloadedDetails = (PersonalDetails) sessionFactory.getCurrentSession().get(PersonalDetails.class, personalDetails.getId());

        assertEquals(nationality1.getId(), reloadedDetails.getFirstNationality().getId());
        assertEquals(null, reloadedDetails.getSecondNationality());
        flushAndClearSession();

    }

    @Before
    public void prepare() {
        country1 = new CountryBuilder().name("AA").code("AA").enabled(true).build();
        country2 = new CountryBuilder().name("CC").code("CC").enabled(true).build();
        country3 = new DomicileBuilder().name("DD").code("DD").enabled(true).build();
        save(country1, country2, country3);

        nationality1 = new LanguageBuilder().name("aaaaa").code("aa").enabled(true).build();
        nationality2 = new LanguageBuilder().name("bbbbb").code("bb").enabled(true).build();
        nationality3 = new LanguageBuilder().name("ccccc").code("cc").enabled(true).build();
        save(nationality1, nationality2, nationality3);
        
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        save(applicant);

        applicationForm = new ApplicationFormBuilder().applicant(applicant).advert(testObjectProvider.getEnabledProgram()).build();
        save(applicationForm);
        flushAndClearSession();
    }

}
