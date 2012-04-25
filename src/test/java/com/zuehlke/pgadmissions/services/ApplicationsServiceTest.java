package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationStage;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;


	@Test
	public void shouldgetListOfApplicationsForApplicant() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicant(user)).andReturn(Arrays.asList(form));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(user);
		Assert.assertTrue(visibleApplications.contains(form));
		Assert.assertEquals(1, visibleApplications.size());
	}
	
	@Test
	public void shouldGetAllApplicationsInValidationStageWithPassedValidationDueDate(){
		Calendar dateAfterTwoWeeks = Calendar.getInstance();
		dateAfterTwoWeeks.add(Calendar.WEEK_OF_MONTH, 2);
		Calendar dateBeforeTwoWeeks = Calendar.getInstance();
		dateBeforeTwoWeeks.add(Calendar.WEEK_OF_MONTH, -2);
		ApplicationForm decidedInValidationStage = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).approvedSatus(ApprovalStatus.APPROVED).validationStage(ValidationStage.TRUE).validationDueDate(new Date()).toApplicationForm();
		ApplicationForm nonSubmitted = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		ApplicationForm submittedInValidationButBeforeDueDate = new ApplicationFormBuilder().id(3).submissionStatus(SubmissionStatus.SUBMITTED).validationStage(ValidationStage.TRUE).validationDueDate(dateAfterTwoWeeks.getTime()).toApplicationForm();
		ApplicationForm submittedInValidationButAfterDueDate = new ApplicationFormBuilder().id(4).submissionStatus(SubmissionStatus.SUBMITTED).validationStage(ValidationStage.TRUE).validationDueDate(dateBeforeTwoWeeks.getTime()).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(decidedInValidationStage, nonSubmitted, submittedInValidationButAfterDueDate, submittedInValidationButBeforeDueDate));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> applications = applicationsService.getAllApplicationsStillInValidationStageAndAfterDueDate();
		EasyMock.verify(applicationFormDAOMock);
		Assert.assertEquals(1, applications.size());
		Assert.assertTrue(applications.contains(submittedInValidationButAfterDueDate));
	}

	@Test
	public void shouldGetListOfApplicationsForAssignedReviewer() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(2).username("tom").roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole())
				.toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1).reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(reviewer);
		Assert.assertEquals(1, visibleApplications.size());
		Assert.assertTrue(visibleApplications.contains(underReviewForm));
	}

	@Test
	public void shouldNotGetListOfApplicationsForUnAssignedReviewer() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(2).username("tom").roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole())
				.toUser();
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(reviewer);
		Assert.assertEquals(0, visibleApplications.size());
		Assert.assertFalse(visibleApplications.contains(underReviewForm));
	}

	@Test
	public void shouldNotGetListOfApplicationsForUnAssignedAdministrator() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(2).username("tom")
				.roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1)
				.project(new ProjectBuilder().program(new ProgramBuilder().toProgram()).toProject()).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(administrator);
		Assert.assertEquals(0, visibleApplications.size());
		Assert.assertFalse(visibleApplications.contains(underReviewForm));
	}

	@Test
	public void shouldGetListOfApplicationsForAssignedAdministrator() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(2).username("tom")
				.roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		ProgramBuilder programBuilder = new ProgramBuilder();
		programBuilder.administrators(administrator);
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1).project(new ProjectBuilder().program(programBuilder.toProgram()).toProject())
				.submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(administrator);
		Assert.assertEquals(1, visibleApplications.size());
		Assert.assertTrue(visibleApplications.contains(underReviewForm));
	}

	@Test
	public void shouldGetListOfApplicationsForSuperAdministrator() {
		RegisteredUser superAdministrator = new RegisteredUserBuilder().id(2).username("tom")
				.roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm underReviewForm = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getAllApplications()).andReturn(Arrays.asList(underReviewForm));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(superAdministrator);
		Assert.assertEquals(1, visibleApplications.size());
		Assert.assertTrue(visibleApplications.contains(underReviewForm));
	}

	@Test
	public void shouldGetMostRecentApplicationFirst() throws InterruptedException {
		ApplicationForm app1 = new ApplicationFormBuilder().id(1).submissionStatus(SubmissionStatus.SUBMITTED).appDate(new Date()).toApplicationForm();
		Thread.sleep(1000);
		ApplicationForm app2 = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).appDate(new Date()).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicant(user)).andReturn(Arrays.asList(app1, app2));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(user);
		Assert.assertEquals(2, visibleApplications.size());
		Assert.assertEquals(app2, visibleApplications.get(0));
		Assert.assertEquals(app1, visibleApplications.get(1));
	}

	public void shouldGetApplicationById() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationById(234));
	}
	
	

	public void shouldCreateAndSaveNewApplicationForm() {
		Project project = new ProjectBuilder().id(1).toProject();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).toUser();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock) {

			@Override
			ApplicationForm newApplicationForm() {
				return newApplicationForm;
			}
		};
		applicationFormDAOMock.save(newApplicationForm);
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, project);
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(project, returnedForm.getProject());

	}

	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationsService = new ApplicationsService(applicationFormDAOMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
