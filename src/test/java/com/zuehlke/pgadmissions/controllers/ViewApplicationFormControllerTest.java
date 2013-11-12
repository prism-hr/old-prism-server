package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ViewApplicationFormControllerTest {

    private ViewApplicationFormController controller;
    private RegisteredUser userMock;
    private ApplicationsService applicationsServiceMock;
    private ApplicationPageModelBuilder applicationPageModelBuilderMock;
    private UserService userServiceMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getViewApplicationPage(null, "1", null, null, null);
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfCurrentCannotSeeApplicatioForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(applicationsServiceMock, userMock);

        controller.getViewApplicationPage(null, "1", null, null, null);
    }

    @Test
    public void shouldGetApplicationFormViewWithApplicationPageModelForApplicationApplicant() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).build();
        String uploadErrorCode = "abc";
        String view = "def";
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(userMock.getId()).andReturn(99).anyTimes();
        ApplicationPageModel model = new ApplicationPageModel();

        EasyMock.expect(applicationPageModelBuilderMock.createAndPopulatePageModel(applicationForm, uploadErrorCode, view, null, null)).andReturn(model);
        applicationFormUserRoleServiceMock.deregisterApplicationUpdate(applicationForm, userMock);

        EasyMock.replay(applicationsServiceMock, userMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        ModelAndView modelAndView = controller.getViewApplicationPage(view, "1", uploadErrorCode, null, null);
        EasyMock.verify(applicationsServiceMock, userMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        
        assertEquals("private/pgStudents/form/main_application_page", modelAndView.getViewName());
        assertEquals(model, modelAndView.getModel().get("model"));
    }

    @Test
    public void shouldGetAdminApplicationFormViewWithApplicationPageModelForApplicationApplicantOfEndStateApplicationFOrm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(userMock).status(ApplicationFormStatus.REJECTED).build();
        String uploadErrorCode = "abc";
        String view = "def";
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(userMock.getId()).andReturn(99).anyTimes();
        ApplicationPageModel model = new ApplicationPageModel();

        EasyMock.expect(applicationPageModelBuilderMock.createAndPopulatePageModel(applicationForm, uploadErrorCode, view, null, null)).andReturn(model);
        applicationFormUserRoleServiceMock.deregisterApplicationUpdate(applicationForm, userMock);

        EasyMock.replay(applicationsServiceMock, userMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        ModelAndView modelAndView = controller.getViewApplicationPage(view, "1", uploadErrorCode, null, null);
        EasyMock.verify(applicationsServiceMock, userMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        
        assertEquals("private/staff/application/main_application_page", modelAndView.getViewName());
        assertEquals(model, modelAndView.getModel().get("model"));
    }

    @Test
    public void shouldGetApplicationFormViewWithApplicationPageModelForStaff() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().id(100).build()).build();
        String uploadErrorCode = "abc";
        String view = "def";
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(userMock.getId()).andReturn(99).anyTimes();

        ApplicationPageModel model = new ApplicationPageModel();
        EasyMock.expect(applicationPageModelBuilderMock.createAndPopulatePageModel(applicationForm, uploadErrorCode, view, null, null)).andReturn(model);
        applicationFormUserRoleServiceMock.deregisterApplicationUpdate(applicationForm, userMock);
        
        EasyMock.replay(applicationsServiceMock, userMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        ModelAndView modelAndView = controller.getViewApplicationPage(view, "1", uploadErrorCode, null, null);
        EasyMock.verify(applicationsServiceMock, userMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        
        assertEquals("private/staff/application/main_application_page", modelAndView.getViewName());
        assertEquals(model, modelAndView.getModel().get("model"));
    }

    @Before
    public void setUp() {
        userMock = EasyMock.createMock(RegisteredUser.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        applicationPageModelBuilderMock = EasyMock.createMock(ApplicationPageModelBuilder.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        controller = new ViewApplicationFormController(applicationsServiceMock, userServiceMock, applicationPageModelBuilderMock, applicationFormUserRoleServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(userMock).anyTimes();
        EasyMock.replay(userServiceMock);
    }
}
