package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase{
	
	private ApplicationFormDAO applicationDAO;
	private RegisteredUser user;
	@Before
	public void setup(){
		applicationDAO = new ApplicationFormDAO(sessionFactory);
	}

	@Test
	public void shouldSaveAndLoadApplication() throws Exception {
		
		RegisteredUser user = new RegisteredUserBuilder().username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();
		
		save(user);
		flushAndClearSession();
		
		ApplicationForm application = new ApplicationForm();
		
		application.setCob("United Kingdom");
		application.setDob("1988/03/24");
		application.setGender("Female");
		application.setNat("British");
		application.setDescriptionOfResearch("I want to make a research on cancer");
		application.setTitle("Miss");
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
		assertEquals(application.getUser(), user);
		assertEquals(application.getDescriptionOfResearch(), reloadedApplication.getDescriptionOfResearch());
	}

	@Test
	public void shouldCheckIFApplicationIsApproved(){
		ApplicationForm application = getApplicationFormsBelongingToSameUser().get(0);
		List<ApplicationForm> approvedApplicationsById = applicationDAO.checkIfApplicationIsAlreadyApproved(application.getId());
		assertEquals(1, approvedApplicationsById.size());
	}
	
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
		user = new RegisteredUserBuilder().username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();
		save(user);
		flushAndClearSession();
		
		ApplicationForm application1 = new ApplicationForm();
		application1.setCob("United Kingdom");
		application1.setDob("1988/03/24");
		application1.setGender("Female");
		application1.setNat("British");
		application1.setDescriptionOfResearch("I want to make a research on cancer");
		application1.setTitle("Miss");
		application1.setUser(user);
		application1.setApproved("1");
		
		applicationDAO.save(application1);
		
		applications.add(application1);
		
		ApplicationForm application2 = new ApplicationForm();
		application2.setCob("Brazilia");
		application2.setDob("1975/01/20");
		application2.setGender("Male");
		application2.setNat("Brazilian");
		application2.setDescriptionOfResearch("I want to make a research on software engineering");
		application2.setTitle("Mr");
		application2.setUser(user);
		
		applicationDAO.save(application2);
		
		applications.add(application2);
		
		flushAndClearSession();
		
		return applications;
	}
}
