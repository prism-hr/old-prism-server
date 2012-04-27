package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class WithdrawControllerTest {
	
	private WithdrawController withdrawController;
	private ApplicationsService applicationsServiceMock;
	private RegisteredUser student;
	private UsernamePasswordAuthenticationToken authenticationToken;

	
	@Test
	public void shouldReturnApplicationsListIfWithdrawn(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		String view = withdrawController.withdrawApplicationAndGetApplicationList(form);
		assertEquals("redirect:/applications", view);
	}
	
	@Test
	public void shouldChangeStatusToWithdrawnAndSave() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicant(student).id(2).toApplicationForm();
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm);
		EasyMock.verify(applicationsServiceMock);
		assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
	}
	
	
	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);

		withdrawController = new WithdrawController(applicationsServiceMock);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(student);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
