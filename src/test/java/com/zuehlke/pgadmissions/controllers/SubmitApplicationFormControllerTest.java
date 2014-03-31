package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SubmitApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SubmitApplicationFormControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationsService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormValidator applicationFormValidatorMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private ActionsProvider actionsProviderMock;
    private WorkflowService applicationFormUserRoleServiceMock;
    private ProgramService programsService;

    @Mock
    @InjectIntoByType

    @Mock
    @InjectIntoByType

    @Mock
    @InjectIntoByType
    private SubmitApplicationFormService submitApplicationFormServiceMock;

    @TestedObject
    private SubmitApplicationFormController controller;

    @Test
    public void shouldReturnCurrentUser() {
        RegisteredUser user = new RegisteredUser();
        expect(userServiceMock.getCurrentUser()).andReturn(user);

        replay();
        assertEquals(user, controller.getUser());
    }

    @Test
    public void shouldReturnStudentApplicationViewOnGetForApplicantOfApplication() {
        RegisteredUser applicant = new RegisteredUser();
        ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).program(new ProgramBuilder().code("dupa").build())
                .advert(new AdvertBuilder().id(666).build()).build();

        expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        actionsProviderMock.validateAction(application, applicant, ApplicationFormAction.VIEW);
        applicationFormUserRoleServiceMock.deleteApplicationUpdate(application, applicant);
        expect(programsService.getValidProgramProjectAdvert("dupa", 666)).andReturn(null);

        replay();
        String view = controller.getApplicationView(null, application);

        assertEquals("/private/pgStudents/form/main_application_page", view);
    }

    @Test
    public void shouldReturnAdminApplicationViewOnGet() {
        RegisteredUser applicant = new RegisteredUser();
        ApplicationForm application = new ApplicationFormBuilder().applicant(new RegisteredUserBuilder().id(6).build()).id(1).advert(new ProgramBuilder().id(1).build()).build();
        
        expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        actionsProviderMock.validateAction(application, applicant, ApplicationFormAction.VIEW);
        applicationFormUserRoleServiceMock.deleteApplicationUpdate(application, applicant);
        expect(actionsProviderMock.checkActionAvailable(application, applicant, ApplicationFormAction.VIEW_EDIT)).andReturn(false);
        
        replay();
        String view = controller.getApplicationView(null,
                application);
        assertEquals("/private/staff/application/main_application_page", view);
    }

    @Test
    public void shouldReturnEditableApplicationViewOnGetForProgrammeAdministrator() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin = new RegisteredUserBuilder().id(2).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).applicationNumber("abc").advert(program)
                .applicant(applicant).build();

        expect(userServiceMock.getCurrentUser()).andReturn(admin);
        actionsProviderMock.validateAction(application, admin, ApplicationFormAction.VIEW);
        expect(actionsProviderMock.checkActionAvailable(application, admin, ApplicationFormAction.VIEW_EDIT)).andReturn(true).once();
        applicationFormUserRoleServiceMock.deleteApplicationUpdate(application, admin);
        
        replay();
        String view = controller.getApplicationView(null, application);
        
        assertEquals("redirect:/editApplicationFormAsProgrammeAdmin?applicationId=abc", view);
    }

    @Test
    public void shouldReturnStudentApplicationViewWithoutHeaders() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin = new RegisteredUserBuilder().id(2).build();
        ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("embeddedApplication", "true");

        expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        actionsProviderMock.validateAction(application, admin, ApplicationFormAction.VIEW);
        applicationFormUserRoleServiceMock.deleteApplicationUpdate(application, admin);
        
        replay();
        String view = controller.getApplicationView(request, application);
        
        assertEquals("/private/staff/application/main_application_page_without_headers", view);
    }

    @Test
    public void shouldSubmitApplicationForm() throws UnknownHostException {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(applicant).applicationNumber("abc").build();
        BindingResult bindingResult = new BeanPropertyBindingResult(applicationForm, "applicationForm");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("localhost");

        expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        actionsProviderMock.validateAction(applicationForm, applicant, ApplicationFormAction.COMPLETE_APPLICATION);
        submitApplicationFormServiceMock.submitApplication(applicationForm);

        replay();
        controller.submitApplication(applicationForm, bindingResult, request);

        assertEquals("127.0.0.1", applicationForm.getIpAddressAsString());
    }

    @Test
    public void shouldRegisterValidator() {
        WebDataBinder binderMock = createMock(WebDataBinder.class);
        binderMock.setValidator(applicationFormValidatorMock);
        replay(binderMock);
        controller.registerValidator(binderMock);
        verify(binderMock);
    }

    @Test
    public void shouldGetApplicationFormFromService() {
        ApplicationForm applicationForm = new ApplicationForm();
        expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(applicationForm);
        
        replay();
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("2");
        
        assertEquals(applicationForm, returnedApplicationForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
        expect(applicationsServiceMock.getByApplicationNumber("2")).andReturn(null);
        
        replay();
        controller.getApplicationForm("2");
    }

}
