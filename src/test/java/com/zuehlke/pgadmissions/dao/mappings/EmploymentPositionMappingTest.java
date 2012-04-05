package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class EmploymentPositionMappingTest extends AutomaticRollbackTestCase {

	private LanguageDAO languageDAO;
	private CountriesDAO countriesDAO;
	private ApplicationForm applicationForm;

	@Test
	public void shouldSaveAndLoadEmploymentPosition() throws ParseException {
		Language language = languageDAO.getAllLanguages().get(0);
		Country country = countriesDAO.getAllCountries().get(0);
		Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/12/2011");
		Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2011");
		EmploymentPosition employment = new EmploymentPositionBuilder().application(applicationForm).employerName("employer").endDate(endDate)
				.isCompleted(CheckedStatus.YES).language(language).remit("remit").position("position")
				.startDate(startDate).employerAdress("address").employerCountry(country).toEmploymentPosition();
		
		save(employment);
		Integer id = employment.getId();
		
		assertNotNull(id);
		EmploymentPosition reloadedEmployment = (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, id);		
		assertSame(employment, reloadedEmployment);
		
		flushAndClearSession();
		reloadedEmployment = (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, id);		
		assertNotSame(employment, reloadedEmployment);
		assertEquals(employment, reloadedEmployment);
		
		assertEquals(applicationForm, reloadedEmployment.getApplication());
		assertEquals("employer", reloadedEmployment.getEmployerName());
		assertEquals(endDate, reloadedEmployment.getEndDate());
		assertEquals(startDate, reloadedEmployment.getStartDate());
		assertEquals(CheckedStatus.YES, reloadedEmployment.getCompleted());
		assertEquals(language, reloadedEmployment.getLanguage());
		assertEquals("remit", reloadedEmployment.getRemit());
		assertEquals("position", reloadedEmployment.getPosition());
		assertEquals("address", reloadedEmployment.getEmployerAddress());
		assertEquals(country, reloadedEmployment.getEmployerCountry());
		
	}

	@Before
	public void setUp() {
		super.setUp();
		languageDAO = new LanguageDAO(sessionFactory);
		countriesDAO = new CountriesDAO(sessionFactory);
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
