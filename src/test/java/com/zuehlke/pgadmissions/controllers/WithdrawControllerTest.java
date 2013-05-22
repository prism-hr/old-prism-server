package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotTerminateApplicationException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WithdrawService;

public class WithdrawControllerTest {
	
	private WithdrawController withdrawController;
	private ApplicationsService applicationsServiceMock;
	private WithdrawService withdrawServiceMock;
	private RegisteredUser student;

	private EventFactory eventFactoryMock;
	private UserService userServiceMock;
	private ApplicationFormAccessService accessServiceMock;

	@Test(expected = CannotTerminateApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfInApprovedStage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicant(student).id(2).build();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm, new ModelMap());
	}
	
	@Test(expected = CannotTerminateApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfInRejectStage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).applicant(student).id(2).build();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm, new ModelMap());
	}
	
	@Test
	public void shouldThrowCannotWithdrawApplicationExceptionIfInUnsubmittedStage() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).applicant(student).id(2).applicationNumber("abc").build();
		withdrawServiceMock.saveApplicationFormAndSendMailNotifications(applicationForm);
		
		StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.WITHDRAWN)).andReturn(event);
		withdrawServiceMock.sendToPortico(applicationForm);
		
		EasyMock.replay(withdrawServiceMock, eventFactoryMock);
		
		String view = withdrawController.withdrawApplicationAndGetApplicationList(applicationForm, new ModelMap());
		
		EasyMock.verify(withdrawServiceMock);
		assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
		assertEquals("redirect:/applications?messageCode=application.withdrawn&application=abc", view);
		
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(event, applicationForm.getEvents().get(0));
	}
	
	@Test(expected = CannotTerminateApplicationException.class)
	public void shouldThrowCannotWithdrawApplicationExceptionIfAlreadyWithdrawn() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).applicant(student).id(2).build();
		withdrawController.withdrawApplicationAndGetApplicationList(applicationForm, new ModelMap());
	}
	
	@Test
	public void shouldChangeStatusToWithdrawnAndSaveAndSendEmailsNotifications() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicant(student).id(2).applicationNumber("abc").build();
		withdrawServiceMock.saveApplicationFormAndSendMailNotifications(applicationForm);
		
		StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.WITHDRAWN)).andReturn(event);
		withdrawServiceMock.sendToPortico(applicationForm);
		
		EasyMock.replay(withdrawServiceMock, eventFactoryMock);
		
		String view = withdrawController.withdrawApplicationAndGetApplicationList(applicationForm, new ModelMap());
		
		EasyMock.verify(withdrawServiceMock);
		assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
		assertEquals("redirect:/applications?messageCode=application.withdrawn&application=abc", view);
		
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(event, applicationForm.getEvents().get(0));
	}
	
    @Test
    public void shouldChangeStatusToWithdrawnAndSetWithdrawnBeforeSubmitToTrue() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).applicant(student).id(2).applicationNumber("abc").build();
        withdrawServiceMock.saveApplicationFormAndSendMailNotifications(applicationForm);
        
        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
        EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.WITHDRAWN)).andReturn(event);
        withdrawServiceMock.sendToPortico(applicationForm);
        
        EasyMock.replay(withdrawServiceMock, eventFactoryMock);
        
        String view = withdrawController.withdrawApplicationAndGetApplicationList(applicationForm, new ModelMap());
        
        EasyMock.verify(withdrawServiceMock);
        assertTrue(applicationForm.getWithdrawnBeforeSubmit());
        assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
        assertEquals("redirect:/applications?messageCode=application.withdrawn&application=abc", view);
        
        assertEquals(1, applicationForm.getEvents().size());
        assertEquals(event, applicationForm.getEvents().get(0));
    }	

	@Test
	public void shouldGetApplicationForm() {
		String applicationNumber = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("abc")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.reset(userServiceMock);
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock,currentUserMock);
		ApplicationForm returnedForm = withdrawController.getApplicationForm(applicationNumber);
		assertEquals(applicationForm, returnedForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfNotApplciationFound() {
		String applicationNumber = "abc";		
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("abc")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		withdrawController.getApplicationForm(applicationNumber);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserCannotSeeApplicationForm() {
		String applicationNumber = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("abc")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock);
		EasyMock.reset(userServiceMock);
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(false);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock,currentUserMock);
		withdrawController.getApplicationForm(applicationNumber);
		
	}
	
	@Before
	public void setUp() {

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		withdrawServiceMock = EasyMock.createMock(WithdrawService.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
		withdrawController = new WithdrawController(applicationsServiceMock,userServiceMock,  withdrawServiceMock,eventFactoryMock, accessServiceMock);

		
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student).anyTimes();
		EasyMock.replay(userServiceMock);

	}

	
}
