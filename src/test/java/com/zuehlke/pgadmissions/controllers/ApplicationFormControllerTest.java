package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.services.ApplicationFormCreationService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationFormControllerTest {

    private ApplicationFormController applicationController;
    private ApplicationFormCreationService applicationFormCreationServiceMock;
    private UserService userServiceMock;
    private ProgramsService programsServiceMock;

    @Test(expected = CannotApplyException.class)
    public void shouldThrowCannotApplyErrorIfProgramOrProjectDoNotExist() throws ParseException {
        EasyMock.expect(programsServiceMock.getValidProgramProjectAdvert(null, null)).andThrow(new CannotApplyException());
        EasyMock.replay(programsServiceMock);
        applicationController.createNewApplicationForm(null, null);
    }
    
    public void shouldCreateNewApplicationIfProgramIsActive() throws ParseException {
        ApplicationForm applicationForm = new ApplicationForm();
        Program program = new Program();
        RegisteredUser registeredUser = new RegisteredUser();
        EasyMock.expect(programsServiceMock.getValidProgramProjectAdvert(null, null)).andReturn(program);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(registeredUser);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(registeredUser, program)).andReturn(applicationForm);
        EasyMock.replay(applicationFormCreationServiceMock, userServiceMock, programsServiceMock);
        applicationController.createNewApplicationForm(program.getCode(), null);
    }
    
    public void shouldCreateNewApplicationIfProjectIsActive() throws ParseException {
        ApplicationForm applicationForm = new ApplicationForm();
        Project project = new Project();
        RegisteredUser registeredUser = new RegisteredUser();
        EasyMock.expect(programsServiceMock.getValidProgramProjectAdvert(null, null)).andReturn(project);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(registeredUser);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(registeredUser, project)).andReturn(applicationForm);
        EasyMock.replay(applicationFormCreationServiceMock, userServiceMock, programsServiceMock);
        applicationController.createNewApplicationForm(null, project.getId());
    }

    @Before
    public void setUp() {
        applicationFormCreationServiceMock = EasyMock.createMock(ApplicationFormCreationService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        programsServiceMock = EasyMock.createMock(ProgramsService.class);
        applicationController = new ApplicationFormController(applicationFormCreationServiceMock, programsServiceMock, userServiceMock);
    }

}
