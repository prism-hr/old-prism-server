package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class FundingDAOTest extends AutomaticRollbackTestCase {

	
	private RegisteredUser user;
	private Program program;
	private Project project;

	@Test
	public void shouldDeleteFunding(){
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		Funding funding = new FundingBuilder().application(application).awardDate(new Date()).description("fi").type(FundingType.EMPLOYER).value("34432").toFunding();		
		save(application, funding);
		flushAndClearSession();
		
		Integer id = funding.getId();
		FundingDAO fundingDAO = new FundingDAO(sessionFactory);
		fundingDAO.delete(funding);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Funding.class, id));
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
