package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;
	private Project project;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		RefereeDAO refereeDAO = new RefereeDAO();
		refereeDAO.getRefereeById(1);
	}

	@Test
	public void shouldDeleteReferee() {
	
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder()
				.application(application)
				.addressCountry(countriesDAO.getCountryById(1))
				.addressLocation("sdfsdf")
				.email("errwe.fsd")
				.firstname("sdsdf")
				.jobEmployer("sdfsdf")
				.jobTitle("fsdsd")
				.lastname("fsdsdf")
				.phoneNumber("hallihallo")
				.toReferee();
		save(application, referee);
		flushAndClearSession();

		Integer id = referee.getId();
		RefereeDAO refereeDAO = new RefereeDAO(sessionFactory);
		refereeDAO.delete(referee);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
	}

	@Test
	public void shouldSaveReferee() throws ParseException {
		Referee referee = new RefereeBuilder().email("email").firstname("name")
				.lastname("last").addressLocation("UK").phoneNumber("hallihallo")
				.toReferee();
		flushAndClearSession();

		RefereeDAO refereeDAO = new RefereeDAO(sessionFactory);
		refereeDAO.save(referee);
		Assert.assertNotNull(referee.getId());
	}

	@Test
	public void shouldGetRefereeById() {
		Referee referee = new RefereeBuilder().email("email").firstname("name")
				.lastname("last").addressLocation("UK")	.phoneNumber("hallihallo")
				.toReferee();

		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();

		RefereeDAO refereeDAO = new RefereeDAO(sessionFactory);
		assertEquals(referee, refereeDAO.getRefereeById(referee.getId()));

	}

	@Test
	public void shouldGetProgramByActivationCode() {
		Referee referee = new RefereeBuilder().activationCode("abcde").email("email").firstname("name")
				.lastname("last").addressLocation("UK").phoneNumber("hallihallo")
				.toReferee();

		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();

		RefereeDAO refereeDAO = new RefereeDAO(sessionFactory);
		assertEquals(referee, refereeDAO.getRefereeByActivationCode("abcde"));

	}

	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
				.email("email@test.com").username("username")
				.password("password").accountNonExpired(false)
				.accountNonLocked(false).credentialsNonExpired(false)
				.enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist")
				.description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis")
				.description("hello").title("title two").program(program)
				.toProject();
		save(user, program, project);

		flushAndClearSession();
	}
}
