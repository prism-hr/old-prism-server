package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNull;

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
import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

	
	private RegisteredUser user;
	private Program program;
	private Project project;

	@Test
	public void shouldDeleteReferee(){
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		Referee referee = new RefereeBuilder().application(application).addressCountry("dfssdf").addressLocation("sdfsdf").addressPostcode("fdsdf").email("errwe.fsd").firstname("sdsdf")
				.jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumbers(new TelephoneBuilder().telephoneNumber("3223").telephoneType(PhoneType.HOME).toTelephone())
						.relationship("ERWERWER").toReferee();
		save(application, referee);
		flushAndClearSession();
		
		Integer id = referee.getId();
		RefereeDAO refereeDAO = new RefereeDAO(sessionFactory);
		refereeDAO.delete(referee);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
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
