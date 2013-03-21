package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.InvalidParameterFormatException;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationFormControllerTest {

    private ProgramDAO programDAOMock;
    private ApplicationFormController applicationController;
    private ApplicationForm applicationForm;
    private ApplicationsService applicationsServiceMock;
    private PlainTextUserPropertyEditor userPropertyEditorMock;
    private RegisteredUser student;
    private ProgramInstanceDAO programInstanceDAOMock;
    private UserService userServiceMock;

    @Test
    public void shouldCreateNewApplicationFormWithProgramProjectAndUserFromSecurityContext() throws ParseException {
        Program program = new ProgramBuilder().id(12).title("Program 1").enabled(true).build();
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationsServiceMock.createOrGetUnsubmittedApplicationForm(student, program, null, null, null)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", null, null, null);
        EasyMock.verify(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
    }

    @Test
    public void shouldCreateNewApplicationFormWithBatchDeadlineInFirstAcceptedFormat() throws ParseException {
        Program program = new ProgramBuilder().id(12).enabled(true).build();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date batchDeadline = dateFormat.parse("2012/08/02");
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(dateFormat.parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationsServiceMock.createOrGetUnsubmittedApplicationForm(student, program, batchDeadline, null, null)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", "02-Aug-2012", null, null);
        EasyMock.verify(programDAOMock, applicationsServiceMock, programInstanceDAOMock);
    }

    @Test
    public void shouldCreateNewApplicationFormWithBatchDeadlineInSecondAcceptedFormat() throws ParseException {

        Program program = new ProgramBuilder().id(12).enabled(true).build();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date batchDeadline = dateFormat.parse("2012/08/02");
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(dateFormat.parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationsServiceMock.createOrGetUnsubmittedApplicationForm(student, program, batchDeadline, null, null)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);

        applicationController.createNewApplicationForm("ABC", "02 Aug 2012", null, null);
        EasyMock.verify(applicationsServiceMock);
    }

    @Test(expected = InvalidParameterFormatException.class)
    public void shouldThrowInvalidParameterFormatExceptionIfBatchDeadlinInIncorrectFormat() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Program program = new ProgramBuilder().id(12).enabled(true).build();

        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(dateFormat.parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);

        applicationController.createNewApplicationForm("ABC", "bob", null, null);
    }

    @Test
    public void shouldCreateNewApplicationFormWithProjectTitle() throws ParseException {

        Program program = new ProgramBuilder().id(12).enabled(true).build();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(simpleDateFormat.parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationsServiceMock.createOrGetUnsubmittedApplicationForm(student, program, null, "project title", null))
                .andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);

        applicationController.createNewApplicationForm("ABC", null, "project title", null);
        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldCreateNewApplicationFormWithValidResearchHomePage() throws ParseException {

        Program program = new ProgramBuilder().id(12).enabled(true).build();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(simpleDateFormat.parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        String researchHomePage = "https://www.researchhomepage.com";
        EasyMock.expect(applicationsServiceMock.createOrGetUnsubmittedApplicationForm(student, program, null, null, researchHomePage)).andReturn(
                applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);

        applicationController.createNewApplicationForm("ABC", null, null, researchHomePage);
        EasyMock.verify(applicationsServiceMock);

    }

    @Test
    public void shouldRedirectToApplicationFormView() throws ParseException {
        Program program = new ProgramBuilder().id(12).enabled(true).title("Program 1").build();
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationsServiceMock.createOrGetUnsubmittedApplicationForm(student, program, null, null, null)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
        EasyMock.replay(programDAOMock, applicationsServiceMock, programInstanceDAOMock);

        ModelAndView modelAndView = applicationController.createNewApplicationForm("ABC", null, null, null);
        assertEquals(applicationForm.getApplicationNumber(), modelAndView.getModel().get("applicationId"));
        assertEquals("redirect:/application", modelAndView.getViewName());

    }

    @Test(expected = CannotApplyToProgramException.class)
    public void shouldReturnProgramDoesNotExistPageIfProgramDoesNotExists() throws ParseException {
        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);

        EasyMock.replay(programDAOMock);
        applicationController.createNewApplicationForm("ABC", null, null, null);
    }

    @Test(expected = CannotApplyToProgramException.class)
    public void shouldReturnProgramDoesNotExistPageIfProgramIsDisabled() throws ParseException {
        Program program1 = new ProgramBuilder().id(12).enabled(false).build();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(simpleDateFormat.parse("2030/08/06")).build();
        program1.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program1)).andReturn(null);
        
        EasyMock.replay(programDAOMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", null, null, null);
    }

    @Test(expected = CannotApplyToProgramException.class)
    public void shouldReturnProgramDoesNotExistPageIfProgramExistsButDoesntHaveAnyActiveInstances() throws ParseException {
        Program program = new ProgramBuilder().id(12).enabled(true).build();

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(null);

        EasyMock.replay(programDAOMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", null, null, null);
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.registerCustomEditor(RegisteredUser.class, userPropertyEditorMock);
        EasyMock.replay(binderMock);
        applicationController.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Before
    public void setUp() {
        applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC").build();

        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userPropertyEditorMock = EasyMock.createMock(PlainTextUserPropertyEditor.class);
        programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
        userServiceMock = EasyMock.createMock(UserService.class);

        applicationController = new ApplicationFormController(programDAOMock, applicationsServiceMock, userPropertyEditorMock, programInstanceDAOMock,
                userServiceMock) {
            ApplicationForm newApplicationForm() {
                return applicationForm;
            }
        };

        student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
                .role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student).anyTimes();
        EasyMock.replay(userServiceMock);
    }
}
