package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProjectException;
import com.zuehlke.pgadmissions.services.ApplicationFormCreationService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationFormControllerTest {

    private ProgramDAO programDAOMock;
    private ApplicationFormController applicationController;
    private ApplicationForm applicationForm;
    private ApplicationFormCreationService applicationFormCreationServiceMock;
    private RegisteredUser student;
    private ProgramInstanceDAO programInstanceDAOMock;
    private UserService userServiceMock;
    private ProgramsService programsServiceMock;

    @Test
    public void shouldCreateNewApplicationFormWithProgramProjectAndUserFromSecurityContext() throws ParseException {
        Program program = new ProgramBuilder().id(12).title("Program 1").enabled(true).build();
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(student, program, null)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationFormCreationServiceMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", null);
        EasyMock.verify(programDAOMock, applicationFormCreationServiceMock, programInstanceDAOMock);
    }

    @Test
    public void shouldCreateNewApplicationFormWithProject() throws ParseException {
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(parseDate("2030/08/06")).build();
        Program program = new ProgramBuilder().id(12).enabled(true).build();
        program.setInstances(Arrays.asList(programInstance));
        RegisteredUser primarySupervisor = new RegisteredUserBuilder().id(1).firstName("first").lastName("last").email("primary@supervisor.com").build();
        Advert advert = new AdvertBuilder().id(1).title("title").build();
        Project project = new ProjectBuilder().id(1).primarySupervisor(primarySupervisor).program(program).advert(advert).build();
        Integer projectId = 1;

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(programsServiceMock.getProject(projectId)).andReturn(project);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(student, program, project)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, applicationFormCreationServiceMock, programInstanceDAOMock, programsServiceMock);

        applicationController.createNewApplicationForm("ABC", projectId);
        EasyMock.verify(applicationFormCreationServiceMock);

    }

    @Test
    public void shouldRedirectToApplicationFormView() throws ParseException {
        Program program = new ProgramBuilder().id(12).enabled(true).title("Program 1").build();
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).build();
        program.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(student, program, null)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
        EasyMock.replay(programDAOMock, applicationFormCreationServiceMock, programInstanceDAOMock);

        ModelAndView modelAndView = applicationController.createNewApplicationForm("ABC", null);
        assertEquals(applicationForm.getApplicationNumber(), modelAndView.getModel().get("applicationId"));
        assertEquals("redirect:/application", modelAndView.getViewName());

    }

    @Test(expected = CannotApplyToProgramException.class)
    public void shouldReturnProgramDoesNotExistPageIfProgramDoesNotExists() throws ParseException {
        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);

        EasyMock.replay(programDAOMock);
        applicationController.createNewApplicationForm("ABC", null);
    }

    @Test(expected = CannotApplyToProgramException.class)
    public void shouldReturnProgramDoesNotExistPageIfProgramIsDisabled() throws ParseException {
        Program program1 = new ProgramBuilder().id(12).enabled(false).build();
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(parseDate("2030/08/06")).build();
        program1.setInstances(Arrays.asList(programInstance));

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program1)).andReturn(null);

        EasyMock.replay(programDAOMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", null);
    }

    @Test(expected = CannotApplyToProgramException.class)
    public void shouldReturnProgramDoesNotExistPageIfProgramExistsButDoesntHaveAnyActiveInstances() throws ParseException {
        Program program = new ProgramBuilder().id(12).enabled(true).build();

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(null);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(null);

        EasyMock.replay(programDAOMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", null);
    }

    @Test(expected = CannotApplyToProjectException.class)
    public void shouldThrowExceptionIfProjectAdvertIsNotActive() throws ParseException {
        ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
                .applicationDeadline(parseDate("2030/08/06")).build();
        Program program = new ProgramBuilder().id(12).enabled(true).build();
        program.setInstances(Arrays.asList(programInstance));
        RegisteredUser primarySupervisor = new RegisteredUserBuilder().id(1).firstName("first").lastName("last").email("primary@supervisor.com").build();
        Advert advert = new AdvertBuilder().id(1).title("title").active(false).build();
        Project project = new ProjectBuilder().id(1).primarySupervisor(primarySupervisor).program(program).advert(advert).build();
        Integer projectId = 1;

        EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
        EasyMock.expect(programsServiceMock.getProject(projectId)).andReturn(project);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(student, program, project)).andReturn(applicationForm);
        EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();

        EasyMock.replay(programDAOMock, programsServiceMock, applicationFormCreationServiceMock, programInstanceDAOMock);
        applicationController.createNewApplicationForm("ABC", projectId);
    }

    @Test(expected = CannotApplyToProjectException.class)
    public void shouldThrowExceptionIfProjectIsDisabled() throws ParseException {
    	ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
    			.applicationDeadline(parseDate("2030/08/06")).build();
    	Program program = new ProgramBuilder().id(12).enabled(true).build();
    	program.setInstances(Arrays.asList(programInstance));
    	RegisteredUser primarySupervisor = new RegisteredUserBuilder().id(1).firstName("first").lastName("last").email("primary@supervisor.com").build();
    	Advert advert = new AdvertBuilder().id(1).title("title").active(false).build();
    	Project project = new ProjectBuilder().id(1).primarySupervisor(primarySupervisor).program(program).advert(advert).disabled(true).build();
    	Integer projectId = 1;
    	
    	EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
    	EasyMock.expect(programsServiceMock.getProject(projectId)).andReturn(project);
    	EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(student, program, project)).andReturn(applicationForm);
    	EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
    	
    	EasyMock.replay(programDAOMock, programsServiceMock, applicationFormCreationServiceMock, programInstanceDAOMock);
    	applicationController.createNewApplicationForm("ABC", projectId);
    }

    @Test(expected = CannotApplyToProjectException.class)
    public void shouldThrowExceptionIfProjectNotExists() throws ParseException {
    	ProgramInstance programInstance = new ProgramInstanceBuilder().id(1).studyOption("Full-time").studyOptionCode("1")
    			.applicationDeadline(parseDate("2030/08/06")).build();
    	Program program = new ProgramBuilder().id(12).enabled(true).build();
    	program.setInstances(Arrays.asList(programInstance));
    	RegisteredUser primarySupervisor = new RegisteredUserBuilder().id(1).firstName("first").lastName("last").email("primary@supervisor.com").build();
    	Advert advert = new AdvertBuilder().id(1).title("title").active(false).build();
    	Project project = new ProjectBuilder().id(1).primarySupervisor(primarySupervisor).program(program).advert(advert).disabled(true).build();
    	Integer projectId = 1;
    	
    	EasyMock.expect(programDAOMock.getProgramByCode("ABC")).andReturn(program);
    	EasyMock.expect(programsServiceMock.getProject(projectId)).andReturn(null);
    	EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(student, program, project)).andReturn(applicationForm);
    	EasyMock.expect(programInstanceDAOMock.getActiveProgramInstances(program)).andReturn(Arrays.asList(programInstance)).anyTimes();
    	
    	EasyMock.replay(programDAOMock, programsServiceMock, applicationFormCreationServiceMock, programInstanceDAOMock);
    	applicationController.createNewApplicationForm("ABC", projectId);
    }

    @Before
    public void setUp() {
        applicationForm = new ApplicationFormBuilder().id(1).applicationNumber("ABC").build();

        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        applicationFormCreationServiceMock = EasyMock.createMock(ApplicationFormCreationService.class);
        programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        programsServiceMock = EasyMock.createMock(ProgramsService.class);

        applicationController = new ApplicationFormController(programDAOMock, applicationFormCreationServiceMock, programInstanceDAOMock, userServiceMock,
                programsServiceMock);

        student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
                .role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student).anyTimes();
        EasyMock.replay(userServiceMock);
    }

    private Date parseDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.parse(date);
    }
}
