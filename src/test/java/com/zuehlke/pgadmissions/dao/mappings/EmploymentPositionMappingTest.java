package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

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
				.remit("remit").position("position").startDate(startDate).address1("address").country(country).current(true)
				.toEmploymentPosition();

		save(employment);
		Integer id = employment.getId();

		assertNotNull(id);
		EmploymentPosition reloadedEmployment = (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, id);
		assertSame(employment, reloadedEmployment);

		flushAndClearSession();
		reloadedEmployment = (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, id);
		assertNotSame(employment, reloadedEmployment);
		assertEquals(employment.getId(), reloadedEmployment.getId());

		assertEquals(applicationForm.getId(), reloadedEmployment.getApplication().getId());
		assertEquals("employer", reloadedEmployment.getEmployerName());
		assertEquals(endDate, reloadedEmployment.getEndDate());
		assertEquals(startDate, reloadedEmployment.getStartDate());
		assertEquals("remit", reloadedEmployment.getRemit());
		assertEquals("position", reloadedEmployment.getPosition());
		assertEquals("address", reloadedEmployment.getEmployerAddress().getLocationString());
		assertEquals(country.getId(), reloadedEmployment.getEmployerAddress().getCountry().getId());
		assertTrue(reloadedEmployment.isCurrent());
	}

	@Before
	public void prepare() {
		languageDAO = new LanguageDAO(sessionFactory);
		countriesDAO = new CountriesDAO(sessionFactory);
		Program program = new ProgramBuilder().code("doesntexist").title("another title").build();		
		save(program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);
		flushAndClearSession();
	}

}
