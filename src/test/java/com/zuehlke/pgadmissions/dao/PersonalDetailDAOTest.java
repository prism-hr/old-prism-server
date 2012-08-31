package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class PersonalDetailDAOTest extends AutomaticRollbackTestCase {

	private Country country;
	private ApplicationForm applicationForm;
	private Ethnicity ethnicity;
	private Disability disability;
	private Domicile domicile;

	
	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		ProgrammeDetailDAO personalDetailsDAO = new ProgrammeDetailDAO();
		personalDetailsDAO.getProgrammeDetailWithId(1);
	}
	
	@Test
	public void shouldGetPersonalDetailsById() throws ParseException {
		PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").title(Title.MR).gender(Gender.MALE).lastName("lastname").residenceDomicile(domicile)
				.requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc")
				.applicationForm(applicationForm).toPersonalDetails();
		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		
		
		PersonalDetailDAO personalDetailDAO = new PersonalDetailDAO(sessionFactory);
		assertEquals(personalDetails, personalDetailDAO.getPersonalDetailsById(personalDetails.getId()));
	}
	
	@Test
	public void shouldSavePersonalDetails() throws ParseException {
		PersonalDetailDAO personalDetailDAO = new PersonalDetailDAO(sessionFactory);
		PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc")
				.email("email").firstName("firstName").title(Title.MR).gender(Gender.MALE).lastName("lastname").residenceDomicile(domicile)
				.applicationForm(applicationForm)
				.ethnicity(ethnicity).disability(disability).toPersonalDetails();
		personalDetailDAO.save(personalDetails);
		assertNotNull(personalDetails.getId());		
		flushAndClearSession();

		PersonalDetails savedDetails = personalDetailDAO.getPersonalDetailsById(personalDetails.getId());
		assertEquals(personalDetails, savedDetails);

		Ethnicity savedEth = savedDetails.getEthnicity();
		Assert.assertNotNull(savedEth);
		Assert.assertEquals("AAAA", savedEth.getName());

		Disability savedDis = savedDetails.getDisability();
		Assert.assertNotNull(savedDis);
		Assert.assertEquals("BBBB", savedDis.getName());
	}

	@Test
	public void shouldSaveEthnicityDisability() throws ParseException {
		PersonalDetailDAO personalDetailDAO = new PersonalDetailDAO(sessionFactory);
		Ethnicity eth = new EthnicityBuilder().name("AAAA").enabled(true).toEthnicity();
		save(eth);

		Disability dis = new DisabilityBuilder().name("BBBB").enabled(true).toDisability();
		save(dis);

		PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country)//
				.dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))//
				.requiresVisa(true).englishFirstLanguage(true).phoneNumber("abc").email("email")//
				.firstName("firstName").title(Title.MR).gender(Gender.MALE).lastName("lastname")//
				.residenceDomicile(domicile).ethnicity(eth).disability(dis).applicationForm(applicationForm).toPersonalDetails();

		personalDetailDAO.save(personalDetails);
		flushAndClearSession();

		PersonalDetails storedPD = personalDetailDAO.getPersonalDetailsById(personalDetails.getId());
		Ethnicity storedEth = storedPD.getEthnicity();
		assertNotNull(storedEth);
		assertEquals("AAAA", storedEth.getName());

		Disability storedDis = storedPD.getDisability();
		assertNotNull(storedDis);
		assertEquals("BBBB", storedDis.getName());
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();
		country = new CountryBuilder().name("AA").enabled(true).toCountry();
		domicile = new DomicileBuilder().name("BB").enabled(true).toDomicile();
		ethnicity = new EthnicityBuilder().name("AAAA").enabled(true).toEthnicity();
		disability = new DisabilityBuilder().name("BBBB").enabled(true).toDisability();

		save(country);
		save(ethnicity);
		save(disability);
		save(domicile);

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
