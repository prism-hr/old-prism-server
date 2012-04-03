package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
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

public class PersonalDetailDAOTest extends AutomaticRollbackTestCase {

	private Country country;
	private ApplicationForm applicationForm;

	
	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		ProgrammeDetailDAO personalDetailsDAO = new ProgrammeDetailDAO();
		personalDetailsDAO.getProgrammeDetailWithId(1);
	}
	
	@Test
	public void shouldGetPersonalDetailsById() throws ParseException {
		PersonalDetail personalDetails = new PersonalDetailsBuilder().country(country).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").gender(Gender.MALE).lastName("lastname").residenceCountry(country)
				.applicationForm(applicationForm).toPersonalDetails();
		sessionFactory.getCurrentSession().save(personalDetails);
		
		flushAndClearSession();
		
		
		PersonalDetailDAO personalDetailDAO = new PersonalDetailDAO(sessionFactory);
		assertEquals(personalDetails, personalDetailDAO.getPersonalDetailsById(personalDetails.getId()));
	}
	
	@Test
	public void shouldSavePersonalDetails() throws ParseException {
		PersonalDetailDAO personalDetailDAO = new PersonalDetailDAO(sessionFactory);
		PersonalDetail personalDetails = new PersonalDetailsBuilder().country(country).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").gender(Gender.MALE).lastName("lastname").residenceCountry(country)
				.applicationForm(applicationForm).toPersonalDetails();
		
		personalDetailDAO.save(personalDetails);
		assertNotNull(personalDetails.getId());		
		flushAndClearSession();
		
		assertEquals(personalDetails, personalDetailDAO.getPersonalDetailsById(personalDetails.getId()));
		
	}
	
	@Before
	public void setUp() {
		super.setUp();
		country = new CountryBuilder().code("AA").name("AA").toCountry();
		
		save(country);

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
