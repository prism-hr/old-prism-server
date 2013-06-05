package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.StageDurationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

public class SubmitApplicationFormControllerTest {

    private SubmitApplicationFormController applicationController;

    private ApplicationsService applicationsServiceMock;

    private RegisteredUser student;

    private ApplicationFormValidator applicationFormValidatorMock;

    private StageDurationService stageDurationServiceMock;

    private EventFactory eventFactoryMock;

    private UserService userServiceMock;

    private MockHttpServletRequest httpServletRequestMock;
    
    private ApplicationFormAccessService accessServiceMock;
    
    private ActionsProvider actionsProviderMock;
    
    @Test
    public void shouldReturnCurrentUser() {
        assertEquals(student, applicationController.getUser());
    }

    @Test
    public void shouldReturnStudenApplicationViewOnGetForApplicantOfApplciation() {
        String view = applicationController.getApplicationView(null,
                new ApplicationFormBuilder().applicant(student).id(1).program(new ProgramBuilder().id(1).build()).build());
        assertEquals("/private/pgStudents/form/main_application_page", view);
    }

    @Test
    public void shouldReturnAdminApplicationViewOnGetForApplicantButNotOfApplication() {
        String view = applicationController.getApplicationView(null, new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().id(6).build()).id(1)
                .program(new ProgramBuilder().id(1).build()).build());
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnAdminApplicationViewOnGetForApplicantForEndStateApp() {
        String view = applicationController.getApplicationView(null,
                new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).id(1).program(new ProgramBuilder().id(1).build()).build());
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnEditableApplicationViewOnGetForProgrammeAdministrator() {
        student.getRoles().add(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build());
        Program program = new ProgramBuilder().id(1).administrators(student).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).applicationNumber("abc").program(program)
                .build();
        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("redirect:/editApplicationFormAsProgrammeAdmin?applicationId=abc", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsNotSubmitted() {
        Program program = new ProgramBuilder().id(1).administrators(student).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).id(1).program(program).build();
        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsDecided() {
        Program program = new ProgramBuilder().id(1).administrators(student).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(1).program(program).build();
        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsWithdrawn() {
        Program program = new ProgramBuilder().id(1).administrators(student).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).id(1).program(program).build();
        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsInValidation() {
        Program program = new ProgramBuilder().id(1).administrators(student).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).program(program).build();
        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnStudenApplicationViewOnGetForNonApplicant() {
        RegisteredUser otherUser = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(otherUser).anyTimes();
        EasyMock.replay(userServiceMock);
        String view = applicationController.getApplicationView(null, new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).build()).build());
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnStudenApplicationViewWithoutHeaders() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().build();
        RegisteredUser otherUser = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(otherUser).anyTimes();
        EasyMock.replay(userServiceMock);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(request.getParameter("embeddedApplication")).andReturn("true");
        EasyMock.expect(request.getParameter("embeddedApplication")).andReturn("true");
        EasyMock.replay(request);
        String view = applicationController.getApplicationView(request, applicationForm);
        assertEquals("/private/staff/application/main_application_page_without_headers", view);
    }

