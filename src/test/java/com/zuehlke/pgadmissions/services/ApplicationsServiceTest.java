package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
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

import com.zuehlke.pgadmissions.dao.AddressDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;
	private AddressDAO addressDAOMock;
	private QualificationDAO qualificationDAOMock;
	private FundingDAO fundingDAOMock;
	private EmploymentPositionDAO employmentDAOMock;
	private RefereeDAO refereeDAOMock;

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

	@Test
	public void shouldDelegateDeleteAddressToDAO() {
		Address address = new AddressBuilder().id(1).toAddress();
		addressDAOMock.delete(address);
		EasyMock.replay(addressDAOMock);
		applicationsService.deleteAddress(address);
		EasyMock.verify(addressDAOMock);
	}

	@Test
	public void shouldDelegateApplicationSaveToDAO() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		applicationFormDAOMock.save(application);
		EasyMock.replay(application, applicationFormDAOMock);
		applicationsService.save(application);
		EasyMock.verify(applicationFormDAOMock);
	}

	@Test
	public void shouldGetApplicationById() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationById(234));
	}

	@Test
	public void shouldGetAddressById() {
		Address address = EasyMock.createMock(Address.class);
		EasyMock.expect(applicationFormDAOMock.getAdddressById(23)).andReturn(address);

		EasyMock.replay(address, applicationFormDAOMock);
		Assert.assertEquals(address, applicationsService.getAddressById(23));
	}

	@Test
	public void shouldCreateAndSaveNewApplicationForm() {
		Project project = new ProjectBuilder().id(1).toProject();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).toUser();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock, addressDAOMock, qualificationDAOMock, fundingDAOMock, employmentDAOMock,
				refereeDAOMock) {

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
		addressDAOMock = EasyMock.createMock(AddressDAO.class);
		qualificationDAOMock = EasyMock.createMock(QualificationDAO.class);
		fundingDAOMock = EasyMock.createMock(FundingDAO.class);
		employmentDAOMock = EasyMock.createMock(EmploymentPositionDAO.class);
		refereeDAOMock = EasyMock.createMock(RefereeDAO.class);
		applicationsService = new ApplicationsService(applicationFormDAOMock, addressDAOMock, qualificationDAOMock, fundingDAOMock, employmentDAOMock,
				refereeDAOMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
