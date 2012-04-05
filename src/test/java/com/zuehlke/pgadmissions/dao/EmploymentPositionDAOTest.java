package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class EmploymentPositionDAOTest extends AutomaticRollbackTestCase{
	private RegisteredUser user;
	private Program program;
	private Project project;

	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		EmploymentPositionDAO positionDAO = new EmploymentPositionDAO();
		EmploymentPosition position = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		positionDAO.delete(position);
	}
	
	
	@Test
	public void shouldDeleteEmploymentPosition(){
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().employerAdress("Address").isCompleted(CheckedStatus.YES).application(application).employerName("fr").endDate(new Date()).language(null).remit("dfsfsd").startDate(new Date()).position("rerew").toEmploymentPosition();		
		save(application, employmentPosition);
		flushAndClearSession();
		
		Integer id = employmentPosition.getId();
		EmploymentPositionDAO dao = new EmploymentPositionDAO(sessionFactory);
		dao.delete(employmentPosition);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(EmploymentPosition.class, id));
	}
	
	@Test
	public void shouldSaveEmployemnt(){
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		save(application);
		flushAndClearSession();
		
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().employerAdress("Address").isCompleted(CheckedStatus.YES).application(application).employerName("fr").endDate(new Date()).language(null).remit("dfsfsd").startDate(new Date()).position("rerew").toEmploymentPosition();
		
		EmploymentPositionDAO dao = new EmploymentPositionDAO(sessionFactory);
		dao.save(employmentPosition);
		flushAndClearSession();
		
		assertEquals(employmentPosition, sessionFactory.getCurrentSession().get(EmploymentPosition.class, employmentPosition.getId()));
	}

	@Test
	public void shouldGetEmploymentById(){
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().employerAdress("Address").isCompleted(CheckedStatus.YES).application(application).employerName("fr").endDate(new Date()).language(null).remit("dfsfsd").startDate(new Date()).position("rerew").toEmploymentPosition();
		save(application, employmentPosition);
		flushAndClearSession();
		
		EmploymentPositionDAO dao = new EmploymentPositionDAO(sessionFactory);
		EmploymentPosition reloadedPosistion = dao.getEmploymentPositionById(employmentPosition.getId());
		assertEquals(employmentPosition, reloadedPosistion);
		
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