    @Test
    public void shouldReturnToApplicationViewIfErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);
        String view = applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        assertEquals("/private/pgStudents/form/main_application_page", view);
    }

    @Test
    public void shouldChangeStatusToValidateAndSaveIfNoErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
        EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.VALIDATION)).andReturn(event);
        
        applicationsServiceMock.sendSubmissionConfirmationToApplicant(applicationForm);
        
        Date batchDeadline = new DateTime(2012, 1, 1, 0, 0).toDate();
        EasyMock.expect(applicationsServiceMock.getBatchDeadlineForApplication(applicationForm)).andReturn(batchDeadline);

        StageDuration validationDuration = new StageDurationBuilder()
                .duration(1)
                .stage(ApplicationFormStatus.VALIDATION)
                .unit(DurationUnitEnum.WEEKS)
                .build();
        EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(validationDuration);
        
        EasyMock.replay(applicationsServiceMock, errorsMock, stageDurationServiceMock, eventFactoryMock);

        applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);

        EasyMock.verify(applicationsServiceMock, stageDurationServiceMock);
        assertEquals(ApplicationFormStatus.VALIDATION, applicationForm.getStatus());

        assertEquals(batchDeadline, applicationForm.getBatchDeadline());
        Date submittedDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date dueDate = com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(submittedDate, validationDuration.getDurationInMinutes());
        assertEquals(0, dueDate.compareTo(applicationForm.getDueDate()));
        
        assertEquals(1, applicationForm.getEvents().size());
        assertNotNull(applicationForm.getDueDate());
        assertEquals(event, applicationForm.getEvents().get(0));
    }

    @Test
    public void shouldRedirectToAppsViewWithMessageIfNoErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).applicationNumber("abc").build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        StageDuration stageDuration = new StageDuration();
        stageDuration.setDuration(1);
        stageDuration.setUnit(DurationUnitEnum.DAYS);
        Date batchDeadline = new DateTime(2012, 1, 1, 0, 0).toDate();
        EasyMock.expect(applicationsServiceMock.getBatchDeadlineForApplication(applicationForm)).andReturn(batchDeadline);
        EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDuration);
        applicationsServiceMock.sendSubmissionConfirmationToApplicant(applicationForm);
        EasyMock.replay(applicationsServiceMock, errorsMock, stageDurationServiceMock);
        String view = applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        assertEquals("redirect:/applications?messageCode=application.submitted&application=abc", view);
    }

    @Test
    public void shouldSaveRequestIp() throws UnknownHostException {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).applicationNumber("abc").build();
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        StageDuration stageDuration = new StageDuration();
        stageDuration.setDuration(1);
        stageDuration.setUnit(DurationUnitEnum.DAYS);
        Date batchDeadline = new DateTime(2012, 1, 1, 0, 0).toDate();
        EasyMock.expect(applicationsServiceMock.getBatchDeadlineForApplication(applicationForm)).andReturn(batchDeadline);
        EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDuration);
        applicationsServiceMock.sendSubmissionConfirmationToApplicant(applicationForm);
        EasyMock.replay(applicationsServiceMock, errorsMock, stageDurationServiceMock);
        applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        assertEquals(httpServletRequestMock.getRemoteAddr(), applicationForm.getIpAddressAsString());
    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(applicationFormValidatorMock);
        EasyMock.replay(binderMock);
        applicationController.registerValidator(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldGetApplicationFormFromService() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.UNSUBMITTED).applicant(student).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock);
        ApplicationForm returnedApplicationForm = applicationController.getApplicationForm("2");
        assertEquals(applicationForm, returnedApplicationForm);

    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfOtherApplicant() {
        RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(otherApplicant).anyTimes();
        EasyMock.replay(userServiceMock);

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).build();
        applicationController.submitApplication(applicationForm, null, httpServletRequestMock);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("2")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        applicationController.getApplicationForm("2");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowSubmitExceptionIfApplicationIsDecided() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).status(ApplicationFormStatus.APPROVED).build();
        applicationController.submitApplication(applicationForm, null, httpServletRequestMock);
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfUserCannotSeeApplicationForm() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(userMock).anyTimes();
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("3")).andReturn(applicationForm);
        EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(applicationsServiceMock, userMock);

        applicationController.getApplicationForm("3");
    }

    @Test
    public void shouldSetValidationDateAfterOneWorkingDayOfBatchDeadlineIfBatchDeadlineIsSetAndValidationStageDurationIsOneDay() throws ParseException {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/12/12")).build();
        StageDuration stageDurationMock = EasyMock.createMock(StageDuration.class);
        EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationMock);
        EasyMock.expect(stageDurationMock.getUnit()).andReturn(DurationUnitEnum.DAYS);
        EasyMock.expect(stageDurationMock.getDurationInMinutes()).andReturn(1440);
        EasyMock.replay(stageDurationServiceMock, stageDurationMock);
        applicationController.assignValidationDueDate(applicationForm);
        Date oneDayMore = new SimpleDateFormat("yyyy/MM/dd").parse("2012/12/14");
        Assert.assertEquals(String.format("Dates are not the same [%s] [%s]", oneDayMore, applicationForm.getDueDate()), oneDayMore,
                applicationForm.getDueDate());
    }

    @Test
    public void shouldSetValidationDateToCurrentDatePlusValidationStageIntervalWorkingDayIfBatchDeadlineIsNotSet() throws ParseException {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED).build();
        StageDuration stageDurationMock = EasyMock.createMock(StageDuration.class);
        EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationMock);
        EasyMock.expect(stageDurationMock.getUnit()).andReturn(DurationUnitEnum.DAYS);
        EasyMock.expect(stageDurationMock.getDurationInMinutes()).andReturn(1440);
        EasyMock.replay(stageDurationServiceMock, accessServiceMock, stageDurationMock);
        applicationController.assignValidationDueDate(applicationForm);
        Date dayAfterTomorrow = com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(new Date(), 1440);
        Assert.assertTrue(String.format("Dates are not on the same day [%s] [%s]", dayAfterTomorrow, applicationForm.getDueDate()),
                DateUtils.isSameDay(dayAfterTomorrow, applicationForm.getDueDate()));
    }

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);

        applicationFormValidatorMock = EasyMock.createMock(ApplicationFormValidator.class);
        stageDurationServiceMock = EasyMock.createMock(StageDurationService.class);
        eventFactoryMock = EasyMock.createMock(EventFactory.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        applicationController = new SubmitApplicationFormController(applicationsServiceMock, userServiceMock, applicationFormValidatorMock,
                stageDurationServiceMock, eventFactoryMock, accessServiceMock, actionsProviderMock);
        httpServletRequestMock = new MockHttpServletRequest();

        student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
                .role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student).anyTimes();
        EasyMock.replay(userServiceMock);
    }
}
