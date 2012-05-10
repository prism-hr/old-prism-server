package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;

	@Test
	public void shouldGetListOfVisibleApplicationsFromDAO() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(form));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(user);
		Assert.assertTrue(visibleApplications.contains(form));
		Assert.assertEquals(1, visibleApplications.size());
	}

	@Test
	public void shouldGetAllApplicationsDueAndUpdatedNotificationToAdmin() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2)
				.toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
		assertSame(applicationsList, appsDueUpdateNotification);
	}

	
	@Test
	public void shouldGetApplicationsOrderedBysubmissionDateThenCreationDateFirst() throws  ParseException {
		
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).appDate(format.parse("01 01 2012")).toApplicationForm();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).appDate(format.parse("01 01 2012")).submittedDate(format.parse("01 04 2012")).toApplicationForm();
		ApplicationForm appThree = new ApplicationFormBuilder().id(3).appDate(format.parse("01 02 2012")).toApplicationForm();
		ApplicationForm appFour = new ApplicationFormBuilder().id(4).appDate(format.parse("01 02 2012")).submittedDate(format.parse("01 03 2012")).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(appOne, appTwo, appThree, appFour));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApps = applicationsService.getVisibleApplications(user);
		assertEquals(appTwo, visibleApps.get(0));
		assertEquals(appFour, visibleApps.get(1));
		assertEquals(appThree, visibleApps.get(2));
		assertEquals(appOne, visibleApps.get(3));
	}

	public void shouldGetApplicationById() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationById(234));
	}

	public void shouldCreateAndSaveNewApplicationForm() {
		Program program = new ProgramBuilder().id(1).toProgram();
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
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, program);
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());

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
