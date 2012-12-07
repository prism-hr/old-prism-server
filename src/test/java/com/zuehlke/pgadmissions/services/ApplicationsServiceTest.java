package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;
	
	@Before
	public void setup() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationsService = new ApplicationsService(applicationFormDAOMock, null);
	}

	@Test
	public void shouldGetAllApplicationsDueAndUpdatedNotificationToAdmin() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2).toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueUpdateNotification);
	}
	
	@Test
	public void shouldGetAllApplicationsDueRegistryNotification() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2).toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueRegistryNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueRegistryNotification = applicationsService.getApplicationsDueRegistryNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueRegistryNotification);
	}
	
	@Test
	public void shouldGetAllApplicationsDueApprovalRestartRequestNotification() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2).toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueApprovalRequestNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueNotification = applicationsService.getApplicationsDueApprovalRestartRequestNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueNotification);
	}
	
	@Test
	public void shouldGetAllApplicationsDueApprovalRestartRequestReminder() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2).toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationDueApprovalRestartRequestReminder()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueNotification = applicationsService.getApplicationsDueApprovalRestartRequestReminder();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueNotification);
	}
	
	@Test
	public void shouldGetApplicationById() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationById(234));
		EasyMock.verify(applicationFormDAOMock);
	}

	@Test
	public void shouldGetApplicationbyApplicationNumber() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber("ABC")).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationByApplicationNumber("ABC"));
		EasyMock.verify(applicationFormDAOMock);
	}

	@Test
	public void shouldCreateAndSaveNewApplicationFormWithoutBatchDeadlineProjectOrResearchHomePage() throws ParseException {
		Program program = new ProgramBuilder().code("KLOP").id(1).toProgram();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).toUser();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock, null) {

			@Override
			ApplicationForm newApplicationForm() {
				return newApplicationForm;
			}
		};
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23);
		applicationFormDAOMock.save(newApplicationForm);
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, program, null, null, null);
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-2012-000024", returnedForm.getApplicationNumber());
		assertNull(returnedForm.getBatchDeadline());
	}
	
	@Test
	public void shouldCreateAndSaveNewApplicationFormWithBatchDeadlineProjectAndResearchHomePage() throws ParseException {
		Program program = new ProgramBuilder().code("KLOP").id(1).toProgram();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).toUser();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock, null) {
			
			@Override
			ApplicationForm newApplicationForm() {
				return newApplicationForm;
			}
		};
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23);
		applicationFormDAOMock.save(newApplicationForm);
		EasyMock.replay(applicationFormDAOMock);
		Date batchDeadline = new SimpleDateFormat("dd/MM/yyyy").parse("12/12/2012");
		String projectTitle = "This is the project title";
		String researchHomePage ="researchHomePage";
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, program, batchDeadline, projectTitle, researchHomePage);
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-2012-000024", returnedForm.getApplicationNumber());
		assertEquals(batchDeadline, returnedForm.getBatchDeadline());
		assertEquals(projectTitle, returnedForm.getProjectTitle());
		assertEquals("http://" + researchHomePage, returnedForm.getResearchHomePage());
	}


	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
