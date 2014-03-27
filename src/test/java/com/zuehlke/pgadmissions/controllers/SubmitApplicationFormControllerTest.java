package com.zuehlke.pgadmissions.controllers;

import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.EventFactory;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.StageDurationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

public class SubmitApplicationFormControllerTest {

    private SubmitApplicationFormController applicationController;
    private ApplicationFormService applicationsServiceMock;
    private ApplicationFormTransferService applicationFormTransferServiceMock;
    private ApplicationFormValidator applicationFormValidatorMock;
    private StageDurationService stageDurationServiceMock;
    private EventFactory eventFactoryMock;
    private UserService userServiceMock;
    private MockHttpServletRequest httpServletRequestMock;
    private ActionsProvider actionsProviderMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private ProgramService programsService;
    private RegisteredUser student;
    private RegisteredUser admin;

    @Test
    public void shouldReturnCurrentUser() {
        assertEquals(student, applicationController.getCurrentUser());
    }

    @Test
    public void shouldReturnStudenApplicationViewOnGetForApplicantOfApplciation() {
        Program program = new ProgramBuilder().id(1).build();
        ProgramInstance instance = new ProgramInstanceBuilder().program(program).applicationDeadline(DateUtils.addMonths(new Date(), 1)).enabled(true).build();
        program.getInstances().add(instance);
        List<ProgramInstance> referenceInstanceList = new ArrayList<ProgramInstance>();
        referenceInstanceList.add(instance);

        String view = applicationController.getApplicationView(null, new ApplicationFormBuilder().applicant(student).id(1).advert(program).build());

        assertEquals("/private/pgStudents/form/main_application_page", view);
    }

    @Test
    public void shouldReturnAdminApplicationViewOnGetForApplicantButNotOfApplication() {
        String view = applicationController.getApplicationView(null, new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().id(6).build()).id(1)
                .advert(new ProgramBuilder().id(1).build()).build());
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnAdminApplicationViewOnGetForApplicantForEndStateApp() {

        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);

