package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase{
	
	private ApplicationFormDAO applicationDAO;
	private RegisteredUser user;
	private Program program;
	private Project project;
	
	
	@Before
	public void setup() {
		applicationDAO = new ApplicationFormDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(user, program, project);

		flushAndClearSession();
		
	}

	@Test
	public void shouldSaveAndLoadApplication() throws Exception {
		
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		
		application.setUser(user);
		
		assertNull(application.getId());
		
		applicationDAO.save(application);
		
		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = applicationDAO.get(id);
		assertSame(application, reloadedApplication);
		
		flushAndClearSession();

		reloadedApplication = applicationDAO.get(id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);
		assertEquals(application.getUser(), user);	}
	
	@Test
	public void shouldFindAllApplicationsBelongingToSameUser(){
		List<ApplicationForm> applications = getApplicationFormsBelongingToSameUser();
		List<ApplicationForm> applicationsByUser = applicationDAO.getApplicationsByUser(user);
		assertNotSame(applications, applicationsByUser);
		assertEquals(applications, applicationsByUser);
		assertEquals(applications.get(0).getUser(), applications.get(1).getUser());
	}
	
	public List<ApplicationForm> getApplicationFormsBelongingToSameUser(){
		List<ApplicationForm> applications = new ArrayList<ApplicationForm>();

		
		ApplicationForm application1 = new ApplicationForm();	
		application1.setUser(user);
		application1.setProject(project);
		application1.setApprovalStatus(ApprovalStatus.APPROVED);
		
		applicationDAO.save(application1);
		
		applications.add(application1);
		
		ApplicationForm application2 = new ApplicationForm();
		application2.setUser(user);
		application2.setProject(project);
		
		applicationDAO.save(application2);
		
		applications.add(application2);
		
		flushAndClearSession();
		
		return applications;
	}
	
	
}
