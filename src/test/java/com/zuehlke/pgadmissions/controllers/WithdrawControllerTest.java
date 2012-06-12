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
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotWithdrawApplicationException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.WithdrawService;
import com.zuehlke.pgadmissions.utils.EventFactory;

public class WithdrawControllerTest {
	
	private WithdrawController withdrawController;
	private ApplicationsService applicationsServiceMock;
	private WithdrawService withdrawServiceMock;
	private RegisteredUser student;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private EventFactory eventFactoryMock;

	@Test(expected = CannotWithdrawApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfInApprovedStage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicant(student).id(2).toApplicationForm();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm);
	}
	
	@Test(expected = CannotWithdrawApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfInRejectStage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).applicant(student).id(2).toApplicationForm();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm);
	}
	
	@Test(expected = CannotWithdrawApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfInUnsubmittedStage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).applicant(student).id(2).toApplicationForm();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm);
	}
	
	@Test(expected = CannotWithdrawApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfAlreadyWithdrawn() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).applicant(student).id(2).toApplicationForm();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm);
	}
	
	@Test
	public void shouldChangeStatusToWithdrawnAndSaveAndSendEmailsNotifications() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicant(student).id(2).toApplicationForm();
		withdrawServiceMock.saveApplicationFormAndSendMailNotifications(applicationForm);
		
		StateChangeEvent event = new StateChangeEventBuilder().id(1).toEvent();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.WITHDRAWN)).andReturn(event);
		
		EasyMock.replay(withdrawServiceMock, eventFactoryMock);
		
		String view = withdrawController.withdrawApplicationAndGetApplicationList(applicationForm);
		
		EasyMock.verify(withdrawServiceMock);
		assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
		assertEquals("redirect:/applications", view);
		
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(event, applicationForm.getEvents().get(0));
		
	}
	
	
	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		withdrawServiceMock = EasyMock.createMock(WithdrawService.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		
		withdrawController = new WithdrawController(applicationsServiceMock, withdrawServiceMock,eventFactoryMock);

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