        String view = applicationController.getApplicationView(null,
                new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).id(1).advert(new ProgramBuilder().id(1).build()).applicant(student)
                        .build());
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnEditableApplicationViewOnGetForProgrammeAdministrator() {
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).applicationNumber("abc").advert(program)
                .applicant(student).build();

        reset(userServiceMock, actionsProviderMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        actionsProviderMock.validateAction(applicationForm, admin, ApplicationFormAction.VIEW);
        EasyMock.expectLastCall().once();
        expect(actionsProviderMock.checkActionAvailable(applicationForm, admin, ApplicationFormAction.VIEW_EDIT)).andReturn(true).once();
        replay(userServiceMock, actionsProviderMock);

        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("redirect:/editApplicationFormAsProgrammeAdmin?applicationId=abc", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsNotSubmitted() {
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).id(1).advert(program).applicant(student)
                .build();

        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);

        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsDecided() {
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(1).advert(program).applicant(student).build();

        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);

        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsWithdrawn() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).id(1).applicant(student).build();

        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);

        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldNotReturnEditableApplicationViewOnGetForProgrammeAdministratorIfApplicationIsInValidation() {
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).advert(program).applicant(student)
                .build();

        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);

        String view = applicationController.getApplicationView(null, applicationForm);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnStudenApplicationViewOnGetForNonApplicant() {
        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);
        String view = applicationController.getApplicationView(null,
                new ApplicationFormBuilder().id(1).advert(new ProgramBuilder().id(1).build()).applicant(student).build());
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnStudenApplicationViewWithoutHeaders() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).build();
        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        replay(userServiceMock);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getParameter("embeddedApplication")).andReturn("true");
        expect(request.getParameter("embeddedApplication")).andReturn("true");
        replay(request);
        String view = applicationController.getApplicationView(request, applicationForm);
        assertEquals("/private/staff/application/main_application_page_without_headers", view);
    }

    @Test
    public void shouldReturnToApplicationViewIfErrors() {
        BindingResult errorsMock = createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).build();
        expect(errorsMock.hasErrors()).andReturn(true);
        expect(errorsMock.getFieldError("program")).andReturn(null);

        replay(errorsMock);
        String view = applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        verify(errorsMock);

        assertEquals("/private/pgStudents/form/main_application_page", view);
    }

    @Test(expected = CannotApplyException.class)
    public void shouldThrowCannotApplyToProgramExceptionIfNotAvailable() {
        BindingResult errorsMock = createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).build();
        expect(errorsMock.hasErrors()).andReturn(true);
        expect(errorsMock.getFieldError("program")).andReturn(new FieldError("applicationForm", "application.program.invalid", null));

        replay(errorsMock);
        applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        verify(errorsMock);
    }

    @Test
    public void shouldChangeStatusToValidateAndSaveIfNoErrors() {
        BindingResult errorsMock = createMock(BindingResult.class);

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).build();
        expect(errorsMock.hasErrors()).andReturn(false);

        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
        expect(eventFactoryMock.createEvent(ApplicationFormStatus.VALIDATION)).andReturn(event);

        applicationsServiceMock.sendSubmissionConfirmation(applicationForm);

        Date batchDeadline = new DateTime(2012, 1, 1, 0, 0).toDate();
        expect(applicationsServiceMock.getBatchDeadlineForApplication(applicationForm)).andReturn(batchDeadline);

        StageDuration validationDuration = new StageDurationBuilder().duration(1).stage(ApplicationFormStatus.VALIDATION).unit(DurationUnitEnum.WEEKS).build();
        expect(stageDurationServiceMock.getById(ApplicationFormStatus.VALIDATION)).andReturn(validationDuration);
        applicationFormUserRoleServiceMock.applicationSubmitted(applicationForm);
        applicationFormUserRoleServiceMock.insertApplicationUpdate(applicationForm, userServiceMock.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);

        replay(applicationsServiceMock, errorsMock, stageDurationServiceMock, eventFactoryMock, applicationFormUserRoleServiceMock);
        applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        verify(applicationsServiceMock, errorsMock, stageDurationServiceMock, eventFactoryMock, applicationFormUserRoleServiceMock);

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
        BindingResult errorsMock = createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).applicationNumber("abc").build();
        expect(errorsMock.hasErrors()).andReturn(false);
        StageDuration stageDuration = new StageDuration();
        stageDuration.setDuration(1);
        stageDuration.setUnit(DurationUnitEnum.DAYS);
        Date batchDeadline = new DateTime(2012, 1, 1, 0, 0).toDate();
        expect(applicationsServiceMock.getBatchDeadlineForApplication(applicationForm)).andReturn(batchDeadline);
        expect(stageDurationServiceMock.getById(ApplicationFormStatus.VALIDATION)).andReturn(stageDuration);
        applicationsServiceMock.sendSubmissionConfirmation(applicationForm);
        applicationFormUserRoleServiceMock.applicationSubmitted(applicationForm);
        applicationFormUserRoleServiceMock.insertApplicationUpdate(applicationForm, userServiceMock.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);

        replay(applicationsServiceMock, errorsMock, stageDurationServiceMock, applicationFormUserRoleServiceMock);
        String view = applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        verify(applicationsServiceMock, errorsMock, stageDurationServiceMock, applicationFormUserRoleServiceMock);

        assertEquals("redirect:/applications?messageCode=application.submitted&application=abc", view);
    }

    @Test
    public void shouldSaveRequestIp() throws UnknownHostException {
        BindingResult errorsMock = createMock(BindingResult.class);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(student).applicationNumber("abc").build();
        expect(errorsMock.hasErrors()).andReturn(false);
        StageDuration stageDuration = new StageDuration();
        stageDuration.setDuration(1);
        stageDuration.setUnit(DurationUnitEnum.DAYS);
        Date batchDeadline = new DateTime(2012, 1, 1, 0, 0).toDate();
        expect(applicationsServiceMock.getBatchDeadlineForApplication(applicationForm)).andReturn(batchDeadline);
        expect(stageDurationServiceMock.getById(ApplicationFormStatus.VALIDATION)).andReturn(stageDuration);
        applicationsServiceMock.sendSubmissionConfirmation(applicationForm);

        replay(applicationsServiceMock, errorsMock, stageDurationServiceMock);
        applicationController.submitApplication(applicationForm, errorsMock, httpServletRequestMock);
        verify(applicationsServiceMock, errorsMock, stageDurationServiceMock);

        assertEquals(httpServletRequestMock.getRemoteAddr(), applicationForm.getIpAddressAsString());
    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = createMock(WebDataBinder.class);
        binderMock.setValidator(applicationFormValidatorMock);
        replay(binderMock);
        applicationController.registerValidator(binderMock);
        verify(binderMock);
    }

    @Test
    public void shouldGetApplicationFormFromService() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.UNSUBMITTED).applicant(student).build();
        expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(applicationForm);
        replay(applicationsServiceMock);
        ApplicationForm returnedApplicationForm = applicationController.getApplicationForm("2");
        assertEquals(applicationForm, returnedApplicationForm);

    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfOtherApplicant() {
        RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        reset(userServiceMock);
        expect(userServiceMock.getCurrentUser()).andReturn(otherApplicant).anyTimes();
        replay(userServiceMock);

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(student).id(2).build();
        applicationController.submitApplication(applicationForm, null, httpServletRequestMock);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
        expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(null);
        replay(applicationsServiceMock);
        applicationController.getApplicationForm("2");
    }

    @Test
    public void shouldSetValidationDateAfterOneWorkingDayOfBatchDeadlineIfBatchDeadlineIsSetAndValidationStageDurationIsOneDay() throws ParseException {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/12/12")).build();
        StageDuration stageDurationMock = createMock(StageDuration.class);
        expect(stageDurationServiceMock.getById(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationMock);
        expect(stageDurationMock.getUnit()).andReturn(DurationUnitEnum.DAYS);
        expect(stageDurationMock.getDurationInMinutes()).andReturn(1440);
        replay(stageDurationServiceMock, stageDurationMock);
        applicationController.assignValidationDueDate(applicationForm);
        Date oneDayMore = new SimpleDateFormat("yyyy/MM/dd").parse("2012/12/14");
        Assert.assertEquals(String.format("Dates are not the same [%s] [%s]", oneDayMore, applicationForm.getDueDate()), oneDayMore,
                applicationForm.getDueDate());
    }

    @Test
    public void shouldSetValidationDateToCurrentDatePlusValidationStageIntervalWorkingDayIfBatchDeadlineIsNotSet() throws ParseException {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED).build();
        StageDuration stageDurationMock = createMock(StageDuration.class);
        expect(stageDurationServiceMock.getById(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationMock);
        expect(stageDurationMock.getDurationInMinutes()).andReturn(1440);

        replay(stageDurationServiceMock, stageDurationMock);
        applicationController.assignValidationDueDate(applicationForm);
        verify(stageDurationServiceMock, stageDurationMock);

        Date dayAfterTomorrow = com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(new Date(), 1440);
        Assert.assertTrue(String.format("Dates are not on the same day [%s] [%s]", dayAfterTomorrow, applicationForm.getDueDate()),
                DateUtils.isSameDay(dayAfterTomorrow, applicationForm.getDueDate()));
    }

    @Before
    public void setUp() {
        applicationsServiceMock = createMock(ApplicationFormService.class);
        applicationFormTransferServiceMock = createMock(ApplicationFormTransferService.class);
        userServiceMock = createMock(UserService.class);
        applicationFormValidatorMock = createMock(ApplicationFormValidator.class);
        stageDurationServiceMock = createMock(StageDurationService.class);
        eventFactoryMock = createMock(EventFactory.class);
        actionsProviderMock = createMock(ActionsProvider.class);
        applicationFormUserRoleServiceMock = createMock(ApplicationFormUserRoleService.class);
        programsService = createMock(ProgramService.class);

        applicationController = new SubmitApplicationFormController(applicationsServiceMock, applicationFormTransferServiceMock, userServiceMock, applicationFormValidatorMock,
                stageDurationServiceMock, eventFactoryMock, actionsProviderMock, applicationFormUserRoleServiceMock, programsService);
        httpServletRequestMock = new MockHttpServletRequest();

        student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
                .role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        admin = new RegisteredUserBuilder().id(2).username("Francishek").email("franek@gmail.com").firstName("Franek").lastName("Pieczka")
                .role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();

        expect(userServiceMock.getCurrentUser()).andReturn(student).anyTimes();
        replay(userServiceMock);
    }
}