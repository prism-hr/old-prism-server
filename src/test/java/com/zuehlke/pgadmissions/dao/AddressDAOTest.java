package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class AddressDAOTest extends AutomaticRollbackTestCase {

	
	private RegisteredUser user;
	private Program program;
	private Project project;

	@Test
	public void shouldDeleteAddress(){
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Country countryById = countriesDAO.getCountryById(1);
		Address address = new AddressBuilder().application(application).country(countryById).location("1 Main Street").toAddress();
		save(application, address);
		flushAndClearSession();
		
		Integer id = address.getId();
		AddressDAO addressDAO = new AddressDAO(sessionFactory);
		addressDAO.delete(address);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Address.class, id));
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldSendNullPointerException(){
		AddressDAO addressDAO = new AddressDAO();
		Address address = new AddressBuilder().id(1).toAddress();
		addressDAO.delete(address);
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
