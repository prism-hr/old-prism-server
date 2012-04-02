package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.xalan.templates.ElemOtherwise;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.Funding;

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
	
	@Test(expected=NullPointerException.class)
	public void shouldSendNullPointerException(){
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationFormDAO.save(applicationForm);
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
	public void shouldGetQualificationById() throws ParseException{
		Language language = new Language();
		language.setId(90);
		language.setName("language");
		Qualification qualification = new QualificationBuilder().id(17)
				.q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).q_level(QualificationLevel.COLLEGE).q_grade("").q_institution("")
				.q_language_of_study(language).q_name_of_programme("").isCompleted(CheckedStatus.YES)
				.q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();
		sessionFactory.getCurrentSession().save(language);
		sessionFactory.getCurrentSession().save(qualification);
		flushAndClearSession();
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO(sessionFactory);
		assertEquals(qualification, applicationFormDAO.getQualification(qualification.getId()));
	}
	
	@Test
	public void shouldGetFundingById() throws ParseException{
		Funding funding = new Funding();
		funding.setId(1);
		funding.setType(FundingType.SCHOLARSHIP);
		funding.setDescription("my description");
		funding.setValue("2000");
		funding.setAwardDate(new Date());
		sessionFactory.getCurrentSession().save(funding);
		flushAndClearSession();
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO(sessionFactory);
		assertEquals(funding, applicationFormDAO.getFundingById(funding.getId()));
	}
	
	@Test
	public void shouldGetEmploymentpositionById() throws ParseException{
		EmploymentPosition position = new EmploymentPositionBuilder().id(1).employer("eployer").remit("remit")
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).isCompleted(CheckedStatus.YES)
				.title("title").endDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/10/10")).toEmploymentPosition();
		sessionFactory.getCurrentSession().save(position);
		flushAndClearSession();
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO(sessionFactory);
		assertEquals(position, applicationFormDAO.getEmploymentById(position.getId()));
	}
	
	@Test
	public void shouldGetAddressById() throws ParseException{
		Address address = new AddressBuilder().contactAddress(AddressStatus.YES)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).purpose(AddressPurpose.EDUCATION).postCode("wc1").location("UK").id(1).toAddress();
		sessionFactory.getCurrentSession().save(address);
		flushAndClearSession();
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO(sessionFactory);
		assertEquals(address, applicationFormDAO.getAdddressById(address.getId()));
	}
	
	@Test
	public void shouldGetRefereeById() throws ParseException{
		Referee referee = new RefereeBuilder().firstname("mark").relationship("friend").lastname("marky").email("test@test.com").id(1).toReferee();
		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();
		ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO(sessionFactory);
		assertEquals(referee, applicationFormDAO.getRefereeById(referee.getId()));
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
		qualification1.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualificationGrade("");
		qualification1.setQualificationInstitution("");
		
		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		qualification1.setQualificationLanguage(languageDAO.getLanguageById(1));
		qualification1.setQualificationLevel(QualificationLevel.COLLEGE);
		qualification1.setQualificationProgramName("");		
		qualification1.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification1.setQualificationType("");
		
		qualifications.add(qualification1);
		
		Qualification qualification2 = new Qualification();	
		qualification2.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualificationGrade("");
		qualification2.setQualificationInstitution("");
		qualification2.setQualificationLanguage(languageDAO.getLanguageById(1));
		qualification2.setQualificationLevel(QualificationLevel.COLLEGE);
		qualification2.setQualificationProgramName("");		
		qualification2.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
		qualification2.setQualificationType("");
		
		
		qualifications.add(qualification1);
		return qualifications;
	}
}
