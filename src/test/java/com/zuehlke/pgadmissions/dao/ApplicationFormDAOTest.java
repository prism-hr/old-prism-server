package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
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
	private ApplicationForm application;
	
	
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
		
		application.setApplicant(user);
		
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
		assertEquals(application.getApplicant(), user);	}
	
	@Test
	public void shouldFindAllApplicationsBelongingToSameUser(){
		List<ApplicationForm> applications = getApplicationFormsBelongingToSameUser();
		List<ApplicationForm> applicationsByUser = applicationDAO.getApplicationsByApplicant(user);
		assertNotSame(applications, applicationsByUser);
		assertEquals(applications, applicationsByUser);
		assertEquals(applications.get(0).getApplicant(), applications.get(1).getApplicant());
	}
	
	@Test
	public void shouldFindAllQualificationsBelongingToSameApplication() throws ParseException{
		List<Qualification> qualifications = getQualificationsBelongingToSameApplication();
		applicationDAO.save(application);
		flushAndClearSession();
		List<Qualification> qualificationsByApplication = applicationDAO.getQualificationsByApplication(application);
		assertNotSame(qualifications, qualificationsByApplication);
		assertEquals(qualifications.get(0).getApplication(), qualifications.get(1).getApplication());
	}
	
	@Test 
	public void shouldAssignDateToApplicationForm() {
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		
		applicationDAO.save(application);
		
		Integer id = application.getId();
		ApplicationForm reloadedApplication = applicationDAO.get(id);
		assertNotNull(reloadedApplication.getApplicationTimestamp());
		
	}
	
	public List<ApplicationForm> getApplicationFormsBelongingToSameUser(){
		List<ApplicationForm> applications = new ArrayList<ApplicationForm>();

		
		ApplicationForm application1 = new ApplicationForm();	
		application1.setApplicant(user);
		application1.setProject(project);
		application1.setApprovalStatus(ApprovalStatus.APPROVED);
		
		applicationDAO.save(application1);
		
		applications.add(application1);
		
		ApplicationForm application2 = new ApplicationForm();
		application2.setApplicant(user);
		application2.setProject(project);
		
		applicationDAO.save(application2);
		
		applications.add(application2);
		
		flushAndClearSession();
		
		return applications;
	}
	
	public List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException{
		
		application = new ApplicationForm();
		application.setApplicant(user);
		application.setProject(project);
		
		List<Qualification> qualifications = new ArrayList<Qualification>();
		
		
		Qualification qualification1 = new Qualification();	
		qualification1.setAward_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setGrade("");
		qualification1.setInstitution("");
		qualification1.setLanguage_of_study("");
		qualification1.setLevel("");
		qualification1.setName_of_programme("");
		qualification1.setScore("");
		qualification1.setStart_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualification_type("");
		
		qualifications.add(qualification1);
		
		Qualification qualification2 = new Qualification();	
		qualification2.setAward_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setGrade("");
		qualification2.setInstitution("");
		qualification2.setLanguage_of_study("");
		qualification2.setLevel("");
		qualification2.setName_of_programme("");
		qualification2.setScore("");
		qualification2.setStart_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualification_type("");
		
		
		qualifications.add(qualification1);
		return qualifications;
	}
}
